package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.view.BaseFragment;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexbaidumap.bean.MapStatusChangeBean;
import org.zywx.wbpalmstar.plugin.uexbaidumap.utils.MLog;

public class EBaiduMapBaseFragment extends BaseFragment implements OnMapClickListener, OnMapStatusChangeListener, OnMapLoadedCallback, OnMapDoubleClickListener, OnMapLongClickListener,
		OnMyLocationClickListener, SnapshotReadyCallback, OnGetGeoCoderResultListener {

	private static final String LTAG = EBaiduMapBaseFragment.class.getSimpleName();
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private UiSettings mUiSettings = null;
	private EBaiduMapOverlayMgr eBaiduMapOverlayMgr = null;
	private EBaiduMapPoiSearch eBaiduMapPoiSearch = null;
	private EBaiduMapBusLineSearch eBaiduMapBusLineSearch = null;
	private EBaiduMapRoutePlanSearch eBaiduMapRoutePlanSearch = null;
	private EUExBaiduMap uexBaseObj;
	private SDKReceiver mSDKReceiver = null;
	// 定位相关
	private LocationClient mLocClient;
	boolean isOneTimeLocation = false;// 是否是一次定位
	boolean isStartDurationLocation = false;// 是否开启持续定位
	private MyLocationListenner myListener = new MyLocationListenner();
	private GeoCoder mGeoCoder = null;
	private float defaultLevel;
	private MapStatusChangeBean changeBean = null;

	private MyOrientationListener myOrientationListener;
	private int mXDirection = 0;
	private LatLng mCenter = null;
	private View overlayView;

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	private class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			MLog.getIns().d("action = " + action);
			if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				jsonSDKReceiverErrorCallback(action);
			} else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				jsonSDKReceiverErrorCallback(action);
			}
		}
	}

	private void jsonSDKReceiverErrorCallback(String errorInfo) {
		if (uexBaseObj != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ERRORINFO, errorInfo);
				String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_SDK_RECEIVER_ERROR + "){" + EBaiduMapUtils.MAP_FUN_ON_SDK_RECEIVER_ERROR + "('" + jsonObject.toString()
						+ "');}";
				uexBaseObj.onCallback(js);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void setStartCenter(LatLng center) {
		this.mCenter = center;
	}

	public void setBaseObj(EUExBaiduMap baseObj) {
		setUexBaseObj(baseObj);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout bg = new FrameLayout(getActivity());

		if (mCenter != null) {
			mMapView = new MapView(getActivity(), new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(mCenter).build()));
		} else {
			mMapView = new MapView(getActivity(), new BaiduMapOptions());
		}
		bg.addView(mMapView);
		overlayView = new View(getActivity());
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		overlayView.setBackgroundColor(Color.WHITE);
		overlayView.setLayoutParams(fl);
		overlayView.setVisibility(View.GONE);
		bg.addView(overlayView);
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		eBaiduMapOverlayMgr = new EBaiduMapOverlayMgr(this, mBaiduMap, mMapView);
		eBaiduMapPoiSearch = new EBaiduMapPoiSearch(this, mBaiduMap, mMapView);
		eBaiduMapBusLineSearch = new EBaiduMapBusLineSearch(this, mBaiduMap, mMapView);
		eBaiduMapRoutePlanSearch = new EBaiduMapRoutePlanSearch(this, mBaiduMap, mMapView);

		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mSDKReceiver = new SDKReceiver();
        getActivity().registerReceiver(mSDKReceiver, iFilter);
		initOritationListener();
		// 定位初始化
		mLocClient = new LocationClient(getActivity().getApplicationContext());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(2000);
		mLocClient.setLocOption(option);

		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);

		mBaiduMap.setOnMapClickListener(this);
		mBaiduMap.setOnMapStatusChangeListener(this);
		mBaiduMap.setOnMapLoadedCallback(this);
		mBaiduMap.setOnMapDoubleClickListener(this);
		mBaiduMap.setOnMapLongClickListener(this);
		mBaiduMap.setOnMyLocationClickListener(this);

		defaultLevel = mBaiduMap.getMapStatus().zoom;

		return bg;
	}

	@Override
	public void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
		myOrientationListener.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
		myOrientationListener.start();
	}

	public void readyToDestroy() {
		overlayView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
        getActivity().unregisterReceiver(mSDKReceiver);
		eBaiduMapOverlayMgr.clearMapOverLayMgr();
		eBaiduMapPoiSearch.destroy();
		eBaiduMapBusLineSearch.destroy();
		eBaiduMapRoutePlanSearch.destroy();
		mGeoCoder.destroy();
		stopLocation();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
		Log.i(LTAG, "onDestroy");
	}

	public void setMapType(int type) {
		mBaiduMap.setMapType(type);
	}

	public void setTrafficEnabled(boolean enable) {
		mBaiduMap.setTrafficEnabled(enable);
	}

	public void setCenter(double lng, double lat, boolean isUseAnimate) {
		LatLng ll = new LatLng(lat, lng);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);

		if (isUseAnimate) {
			mBaiduMap.animateMapStatus(u);
		} else {
			mBaiduMap.setMapStatus(u);
			MapStatus status = mBaiduMap.getMapStatus();
			@SuppressWarnings("unused")
			double lat2 = status.target.latitude;
		}
	}

	/**
	 * getCenter得到中心点
	 */
	public LatLng getCenter() {
		MapStatus mapStatus = mBaiduMap.getMapStatus();
		if (mapStatus == null) {
			MLog.getIns().e("mapStatus == null");
			return null;
		}
		LatLng latLng = mapStatus.target;
		if (latLng == null) {
			MLog.getIns().e("latLng == null");
		}
		jsonLatLngCallback(latLng, EBaiduMapUtils.MAP_FUN_CB_GETCENTER);
        return latLng;
	}

	/**
	 * 处理缩放 sdk 缩放级别范围： [3.0,19.0]
	 */
	public void zoomTo(float zoomLevel) {
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
		mBaiduMap.animateMapStatus(u);
	}

	public void zoomIn() {
		MapStatusUpdate u = MapStatusUpdateFactory.zoomIn();
		mBaiduMap.animateMapStatus(u);
	}

	public void zoomOut() {
		MapStatusUpdate u = MapStatusUpdateFactory.zoomOut();
		mBaiduMap.animateMapStatus(u);
	}

	public void rotate(int angle) {
		MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(angle).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(u);
	}

	public void overlook(int angle) {
		MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(angle).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(u);
	}

	public void setZoomEnable(boolean enable) {
		mUiSettings.setZoomGesturesEnabled(enable);
	}

	public void setRotateEnable(boolean enable) {
		mUiSettings.setRotateGesturesEnabled(enable);
	}

	public void setCompassEnable(boolean enable) {
		mUiSettings.setCompassEnabled(enable);
	}

	public void setScrollEnable(boolean enable) {
		mUiSettings.setScrollGesturesEnabled(enable);
	}

	public void setOverlookEnable(boolean enable) {
		mUiSettings.setOverlookingGesturesEnabled(enable);
	}

	public void addMarkerOverlay(String markerInfo) {
		eBaiduMapOverlayMgr.addMarkerOverlay(markerInfo);
	}

	public void removeMarkerOverlay(String markerId) {
		eBaiduMapOverlayMgr.removeMarkerOverlay(markerId);
	}

	public void setMarkerOverlay(String markerId, String markerInfo) {
		eBaiduMapOverlayMgr.setMarkerOverlay(markerId, markerInfo);
	}

	public void showBubble(String markerId) {
		eBaiduMapOverlayMgr.showBubble(markerId);
	}

	public void hideBubble() {
		eBaiduMapOverlayMgr.hideBubble();
	}

	public EUExBaiduMap getUexBaseObj() {
		return uexBaseObj;
	}

	public void setUexBaseObj(EUExBaiduMap uexBaseObj) {
		this.uexBaseObj = uexBaseObj;
	}

	public void poiSearchInCity(String city, String searchKey, int pageNum) {
		eBaiduMapPoiSearch.poiSearchInCity(city, searchKey, pageNum);
	}

	public void poiNearbySearch(double lng, double lat, int radius, String searchKey, int pageNum) {
		eBaiduMapPoiSearch.poiNearbySearch(lng, lat, radius, searchKey, pageNum);
	}

	public void poiBoundSearch(double east, double north, double west, double south, String searchKey, int pageNum) {
		eBaiduMapPoiSearch.poiBoundSearch(east, north, west, south, searchKey, pageNum);
	}

	public void busLineSearch(String city, String searchKey) {
		eBaiduMapBusLineSearch.busLineSearch(city, searchKey);
	}

	public void removeBusLine() {
		eBaiduMapBusLineSearch.removeBusLine();
	}

	public void preBusLineNode() {
		eBaiduMapBusLineSearch.preBusLineNode();
	}

	public void nextBusLineNode() {
		eBaiduMapBusLineSearch.nextBusLineNode();
	}

	public void searchRoutePlan(EBaiduMapRoutePlanOptions routePlanOptions) {
		eBaiduMapRoutePlanSearch.searchRoutePlan(routePlanOptions);
	}

	public void removeRoutePlan(String routePlanId) {
		eBaiduMapRoutePlanSearch.removeRoutePlan(routePlanId);
	}

	public void preRouteNode() {
		eBaiduMapRoutePlanSearch.preRouteNode();
	}

	public void nextRouteNode() {
		eBaiduMapRoutePlanSearch.nextRouteNode();
	}

	public void geocode(String city, String address) {
		Log.i(LTAG, "geocode-》" + city + "," + address);
		mGeoCoder.geocode(new GeoCodeOption().city(city).address(address));
	}

	public void reverseGeoCode(double lng, double lat) {
		Log.i(LTAG, "reverseGeoCode");
		LatLng ll = new LatLng(lat, lng);
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			jsonLatLngCallback(null, EBaiduMapUtils.MAP_FUN_CB_GEOCODE_RESULT);
			return;
		}
		jsonLatLngCallback(result.getLocation(), EBaiduMapUtils.MAP_FUN_CB_GEOCODE_RESULT);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			jsonAddressCallback(null, EBaiduMapUtils.MAP_FUN_CB_REVERSE_GEOCODE_RESULT);
			return;
		}
		jsonAddressCallback(result.getAddress(), EBaiduMapUtils.MAP_FUN_CB_REVERSE_GEOCODE_RESULT);
	}

	/**
	 * 获得当前位置
	 */
	public void getCurrentLocation() {
		Log.i(LTAG, "getCurrentLocation");
		if (mLocClient != null && !mLocClient.isStarted()) {
			mLocClient.start();
		}
		isOneTimeLocation = true;// 一次定位开启
	}

	/**
	 * 开始定位
	 */
	public void startLocation() {
		Log.i(LTAG, "startLocation");
		if (mLocClient != null && !mLocClient.isStarted()) {
			mLocClient.start();
			isStartDurationLocation = true;
		}
	}

	/**
	 * 结束定位
	 */
	public void stopLocation() {
		Log.i(LTAG, "stopLocation");
		if (mLocClient != null && mLocClient.isStarted()) {
			// 退出时销毁定位
			mLocClient.stop();
			isStartDurationLocation = false;
		}
		// 关闭定位图层
		setMyLocationEnabled(false);
	}

	/**
	 * 显示或隐藏用户位置
	 */
	public void setMyLocationEnabled(boolean enable) {
		Log.i(LTAG, "setMyLocationEnabled");
		mBaiduMap.setMyLocationEnabled(enable);
		if (enable) {
			if (mLocClient != null && !mLocClient.isStarted()) {
				mLocClient.start();
			}
			isStartDurationLocation = true;
		} else {
			isStartDurationLocation = false;
		}
	}

	public void setUserTrackingMode(int mode) {
		if (mode == 0) {
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));
		} else if (mode == 1) {
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.FOLLOWING, true, null));
		} else if (mode == 2) {
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.COMPASS, true, null));
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				jsonReceiveLocationCallback(null, EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION);
				return;
			}
			MyLocationData locationData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locationData);
			if (isOneTimeLocation) {
				jsonReceiveLocationCallback(location, EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION);
				isOneTimeLocation = false;
				// 如果不是持续定位，每次定位后停止定位服务
				if (!isStartDurationLocation) {
					mLocClient.stop();
				}
			} else {
				jsonReceiveLocationCallback(location, EBaiduMapUtils.MAP_FUN_ON_RECEIVE_LOCATION);
			}
		}

		@SuppressWarnings("unused")
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void jsonReceiveLocationCallback(BDLocation location, String header) {
		if (uexBaseObj != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, Double.toString(location.getLatitude()));
				jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, Double.toString(location.getLongitude()));
				jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TIMESTAMP, location.getTime());
				String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + jsonObject.toString() + "');}";
				uexBaseObj.onCallback(js);
                if (null != uexBaseObj.getCurrentLocationFuncId && EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION.equals(header)) {
                    uexBaseObj.callbackToJs(Integer.parseInt(uexBaseObj.getCurrentLocationFuncId), false, jsonObject);
                }
			} catch (JSONException e) {
				String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + null + "');}";
				uexBaseObj.onCallback(js);
                if (null != uexBaseObj.getCurrentLocationFuncId && EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION.equals(header)) {
                    uexBaseObj.callbackToJs(Integer.parseInt(uexBaseObj.getCurrentLocationFuncId), false);
                }
				e.printStackTrace();
			}
		}
	}

	private void jsonAddressCallback(String address, String header) {
		if (uexBaseObj != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ADDRESS, address);
				String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + jsonObject.toString() + "');}";
				uexBaseObj.onCallback(js);
                if (null != uexBaseObj.reverseGeocodeFuncId) {
                    uexBaseObj.callbackToJs(Integer.parseInt(uexBaseObj.reverseGeocodeFuncId), false, jsonObject);
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void jsonLatLngCallback(LatLng point, String header) {
		if (uexBaseObj != null) {
			JSONObject jsonObject = new JSONObject();
			try {
				if (point != null) {
					jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, Double.toString(point.longitude));
					jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, Double.toString(point.latitude));
				}
				String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + jsonObject.toString() + "');}";
				uexBaseObj.onCallback(js);
                if (null != uexBaseObj.geocodeFuncId) {
                    uexBaseObj.callbackToJs(Integer.parseInt(uexBaseObj.geocodeFuncId), false, jsonObject);
                }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 地图单击事件回调函数
	 *
	 * @param point
	 *            点击的地理坐标
	 */
	public void onMapClick(LatLng point) {
		jsonLatLngCallback(point, EBaiduMapUtils.MAP_FUN_ON_MAP_CLICK_LISTNER);
	}

	/**
	 * 地图内 Poi 单击事件回调函数
	 *
	 * @param poi
	 *            点击的 poi 信息
	 */
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

	/**
	 * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
	 *
	 * @param status
	 *            地图状态改变开始时的地图状态
	 */
	public void onMapStatusChangeStart(MapStatus status) {
		if (changeBean == null) {
			changeBean = new MapStatusChangeBean();
		}
		changeBean.setOldZoom(status.zoom);
		changeBean.setOldOverlook(status.overlook);
		changeBean.setOldRotate(status.rotate);
		changeBean.setOldCenterLongitude(status.target.longitude);
		changeBean.setOldCenterLatitude(status.target.latitude);
		changeBean.setOldNortheastLatitude(status.bound.northeast.latitude);
		changeBean.setOldNortheastLongitude(status.bound.northeast.longitude);
		changeBean.setOldSouthwestLatitude(status.bound.southwest.latitude);
		changeBean.setOldCenterLongitude(status.bound.southwest.longitude);
	}

	/**
	 * 地图状态变化中
	 *
	 * @param status
	 *            当前地图状态
	 */
	public void onMapStatusChange(MapStatus status) {
	}

	/**
	 * 地图状态改变结束
	 *
	 * @param status
	 *            地图状态改变结束后的地图状态
	 */
	public void onMapStatusChangeFinish(MapStatus status) {
		if (uexBaseObj != null && status.zoom != defaultLevel) {
			defaultLevel = status.zoom;
			String js = EUExBase.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_ZOOM_LEVEL_CHANGE_LISTENER + "){" + EBaiduMapUtils.MAP_FUN_ON_ZOOM_LEVEL_CHANGE_LISTENER + "(" + status.zoom + ", "
					+ status.target.latitude + ", " + status.target.longitude + ");}";
			uexBaseObj.onCallback(js);
		}
		if (uexBaseObj != null && changeBean != null) {
			changeBean.setNewZoom(status.zoom);
			changeBean.setNewOverlook(status.overlook);
			changeBean.setNewRotate(status.rotate);
			changeBean.setNewCenterLongitude(status.target.longitude);
			changeBean.setNewCenterLatitude(status.target.latitude);
			changeBean.setNewNortheastLatitude(status.bound.northeast.latitude);
			changeBean.setNewNortheastLongitude(status.bound.northeast.longitude);
			changeBean.setNewSouthwestLatitude(status.bound.southwest.latitude);
			changeBean.setNewSouthwestLongitude(status.bound.southwest.longitude);
			JSONObject json = new JSONObject();
			try {
				if (changeBean.isZoomChanged()) {
					JSONObject zoomJson = new JSONObject();
					zoomJson.put(MapStatusChangeBean.TAG_OLDZOOM, changeBean.getOldZoom());
					zoomJson.put(MapStatusChangeBean.TAG_NEWZOOM, changeBean.getNewZoom());
					json.put(MapStatusChangeBean.TAG_ZOOM, zoomJson);
				}
				if (changeBean.isOverlookChanged()) {
					JSONObject overlookJson = new JSONObject();
					overlookJson.put(MapStatusChangeBean.TAG_OLDOVERLOOK, changeBean.getOldOverlook());
					overlookJson.put(MapStatusChangeBean.TAG_NEWOVERLOOK, changeBean.getNewOverlook());
					json.put(MapStatusChangeBean.TAG_OVERLOOK, overlookJson);
				}
				if (changeBean.isRotateChanged()) {
					JSONObject rotateJson = new JSONObject();
					rotateJson.put(MapStatusChangeBean.TAG_OLDROTATE, changeBean.getOldRotate());
					rotateJson.put(MapStatusChangeBean.TAG_NEWROTATE, changeBean.getNewRotate());
					json.put(MapStatusChangeBean.TAG_ROTATE, rotateJson);
				}
				if (changeBean.isNortheastChanged()) {
					JSONObject northeastJson = new JSONObject();
					northeastJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, status.bound.northeast.longitude);
					northeastJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, status.bound.northeast.latitude);
					json.put(MapStatusChangeBean.TAG_NORTHEAST, northeastJson);
				}
				if (changeBean.isSouthWestChanged()) {
					JSONObject southwestJson = new JSONObject();
					southwestJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, status.bound.southwest.longitude);
					southwestJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, status.bound.southwest.latitude);
					json.put(MapStatusChangeBean.TAG_SOUTHWEST, southwestJson);
				}
				if ((changeBean.isSouthWestChanged() || changeBean.isNortheastChanged()) && changeBean.isCenterChanged()) {
					JSONObject centerJson = new JSONObject();
					centerJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, status.target.longitude);
					centerJson.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, status.target.latitude);
					json.put(MapStatusChangeBean.TAG_CENTER, centerJson);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (json.keys().hasNext()) {
				String js = EUExBase.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_MAP_STATUS_CHANGE_LISTENER + "){" + EBaiduMapUtils.MAP_FUN_ON_MAP_STATUS_CHANGE_LISTENER + "('" + json.toString()
						+ "');}";
				uexBaseObj.onCallback(js);
			}
			changeBean = null;
		}
	}

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(getActivity().getApplicationContext());
		myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				MyLocationData locationData = mBaiduMap.getLocationData();
				if (locationData == null) {
					return;
				}
				mXDirection = (int) x;
				// 构造定位数据
				MyLocationData locData = new MyLocationData.Builder().accuracy(locationData.accuracy)
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(mXDirection).latitude(locationData.latitude).longitude(locationData.longitude).build();
				// 设置定位数据
				mBaiduMap.setMyLocationData(locData);
			}
		});
	}

	/**
	 * 地图加载完成回调函数
	 */
	public void onMapLoaded() {
		if (uexBaseObj != null) {
			String js = EUExBase.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_CB_OPEN + "){" + EBaiduMapUtils.MAP_FUN_CB_OPEN + "();}";
			uexBaseObj.onCallback(js);
            if (null != uexBaseObj.openFuncId) {
                uexBaseObj.callbackToJs(Integer.parseInt(uexBaseObj.openFuncId), false);
            }
		}
	}

	/**
	 * 地图双击事件监听回调函数
	 *
	 * @param point
	 *            双击的地理坐标
	 */
	public void onMapDoubleClick(LatLng point) {
		jsonLatLngCallback(point, EBaiduMapUtils.MAP_FUN_ON_MAP_DOUBLE_CLICK_LISTNER);
	}

	/**
	 * 地图长按事件监听回调函数
	 *
	 * @param point
	 *            长按的地理坐标
	 */
	public void onMapLongClick(LatLng point) {
		jsonLatLngCallback(point, EBaiduMapUtils.MAP_FUN_ON_MAP_LONG_CLICK_LISTNER);
	}

	/**
	 * 地图定位图标点击事件监听函数
	 */
	public boolean onMyLocationClick() {
		return true;
	}

	/**
	 * 地图截屏回调接口
	 *
	 * @param snapshot
	 *            截屏返回的 bitmap 数据
	 */
	public void onSnapshotReady(Bitmap snapshot) {
	}

	public void removeOverlay(String overlayInfo) {
		eBaiduMapOverlayMgr.removeOverlay(overlayInfo);

	}

	public void addDotOverlay(String dotInfo) {
		eBaiduMapOverlayMgr.addDotOverlay(dotInfo);
	}

	public void addPolylineOverlay(String polylineInfo) {
		eBaiduMapOverlayMgr.addPolylineOverlay(polylineInfo);
	}

	public void addArcOverlay(String arcInfo) {
		eBaiduMapOverlayMgr.addArcOverlay(arcInfo);
	}

	public void addCircleOverlay(String circleInfo) {
		eBaiduMapOverlayMgr.addCircleOverlay(circleInfo);
	}

	public void addPolygonOverlay(String polygonInfo) {
		eBaiduMapOverlayMgr.addPolygonOverlay(polygonInfo);
	}

	public void addGroundOverlay(String groundInfo) {
		eBaiduMapOverlayMgr.addGroundOverlay(groundInfo);
	}

	public void addTextOverlay(String textInfo) {
		eBaiduMapOverlayMgr.addTextOverlay(textInfo);
	}

	public void hideMap() {
		if (mMapView != null) {
			mMapView.setVisibility(View.INVISIBLE);
		}
	}

	public void showMap() {
		if (mMapView != null) {
			mMapView.setVisibility(View.VISIBLE);
		}
	}

	public void zoomControlsEnabled(boolean zoomControlsEnabled) {
		if (mMapView != null) {
			mMapView.showZoomControls(zoomControlsEnabled);
		}
	}
}
