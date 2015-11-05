package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Polyline;

public class EBaiduMapPolylineOverlay extends EBaiduMapOverlay {

	private Polyline polyline = null;
	public EBaiduMapPolylineOverlay(String id, EBaiduMapBaseFragment context,
			BaiduMap baiduMap) {
		super(id, context, baiduMap);
	}
	
	public void setPolyline(Polyline polyline) {
		this.polyline = polyline;
	}

	@Override
	public void clearOverlay() {
		if(polyline != null) {
			polyline.remove();
		}
	}

}
