package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

/**
 * 百度地图一些不需要打开地图的功能管理器
 * 
 * @author waka
 *
 */
public class EBaiduMapBaseNoMapViewManager {

	// 须传入的参数
	private Context mContext;// 上下文对象
	private EUExBaiduMap mEUExBaiduMap;// EUExBaiduMap对象

	private EBaiduMapPoiSearch mEBaiduMapPoiSearch = null;

	/**
	 *  Construct 构造方法
	 *   
	 * @param euExBaiduMap 
	 * 						须传入EUExBaiduMap的实例
	 */
	public EBaiduMapBaseNoMapViewManager(Context context, EUExBaiduMap euExBaiduMap) {
		mContext = context;
		mEUExBaiduMap = euExBaiduMap;

		mEBaiduMapPoiSearch = new EBaiduMapPoiSearch(this);
	}

	/**
	 * 销毁方法
	 */
	public void destory() {
		mEBaiduMapPoiSearch.destroy();
	}

	/**
	 * 在城市内搜索 
	 * 
	 * @param city
	 * @param searchKey
	 * @param pageNum
	 */
	public void poiSearchInCity(String city, String searchKey, int pageNum) {
		mEBaiduMapPoiSearch.poiSearchInCity(city, searchKey, pageNum);
	}

	/**
	 * 附近搜索
	 * 
	 * @param lng
	 * @param lat
	 * @param radius
	 * @param searchKey
	 * @param pageNum
	 */
	public void poiNearbySearch(double lng, double lat, int radius, String searchKey, int pageNum) {
		mEBaiduMapPoiSearch.poiNearbySearch(lng, lat, radius, searchKey, pageNum);
	}

	/**
	 * 区域搜索 
	 * 
	 * @param east
	 * @param north
	 * @param west
	 * @param south
	 * @param searchKey
	 * @param pageNum
	 */
	public void poiBoundSearch(double east, double north, double west, double south, String searchKey, int pageNum) {
		mEBaiduMapPoiSearch.poiBoundSearch(east, north, west, south, searchKey, pageNum);
	}

	/*---------------------------------get,set方法-------------------------------------*/

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public EUExBaiduMap getmEUExBaiduMap() {
		return mEUExBaiduMap;
	}

	public void setmEUExBaiduMap(EUExBaiduMap mEUExBaiduMap) {
		this.mEUExBaiduMap = mEUExBaiduMap;
	}
}
