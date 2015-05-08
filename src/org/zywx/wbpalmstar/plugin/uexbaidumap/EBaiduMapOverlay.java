package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;


public abstract class EBaiduMapOverlay {
	
	protected String mIDString;
	protected Context mContext;
	protected BaiduMap mBaiduMap;
	
	public EBaiduMapOverlay(String id, Context context, BaiduMap baiduMap) {
		
		mIDString = id;
		mContext = context;
		mBaiduMap = baiduMap;
	}
	public abstract void clearOverlay();
}
