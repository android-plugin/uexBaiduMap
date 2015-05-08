package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

import com.baidu.mapapi.map.Arc;
import com.baidu.mapapi.map.BaiduMap;

public class EBaiduMapArcOverlay extends EBaiduMapOverlay {

	private Arc arc = null;
	
	public EBaiduMapArcOverlay(String id, Context context, BaiduMap baiduMap) {
		super(id, context, baiduMap);
	}
	
	public void setArc(Arc arc) {
		this.arc = arc;
	}

	@Override
	public void clearOverlay() {
		if(arc != null) {
			arc.remove();
		}
	}

}
