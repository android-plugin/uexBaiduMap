package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Polygon;

public class EBaiduMapPolygonOverlay extends EBaiduMapOverlay {

	private Polygon polygon = null;
	
	public EBaiduMapPolygonOverlay(String id, EBaiduMapBaseFragment context, BaiduMap baiduMap) {
		super(id, context, baiduMap);
	}
	
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}

	@Override
	public void clearOverlay() {
		if(polygon != null) {
			polygon.remove();
		}
	}

}
