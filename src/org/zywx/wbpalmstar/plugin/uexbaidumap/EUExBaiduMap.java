package org.zywx.wbpalmstar.plugin.uexbaidumap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import org.zywx.wbpalmstar.plugin.uexbaidumap.function.GeoCoderFunction;
import org.zywx.wbpalmstar.plugin.uexbaidumap.function.LocationFunction;
import org.zywx.wbpalmstar.plugin.uexbaidumap.receiver.SDKReceiver;
import org.zywx.wbpalmstar.plugin.uexbaidumap.utils.MLog;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;

public class EUExBaiduMap extends EUExBase {

	/**
	 * 百度地图是否初始化
	 */
	private static boolean isBaiduSdkInit = false;

	/**
	 * SDK广播接收器，主要为了监听appKey是否配置正确
	 */
	private SDKReceiver mSDKReceiver;

	/**
	 * 必需要打开地图的功能使用Fragment
	 */
	private EBaiduMapBaseFragment mMapBaseFragment;

	/**
	 * 百度地图一些不需要打开地图的功能管理器
	 */
	private EBaiduMapBaseNoMapViewManager mMapBaseNoMapViewManager;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param inParent
	 */
	public EUExBaiduMap(Context context, EBrowserView inParent) {
		super(context, inParent);

		if (!isBaiduSdkInit) {
			SDKInitializer.initialize(context.getApplicationContext());// 在使用SDK各组间之前初始化context信息，传入ApplicationContext
			isBaiduSdkInit = true;
		}

		// 注册 SDK 广播接收器
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
		intentFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		intentFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mSDKReceiver = new SDKReceiver();
		mContext.registerReceiver(mSDKReceiver, intentFilter);

	}

	/**
	 * clean
	 */
	@Override
	protected boolean clean() {
		close(null);
		return false;
	}

	/**
	 * 拦截Activity的onCreate方法
	 * 
	 * @param context
	 */
	public static void onActivityCreate(Context context) {
		((Activity) context).getWindow().setFormat(PixelFormat.TRANSLUCENT);
	}

	// TODO
	/*
	 * 前端接口
	 */
	public void open(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_OPEN, params);
	}

	public void close(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_CLOSE, params);
	}

	public void setMapType(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETMAPTYPE, params);
	}

	public void setTrafficEnabled(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETTRAFFIC, params);
	}

	public void setCenter(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETCENTER, params);
	}

	public void getCenter(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_GETCENTER, params);
	}

	public void setZoomLevel(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ZOOMTO, params);
	}

	public void zoomIn(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ZOOMIN, params);
	}

	public void zoomOut(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ZOOMOUT, params);
	}

	public void rotate(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ROTATE, params);
	}

	public void overlook(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_OVERLOOK, params);
	}

	public void setZoomEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ZOOMENABLE, params);
	}

	public void setRotateEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ROTATEENABLE, params);
	}

	public void setCompassEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_COMPASSENABLE, params);
	}

	public void setScrollEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SCROLLENABLE, params);
	}

	public void setOverlookEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_OVERLOOKENABLE, params);
	}

	public void addMarkersOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDMARKERSOVERLAY, params);
	}

	public void setMarkerOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETMARKERSOVERLAY, params);
	}

	public void showBubble(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SHOWBUBBLE, params);
	}

	public void hideBubble(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_HIDEBUBBLE, params);
	}

	public void removeMakersOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_REMOVEMAKERSOVER, params);
	}

	public void removeOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_REMOVEOVERLAY, params);
	}

	public void addDotOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDDOTOVERLAY, params);
	}

	public void addPolylineOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDPOLYLINEOVERLAY, params);
	}

	public void addArcOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDARCOVERLAY, params);
	}

	public void addCircleOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDCIRCLEOVERLAY, params);
	}

	public void addPolygonOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDPOLYGONOVERLAY, params);
	}

	public void addGroundOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDGROUNDOVERLAY, params);
	}

	public void addTextOverlay(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ADDTEXTOVERLAY, params);
	}

	public void poiSearchInCity(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_POISEARCHINCITY, params);
	}

	public void poiNearbySearch(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_POINEARBYSEARCH, params);
	}

	public void poiBoundSearch(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_POIBOUNDSEARCH, params);
	}

	public void busLineSearch(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_BUSLINESEARCH, params);
	}

	public void removeBusLine(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_REMOVEBUSLINE, params);
	}

	public void preBusLineNode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_PREBUSLINENODE, params);
	}

	public void nextBusLineNode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_NEXTBUSLINENODE, params);
	}

	public void searchRoutePlan(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SEARCHROUTEPLAN, params);
	}

	public void removeRoutePlan(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_REMOVEROUTEPLAN, params);
	}

	public void preRouteNode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_PREROUTENODE, params);
	}

	public void nextRouteNode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_NEXTROUTENODE, params);
	}

	public void geocode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_GEOCODE, params);
	}

	public void reverseGeocode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_REVERSEGEOCODE, params);
	}

	public void getCurrentLocation(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_GETCURRENTLOCATION, params);
	}

	public void startLocation(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_STARTLOCATION, params);
	}

	public void stopLocation(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_STOPTLOCATION, params);
	}

	public void setMyLocationEnable(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETMYLOCATIONENABLE, params);
	}

	public void setUserTrackingMode(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SETUSERTRACKINGMODE, params);
	}

	public void hideMap(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_HIDEMAP, params);
	}

	public void showMap(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_SHOWMAP, params);
	}

	public void zoomControlsEnabled(String[] params) {
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_ZOOMCONTROLSENABLED, params);
	}

	public void getDistance(String[] params) {// 计算两点之间的距离 by waka
		sendMessageWithType(EBaiduMapUtils.MAP_MSG_CODE_GETDISTANCE, params);
	}

	private void sendMessageWithType(int msgType, String[] params) {
		if (mHandler == null) {
			MLog.getIns().i("mHandler == null");
			return;
		}
		Message msg = new Message();
		msg.what = msgType;
		msg.obj = this;
		Bundle b = new Bundle();
		b.putStringArray(EBaiduMapUtils.MAP_FUN_PARAMS_KEY, params);
		msg.setData(b);
		mHandler.sendMessage(msg);
	}

	/**
	 * onHandleMessage
	 */
	@Override
	public void onHandleMessage(Message msg) {
		if (msg.what == EBaiduMapUtils.MAP_MSG_CODE_OPEN) {
			handleOpen(msg);
		} else {
			handleMessageInMap(msg);
		}
	}

	/**
	 * handleMessageInMap
	 * 
	 * @param msg
	 */
	private void handleMessageInMap(Message msg) {
		if (mMapBaseFragment != null) {
			String[] params = msg.getData().getStringArray(EBaiduMapUtils.MAP_FUN_PARAMS_KEY);
			EBaiduMapBaseFragment eBaiduMapBaseFragment = mMapBaseFragment;

			switch (msg.what) {
			case EBaiduMapUtils.MAP_MSG_CODE_CLOSE:
				handleCloseBaiduMap();
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETMAPTYPE:
				handleSetMapType(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETTRAFFIC:
				handleSetTraffic(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETCENTER:
				handleSetCenter(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_GETCENTER:
				handleGetCenter(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ZOOMTO:
				handleZoomTo(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ZOOMIN:
				handleZoomIn(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ZOOMOUT:
				handleZoomOut(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ROTATE:
				handleRotate(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_OVERLOOK:
				handleOverlook(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ZOOMENABLE:
				handleZoomEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ROTATEENABLE:
				handleRotateEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_COMPASSENABLE:
				handleCompassEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SCROLLENABLE:
				handleScrollEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_OVERLOOKENABLE:
				handleOverlookEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDMARKERSOVERLAY:
				handleAddMarkersOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETMARKERSOVERLAY:
				handleSetMarkerOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SHOWBUBBLE:
				handleShowBubble(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_HIDEBUBBLE:
				handleHideBubble(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_REMOVEMAKERSOVER:
				handleRemoveMakersOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_POISEARCHINCITY:
				handlePoiSearchInCity(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_POINEARBYSEARCH:
				handlePoiNearbySearch(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_POIBOUNDSEARCH:
				handlePoiBoundSearch(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_BUSLINESEARCH:
				handleBusLineSearch(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_REMOVEBUSLINE:
				handleRemoveBusLine(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_PREBUSLINENODE:
				handlePreBusLineNode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_NEXTBUSLINENODE:
				handleNextBusLineNode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SEARCHROUTEPLAN:
				handleSearchRoutePlan(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_REMOVEROUTEPLAN:
				handleRemoveRoutePlan(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_PREROUTENODE:
				handlePreRouteNode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_NEXTROUTENODE:
				handleNextRouteNode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_GEOCODE:
				handleGeocode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_REVERSEGEOCODE:
				handleReverseGeocode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_GETCURRENTLOCATION:
				handleGetCurrentLocation(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_STARTLOCATION:
				handleStartLocation(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_STOPTLOCATION:
				handleStopLocation(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETMYLOCATIONENABLE:
				handleSetMyLocationEnable(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SETUSERTRACKINGMODE:
				handleSetUserTrackingMode(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_REMOVEOVERLAY:
				handleRemoveOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDDOTOVERLAY:
				handleAddDotOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDPOLYLINEOVERLAY:
				handleAddPolylineOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDARCOVERLAY:
				handleAddArcOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDCIRCLEOVERLAY:
				handleAddCircleOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDPOLYGONOVERLAY:
				handleAddPolygonOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDGROUNDOVERLAY:
				handleAddGroundOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ADDTEXTOVERLAY:
				handleAddTextOverlay(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_HIDEMAP:
				handleHideMap(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_SHOWMAP:
				handleShowMap(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_ZOOMCONTROLSENABLED:
				handleZoomControlsEnabled(params, eBaiduMapBaseFragment);
				break;
			case EBaiduMapUtils.MAP_MSG_CODE_GETDISTANCE:// 计算两点之间的距离 by waka
				handleGetDistance(params);
				break;

			default:
				break;
			}
		}

		// 一些功能不需要打开地图 by waka
		else {
			String[] params = msg.getData().getStringArray(EBaiduMapUtils.MAP_FUN_PARAMS_KEY);
			switch (msg.what) {

			// 城市检索
			case EBaiduMapUtils.MAP_MSG_CODE_POISEARCHINCITY:
				handlePoiSearchInCity(params, null);
				break;

			// 周边检索
			case EBaiduMapUtils.MAP_MSG_CODE_POINEARBYSEARCH:
				handlePoiNearbySearch(params, null);
				break;

			// 区域检索
			case EBaiduMapUtils.MAP_MSG_CODE_POIBOUNDSEARCH:
				handlePoiBoundSearch(params, null);
				break;

			// 计算两点之间的距离 by waka
			case EBaiduMapUtils.MAP_MSG_CODE_GETDISTANCE:
				handleGetDistance(params);
				break;

			// 获得当前位置
			case EBaiduMapUtils.MAP_MSG_CODE_GETCURRENTLOCATION:
				handleGetCurrentLocation(params, null);
				break;

			// 地理编码
			case EBaiduMapUtils.MAP_MSG_CODE_GEOCODE:
				handleGeocode(params, null);
				break;

			// 反地理编码
			case EBaiduMapUtils.MAP_MSG_CODE_REVERSEGEOCODE:
				handleReverseGeocode(params, null);
				break;

			default:
				break;
			}
		}
	}

	// TODO
	/*
	 * 真正的实现方法
	 */
	private void handleOpen(Message msg) {

		MLog.getIns().d("start");

		String[] params = msg.getData().getStringArray(EBaiduMapUtils.MAP_FUN_PARAMS_KEY);
		if (params == null || (params.length != 4 && params.length != 6)) {
			return;
		}

		int x = 0, y = 0, w = 0, h = 0;
		double lng = 0.0, lat = 0.0;
		boolean isUseLngLat = false;

		try {
			// 修复了前端调用open方法时传入小数时抛出NumberFormatException的问题 by waka 2016/01/23
			x = (int) Double.parseDouble(params[0]);
			y = (int) Double.parseDouble(params[1]);
			w = (int) Double.parseDouble(params[2]);
			h = (int) Double.parseDouble(params[3]);

			if (params.length == 6) {
				lng = Double.parseDouble(params[4]);
				lat = Double.parseDouble(params[5]);
				isUseLngLat = true;
			}
			if (mMapBaseFragment != null) {
				MLog.getIns().e("mMapBaseFragment != null");
				return;
			}
			mMapBaseFragment = new EBaiduMapBaseFragment();
			mMapBaseFragment.setBaseObj(this);
			if (isUseLngLat) {
				LatLng center = new LatLng(lat, lng);
				mMapBaseFragment.setStartCenter(center);
			}

			String activityId = EBaiduMapUtils.MAP_ACTIVITY_ID + EUExBaiduMap.this.hashCode();
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
			lp.leftMargin = x;
			lp.topMargin = y;
			addFragmentToCurrentWindow(mMapBaseFragment, lp, activityId);

		} catch (Exception e) {
			e.printStackTrace();
			MLog.getIns().e(e);
		}
	}

	private void handleCloseBaiduMap() {
		if (mMapBaseFragment != null) {
			@SuppressWarnings("unused")
			String activityId = EBaiduMapUtils.MAP_ACTIVITY_ID + EUExBaiduMap.this.hashCode();
			// removeFragmentFromWebView(activityId);
			// mapBaseFragment = null;
			mMapBaseFragment.readyToDestroy();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
                    if (mMapBaseFragment != null) {
                        removeFragmentFromWindow(mMapBaseFragment);
                        mMapBaseFragment = null;
                    }
				}
			}, 10);
		}

		// by waka
		// 关闭后会出问题
		// if (mapBaseNoMapViewManager != null) {
		// mapBaseNoMapViewManager.destory();
		// }
	}

	private void handleSetMapType(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int type = Integer.parseInt(params[0]);
			if (type == 1) { // 普通地图
				type = BaiduMap.MAP_TYPE_NORMAL;
			} else if (type == 2) { // 卫星地图
				type = BaiduMap.MAP_TYPE_SATELLITE;
			}
			eBaiduMapBaseFragment.setMapType(type);
		} catch (Exception e) {
		}
	}

	private void handleSetTraffic(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int type = Integer.parseInt(params[0]);
			if (type == 0) { // 关闭交通图
				eBaiduMapBaseFragment.setTrafficEnabled(false);
			} else if (type == 1) { // 开启交通图
				eBaiduMapBaseFragment.setTrafficEnabled(true);
			}
		} catch (Exception e) {
		}
	}

	private void handleSetCenter(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			double lng = Double.parseDouble(params[0]);
			double lat = Double.parseDouble(params[1]);
			boolean isUseAnimate = false;
			if (params.length == 3) { // 设置动画
				int v = Integer.parseInt(params[2]);
				isUseAnimate = (v == 1);
			}
			eBaiduMapBaseFragment.setCenter(lng, lat, isUseAnimate);
		} catch (Exception e) {
		}
	}

	private void handleGetCenter(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.getCenter();
		} catch (Exception e) {
		}
	}

	private void handleZoomTo(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			float zoomLevel = Float.parseFloat(params[0]);
			if (zoomLevel < 3) {
				zoomLevel = 3.0f;
			}
			if (zoomLevel > 19) {
				zoomLevel = 19.0f;
			}
			eBaiduMapBaseFragment.zoomTo(zoomLevel);
		} catch (Exception e) {
		}
	}

	private void handleZoomIn(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.zoomIn();
		} catch (Exception e) {
		}
	}

	private void handleZoomOut(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.zoomOut();
		} catch (Exception e) {
		}
	}

	private void handleRotate(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int angle = Integer.parseInt(params[0]);
			eBaiduMapBaseFragment.rotate(angle);
		} catch (Exception e) {
		}
	}

	private void handleOverlook(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int angle = Integer.parseInt(params[0]);
			eBaiduMapBaseFragment.overlook(angle);
		} catch (Exception e) {
		}
	}

	private void handleZoomEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1) ? true : false;
			eBaiduMapBaseFragment.setZoomEnable(enable);
		} catch (Exception e) {
		}
	}

	private void handleRotateEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1) ? true : false;
			eBaiduMapBaseFragment.setRotateEnable(enable);
		} catch (Exception e) {
		}
	}

	private void handleCompassEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1) ? true : false;
			eBaiduMapBaseFragment.setCompassEnable(enable);
		} catch (Exception e) {
		}
	}

	private void handleScrollEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1) ? true : false;
			eBaiduMapBaseFragment.setScrollEnable(enable);
		} catch (Exception e) {
		}
	}

	private void handleOverlookEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1) ? true : false;
			eBaiduMapBaseFragment.setOverlookEnable(enable);
		} catch (Exception e) {
		}
	}

	private void handleAddMarkersOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		try {
			JSONArray jsonArray = new JSONArray(params[0]);
			for (int i = 0; i < jsonArray.length(); i++) {
				eBaiduMapBaseFragment.addMarkerOverlay(jsonArray.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleSetMarkerOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		MLog.getIns().i("");
		if (params.length != 2) {
			return;
		}
		try {
			String markerId = params[0];
			String markerInfo = params[1];
			MLog.getIns().i("markerId = " + markerId);
			MLog.getIns().i("markerInfo = " + markerInfo);
			eBaiduMapBaseFragment.setMarkerOverlay(markerId, markerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleShowBubble(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params.length != 1) {
				return;
			}
			String markerId = params[0];
			eBaiduMapBaseFragment.showBubble(markerId);
		} catch (Exception e) {
		}
	}

	private void handleHideBubble(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.hideBubble();
		} catch (Exception e) {
		}
	}

	private void handleRemoveMakersOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params.length != 1) {
				return;
			}
			String infoString = params[0];
			JSONArray jsonArray = new JSONArray(infoString);
			for (int i = 0; i < jsonArray.length(); i++) {
				infoString = jsonArray.getString(i);
				eBaiduMapBaseFragment.removeMarkerOverlay(infoString);
			}
		} catch (Exception e) {
		}
	}

	private void handlePoiSearchInCity(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			JSONObject json = new JSONObject(params[0]);
			String city = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CITY);
			String searchKey = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SEARCHKEY);
			int pageNum = Integer.parseInt(json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_PAGENUM));

			// change by waka
			if (eBaiduMapBaseFragment != null) {
				eBaiduMapBaseFragment.poiSearchInCity(city, searchKey, pageNum);
			} else {
				if (mMapBaseNoMapViewManager == null) {
					mMapBaseNoMapViewManager = new EBaiduMapBaseNoMapViewManager(mContext, this);
				}
				mMapBaseNoMapViewManager.poiSearchInCity(city, searchKey, pageNum);
			}

		} catch (Exception e) {
			Log.e("waka", e.getMessage(), e);
		}
	}

	private void handlePoiNearbySearch(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params == null || params.length == 0) {
				return;
			}
			JSONObject json = new JSONObject(params[0]);
			String lng = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG);
			String lat = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT);
			String radius = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_RADIUS);
			String searchKey = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SEARCHKEY);
			String pageNum = json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_PAGENUM);

			// change by waka
			if (eBaiduMapBaseFragment != null) {
				eBaiduMapBaseFragment.poiNearbySearch(Double.parseDouble(lng), Double.parseDouble(lat), (int) Float.parseFloat(radius), searchKey, Integer.parseInt(pageNum));
			} else {
				if (mMapBaseNoMapViewManager == null) {
					mMapBaseNoMapViewManager = new EBaiduMapBaseNoMapViewManager(mContext, this);
				}
				mMapBaseNoMapViewManager.poiNearbySearch(Double.parseDouble(lng), Double.parseDouble(lat), (int) Float.parseFloat(radius), searchKey, Integer.parseInt(pageNum));
			}

		} catch (Exception e) {
		}
	}

	private void handlePoiBoundSearch(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			// 1
			JSONObject jsonObject = new JSONObject(params[0]);
			String northeastStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_NORTHEAST);
			// 2
			JSONObject jsonNortheastObj = new JSONObject(northeastStr);
			String lngNE = jsonNortheastObj.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG);
			String latNE = jsonNortheastObj.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT);
			// 3
			String southwestStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SOUTHWEST);
			// 4
			JSONObject jsonSouthwestObj = new JSONObject(southwestStr);
			String lngSW = jsonSouthwestObj.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG);
			String latSW = jsonSouthwestObj.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT);
			// 5
			String searchKey = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SEARCHKEY);
			String pageNum = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_PAGENUM);

			// change by waka
			if (eBaiduMapBaseFragment != null) {
				eBaiduMapBaseFragment.poiBoundSearch(Double.parseDouble(lngNE), Double.parseDouble(latNE), Double.parseDouble(lngSW), Double.parseDouble(latSW), searchKey, Integer.parseInt(pageNum));
			} else {
				if (mMapBaseNoMapViewManager == null) {
					mMapBaseNoMapViewManager = new EBaiduMapBaseNoMapViewManager(mContext, this);
				}
				mMapBaseNoMapViewManager.poiBoundSearch(Double.parseDouble(lngNE), Double.parseDouble(latNE), Double.parseDouble(lngSW), Double.parseDouble(latSW), searchKey,
						Integer.parseInt(pageNum));
			}

		} catch (Exception e) {
		}
	}

	private void handleBusLineSearch(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			JSONObject jsonObject = new JSONObject(params[0]);
			String cityStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CITY);
			String busStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSLINENAME);
			eBaiduMapBaseFragment.busLineSearch(cityStr, busStr);
		} catch (Exception e) {
		}
	}

	private void handleRemoveBusLine(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		eBaiduMapBaseFragment.removeBusLine();
	}

	private void handlePreBusLineNode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.preBusLineNode();
		} catch (Exception e) {
		}
	}

	private void handleNextBusLineNode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.nextBusLineNode();
		} catch (Exception e) {
		}
	}

	private void handleSearchRoutePlan(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			final EBaiduMapRoutePlanOptions routePlanOptions = EBaiduMapUtils.paraseRoutePlanOptions(params[0]);
			if (routePlanOptions == null) {
				return;
			}
			eBaiduMapBaseFragment.searchRoutePlan(routePlanOptions);
		} catch (Exception e) {
		}
	}

	private void handleRemoveRoutePlan(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params.length != 1) {
				return;
			}
			String routePlanId = params[0];
			eBaiduMapBaseFragment.removeRoutePlan(routePlanId);
		} catch (Exception e) {
		}
	}

	private void handlePreRouteNode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.preRouteNode();
		} catch (Exception e) {
		}
	}

	private void handleNextRouteNode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.nextRouteNode();
		} catch (Exception e) {
		}
	}

	private void handleGeocode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params != null && params.length > 0) {
				JSONObject jsonObject = new JSONObject(params[0]);
				if (jsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CITY) && jsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ADDRESS)) {
					String cityStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CITY);
					String addrStr = jsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ADDRESS);
					// eBaiduMapBaseFragment.geocode(cityStr, addrStr);

					// 不打开地图也能用地理编码
					GeoCoderFunction geoCoderFunction = new GeoCoderFunction(this);
					geoCoderFunction.geocode(cityStr, addrStr);
				}
			}
		} catch (Exception e) {
		}
	}

	private void handleReverseGeocode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			JSONObject json = new JSONObject(params[0]);
			double lng = Double.parseDouble(json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG));
			double lat = Double.parseDouble(json.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT));
			// eBaiduMapBaseFragment.reverseGeoCode(lng, lat);

			// 不打开地图也能用反地理编码
			GeoCoderFunction geoCoderFunction = new GeoCoderFunction(this);
			geoCoderFunction.reverseGeoCode(lng, lat);

		} catch (Exception e) {
			MLog.getIns().e(e);
		}
	}

	private void handleGetCurrentLocation(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		MLog.getIns().i("start");
		try {

			// eBaiduMapBaseFragment.getCurrentLocation();

			LocationFunction function = new LocationFunction(mContext, this);
			function.start();

		} catch (Exception e) {
			MLog.getIns().e(e);
		}
		MLog.getIns().i("end");
	}

	private void handleStartLocation(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.startLocation();
		} catch (Exception e) {
		}
	}

	private void handleStopLocation(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			eBaiduMapBaseFragment.stopLocation();
		} catch (Exception e) {
		}
	}

	private void handleSetMyLocationEnable(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int value = Integer.parseInt(params[0]);
			boolean enable = (value == 1);
			eBaiduMapBaseFragment.setMyLocationEnabled(enable);
		} catch (Exception e) {
		}
	}

	private void handleSetUserTrackingMode(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			int trackingMode = Integer.parseInt(params[0]);
			eBaiduMapBaseFragment.setUserTrackingMode(trackingMode);
		} catch (Exception e) {
		}
	}

	private void handleAddTextOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addTextOverlay(params[0]);
	}

	private void handleAddGroundOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addGroundOverlay(params[0]);
	}

	private void handleAddPolygonOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addPolygonOverlay(params[0]);
	}

	private void handleAddCircleOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addCircleOverlay(params[0]);
	}

	private void handleAddArcOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addArcOverlay(params[0]);
	}

	private void handleAddPolylineOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addPolylineOverlay(params[0]);
	}

	private void handleAddDotOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		eBaiduMapBaseFragment.addDotOverlay(params[0]);
	}

	private void handleRemoveOverlay(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		try {
			if (params.length != 1) {
				return;
			}
			eBaiduMapBaseFragment.removeOverlay(params[0]);
		} catch (Exception e) {
		}
	}

	private void handleHideMap(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		eBaiduMapBaseFragment.hideMap();
	}

	private void handleShowMap(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		eBaiduMapBaseFragment.showMap();
	}

	private void handleZoomControlsEnabled(String[] params, EBaiduMapBaseFragment eBaiduMapBaseFragment) {
		if (params.length != 1) {
			return;
		}
		try {
			int type = Integer.parseInt(params[0]);
			if (type == 0) {
				eBaiduMapBaseFragment.zoomControlsEnabled(false);
			} else if (type == 1) {
				eBaiduMapBaseFragment.zoomControlsEnabled(true);
			}
		} catch (Exception e) {
		}
	}

	private void handleGetDistance(String[] params) {
		if (params.length < 4) {
			return;
		}
		try {

			double lat1 = Double.valueOf(params[0]);
			double lon1 = Double.valueOf(params[1]);
			double lat2 = Double.valueOf(params[2]);
			double lon2 = Double.valueOf(params[3]);

			double distance = -1;

			LatLng p1 = new LatLng(lat1, lon1);
			LatLng p2 = new LatLng(lat2, lon2);
			distance = DistanceUtil.getDistance(p1, p2);

			// // 判断，如果是小距离
			// if ((Math.abs(lat1 - lat2) < MyDistanceUtils.SMALL_DISTANCE_FLAG)
			// && (Math.abs(lon1 - lon2) < MyDistanceUtils.SMALL_DISTANCE_FLAG))
			// {
			// distance = MyDistanceUtils.getShortDistance(lon1, lat1, lon2,
			// lat2);
			// }
			// // 大距离
			// else {
			// distance = MyDistanceUtils.getLongDistance(lon1, lat1, lon2,
			// lat2);
			// }

			jsCallback(EBaiduMapUtils.MAP_FUN_CB_GET_DISTANCE, 0, EUExCallback.F_C_TEXT, "" + distance);

		} catch (NumberFormatException e) {
			e.getStackTrace();
		}
	}
}
