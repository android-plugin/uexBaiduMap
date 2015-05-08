package org.zywx.wbpalmstar.plugin.uexbaidumap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineResult.BusStation;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class EBaiduMapBusLineSearch implements OnGetPoiSearchResultListener,
		OnGetBusLineSearchResultListener {
	private String TAG = "EBaiduMapBusLineSearch";
	protected Context mContext;
	protected BaiduMap mBaiduMap;
	protected MapView mMapView;
	private PoiSearch mBusLinePoiSearch = null;
	private BusLineSearch mBusLineSearch = null;
	private MyBusLineOverlay mMyBusLineOverlay;
	private List<String> mBusLineIDList = new ArrayList<String>();;
	private int busLineIndex = 0;
	private int busNodeIndex = -2; // 公交节点索引,供浏览节点时使用
	private BusLineResult mBusRoute = null; // 保存驾车/步行路线数据的变量，供浏览节点时使用

	public EBaiduMapBusLineSearch(Context context, BaiduMap baiduMap,
			MapView mapView) {
		mContext = context;
		mBaiduMap = baiduMap;
		mMapView = mapView;
		// 初始化搜索模块，注册搜索事件监听
		mBusLinePoiSearch = PoiSearch.newInstance();
		mBusLinePoiSearch.setOnGetPoiSearchResultListener(this);
		mBusLineSearch = BusLineSearch.newInstance();
		mBusLineSearch.setOnGetBusLineSearchResultListener(this);
	}

	public void busLineSearch(String city, String searchKey) {
		Log.i(TAG, "busLineSearch");
		mBusLineIDList.clear();
		busLineIndex = 0;
		// 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
		mBusLinePoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
				.keyword(searchKey).pageNum(0));
	}

	private void searchBusline(String city) {
		Log.i(TAG, "searchBusline");
		if (busLineIndex >= mBusLineIDList.size()) {
			busLineIndex = 0;
		}
		if (busLineIndex >= 0 && busLineIndex < mBusLineIDList.size()
				&& mBusLineIDList.size() > 0) {
			mBusLineSearch.searchBusLine((new BusLineSearchOption().city(city)
					.uid(mBusLineIDList.get(busLineIndex))));
			busLineIndex++;
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Log.i(TAG, "onGetPoiResult RESULT_NOT_FOUND");
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.i(TAG, "onGetPoiResult NO_ERROR");
			// 遍历所有poi，找到类型为公交线路的poi
			mBusLineIDList.clear();
			for (PoiInfo poi : result.getAllPoi()) {
				if (poi.type == PoiInfo.POITYPE.BUS_LINE
						|| poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
					mBusLineIDList.add(poi.uid);
				}
			}
			searchBusline(result.getAllPoi().get(0).city);
			mBusRoute = null;
		}
	}

	@Override
	public void onGetBusLineResult(BusLineResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			jsonNoResultCallback();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			jsonBusLineResultCallback(result);
			mBaiduMap.clear();
			mBusRoute = result;
			busNodeIndex = -1;
			mMyBusLineOverlay = new MyBusLineOverlay(mBaiduMap);
			mMyBusLineOverlay.setData(result);
			mMyBusLineOverlay.addToMap();
			mMyBusLineOverlay.zoomToSpan();
			mBaiduMap.setOnMarkerClickListener(mMyBusLineOverlay);
		}
	}

	private class MyBusLineOverlay extends BusLineOverlay {
		public MyBusLineOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}
		
		@Override
		public boolean onBusStationClick(int index) {
			mBaiduMap.hideInfoWindow();
			busNodeIndex = index;
			showBusNode();
			return false;
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			jsonNoResultCallback();
		} else {
			Toast.makeText(mContext,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void jsonBusLineResultCallback(BusLineResult result) {
		EBaiduMapBaseActivity activity;
		activity = (EBaiduMapBaseActivity) mContext;
		if (activity != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
				JSONObject jsonBusLine = new JSONObject();
				jsonBusLine.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSLINENAME,
						result.getBusLineName());
				jsonBusLine.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSCOMPANY,
						result.getBusCompany());
				jsonBusLine.put(
						EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSSTARTTIME,
						dateFormat.format(result.getStartTime()));
				jsonBusLine.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSENDTIME,
						dateFormat.format(result.getEndTime()));

				JSONArray jsonBusStationArray = new JSONArray();
				for (BusStation busStation : result.getStations()) {
					JSONObject jsonBusStation = new JSONObject();
					if (busStation.getLocation() != null) {
						jsonBusStation.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG,
								busStation.getLocation().longitude);
						jsonBusStation.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT,
								busStation.getLocation().latitude);
					}
					jsonBusStation.put(
							EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TITLE,
							busStation.getTitle());
					jsonBusStationArray.put(jsonBusStation);
				}
				jsonBusLine.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUSSTATION,
						jsonBusStationArray);

				EUExBaiduMap uexBaiduMap = activity.getUexBaseObj();
				String js = EUExBaiduMap.SCRIPT_HEADER + "if("
						+ EBaiduMapUtils.MAP_FUN_CB_BUSLINE_SEARCH_RESULT
						+ "){"
						+ EBaiduMapUtils.MAP_FUN_CB_BUSLINE_SEARCH_RESULT
						+ "('" + jsonBusLine.toString() + "');}";
				uexBaiduMap.onCallback(js);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void jsonNoResultCallback() {
		EBaiduMapBaseActivity activity;
		activity = (EBaiduMapBaseActivity) mContext;
		if (activity != null) {
			EUExBaiduMap uexBaiduMap = activity.getUexBaseObj();
			String js = EUExBaiduMap.SCRIPT_HEADER + "if("
					+ EBaiduMapUtils.MAP_FUN_CB_BUSLINE_SEARCH_RESULT + "){"
					+ EBaiduMapUtils.MAP_FUN_CB_BUSLINE_SEARCH_RESULT + "('" + null
					+ "');}";
			uexBaiduMap.onCallback(js);
		}
	}
	
	/**
	 * 公交线路的上一个节点
	 */
	public void preBusLineNode() {
		if (busNodeIndex > 0) {
			// 索引减
			busNodeIndex--;
			showBusNode();
		}
	}

	/**
	 * 公交线路的下一个节点
	 */
	public void nextBusLineNode() {
		if (mBusRoute != null
				&& busNodeIndex < (mBusRoute.getStations().size() - 1)) {
			// 索引加
			busNodeIndex++;
			showBusNode();
		}
	}

	/**
	 * 公交节点显示
	 */
	private void showBusNode() {
		if (busNodeIndex < -1 || mBusRoute == null
				|| busNodeIndex >= mBusRoute.getStations().size())
			return;
		showBusStationsInfo(mBusRoute.getStations().get(busNodeIndex)
				.getLocation(), mBusRoute.getStations().get(busNodeIndex)
				.getTitle());
	}

	private void showBusStationsInfo(LatLng location, String title) {
		if (location == null || title == null) {
            return;
        }
		// 移动到指定索引的坐标
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
		// 弹出泡泡
		TextView popupText = new TextView(mContext);
		popupText.setBackgroundResource(EUExUtil.getResDrawableID("plugin_map_bubble_bg_default"));
		popupText.setTextColor(0xFF000000);
		popupText.setText(title);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, location, 0));
	}
	
	public void removeBusLine() {
		if (mMyBusLineOverlay != null) {
			mBaiduMap.hideInfoWindow();
			mMyBusLineOverlay.removeFromMap();
			mBusRoute = null;
			mMyBusLineOverlay = null;
		}
	}

	public void destroy() {
		mBusLinePoiSearch.destroy();
		mBusLineSearch.destroy();
	}
}
