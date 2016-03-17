package org.zywx.wbpalmstar.plugin.uexbaidumap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

public class EBaiduMapPoiSearch implements OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
	private String TAG = "EBaiduMapPoiSearch";
	protected Context mContext;
	protected BaiduMap mBaiduMap;
	protected MapView mMapView;
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;

	// change by waka
	// 记录下传入的经纬度
	private double mLongitude;
	private double mLatitude;

	private EBaiduMapBaseNoMapViewManager mEBaiduMapBaseNoMapViewManager;

	/**
	 * 不开启MapView的构造方法
	 * 
	 * @param mapBaseNoMapViewManager
	 */
	public EBaiduMapPoiSearch(EBaiduMapBaseNoMapViewManager mapBaseNoMapViewManager) {
		mContext = mapBaseNoMapViewManager.getmContext();
		mEBaiduMapBaseNoMapViewManager = mapBaseNoMapViewManager;
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
	}

	public EBaiduMapPoiSearch(Context context, BaiduMap baiduMap, MapView mapView) {
		mContext = context;
		mBaiduMap = baiduMap;
		mMapView = mapView;
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
	}

	public void poiSearchInCity(String city, String searchKey, int pageNum) {
		Log.i(TAG, "poiSearchInCity");
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city).keyword(searchKey).pageNum(pageNum));
	}

	public void poiNearbySearch(double lng, double lat, int radius, String searchKey, int pageNum) {
		Log.i(TAG, "poiNearbySearch");

		mLongitude = lng;
		mLatitude = lat;

		LatLng ll = new LatLng(lat, lng);
		mPoiSearch.searchNearby(
				(new PoiNearbySearchOption()).location(ll).radius(radius).keyword(searchKey).pageNum(pageNum));
	}

	public void poiBoundSearch(double east, double north, double west, double south, String searchKey, int pageNum) {
		Log.i(TAG, "poiBoundSearch");
		LatLng northeast = new LatLng(north, east);
		LatLng southwest = new LatLng(south, west);
		LatLngBounds bounds = new LatLngBounds.Builder().include(northeast).include(southwest).build();
		mPoiSearch.searchInBound((new PoiBoundSearchOption()).bound(bounds).keyword(searchKey).pageNum(pageNum));
	}

	/**
	 * 使用建议搜索服务获取建议列表，结果在onGetSuggestionResult()中更新
	 */
	public void requestSuggestion(String city, String key, double lat, double lng) {
		Log.i(TAG, "requestSuggestion");
		LatLng ll = new LatLng(lat, lng);
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption()).keyword(key).city(city).location(ll));
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			jsonNoResultCallback();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.i(TAG, "onGetPoiResult NO_ERROR");
			jsonPoiResultCallback(result);
			if (mBaiduMap != null) {
				mBaiduMap.clear();
			}
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
			Log.i(TAG, "onGetPoiResult AMBIGUOUS_KEYWORD");
			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = EUExUtil.getString("plugin_baidu_map_suggest_city_list");
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			Toast.makeText(mContext, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			jsonNoResultCallback();
		} else {
			Toast.makeText(mContext, result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT).show();
		}
	}

	private class MyPoiOverlay extends PoiOverlay {
		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
			// }
			return true;
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult result) {
		if (result == null || result.getAllSuggestions() == null) {
			return;
		}
		for (SuggestionResult.SuggestionInfo info : result.getAllSuggestions()) {
			// TODO Auto-generated method stub
		}
	}

	private void jsonPoiResultCallback(PoiResult result) {
		try {
			JSONObject jsonPoi = new JSONObject();
			jsonPoi.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TOTALPOINUM, result.getTotalPoiNum());
			jsonPoi.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TOTALPAGENUM, result.getTotalPageNum());
			jsonPoi.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CURRENTPAGENUM, result.getCurrentPageNum());
			jsonPoi.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TOTALPOINUM, result.getCurrentPageCapacity());

			JSONArray jsonPoiInfoArray = new JSONArray();
			for (PoiInfo poi : result.getAllPoi()) {
				JSONObject jsonPoiInfo = new JSONObject();
				if (poi.location != null) {
					jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, poi.location.longitude);
					jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, poi.location.latitude);
					jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_DISTANCE, MyDistanceUtils.getDistance(mLongitude,
							mLatitude, poi.location.longitude, poi.location.latitude));
				}
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_NAME, poi.name);
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_UID, poi.uid);
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ADDRESS, poi.address);
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_CITY, poi.city);
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_PHONENUM, poi.phoneNum);
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_POSTCODE, poi.postCode);
				String poiType = null;
				switch (poi.type) {
				case POINT:
					poiType = "0";
					break;
				case BUS_STATION:
					poiType = "1";
					break;
				case BUS_LINE:
					poiType = "2";
					break;
				case SUBWAY_STATION:
					poiType = "3";
					break;
				case SUBWAY_LINE:
					poiType = "4";
					break;
				default:
					break;
				}
				jsonPoiInfo.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_POITYPE, poiType);
				jsonPoiInfoArray.put(jsonPoiInfo);
			}
			jsonPoi.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_POIINFO, jsonPoiInfoArray);

			EUExBaiduMap uexBaiduMap = null;

			// change by waka
			if (mEBaiduMapBaseNoMapViewManager != null) {
				uexBaiduMap = mEBaiduMapBaseNoMapViewManager.getmEUExBaiduMap();
			} else if (mContext != null && mContext instanceof EBaiduMapBaseActivity) {
				uexBaiduMap = ((EBaiduMapBaseActivity) mContext).getUexBaseObj();
			} else {
				return;
			}

			String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_CB_POISEARCH_RESULT + "){"
					+ EBaiduMapUtils.MAP_FUN_CB_POISEARCH_RESULT + "('" + jsonPoi.toString() + "');}";
			uexBaiduMap.onCallback(js);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void jsonNoResultCallback() {

		EUExBaiduMap uexBaiduMap = null;

		// 先看NoMapViewManager是否为空
		if (mEBaiduMapBaseNoMapViewManager != null) {
			uexBaiduMap = mEBaiduMapBaseNoMapViewManager.getmEUExBaiduMap();
		}
		// 再看mContext是否是EBaiduMapBaseActivity的实例
		else if (mContext != null && mContext instanceof EBaiduMapBaseActivity) {
			uexBaiduMap = ((EBaiduMapBaseActivity) mContext).getUexBaseObj();
		} else {
			return;
		}

		String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_CB_POISEARCH_RESULT + "){"
				+ EBaiduMapUtils.MAP_FUN_CB_POISEARCH_RESULT + "('" + null + "');}";
		uexBaiduMap.onCallback(js);
	}

	public void destroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
	}
}
