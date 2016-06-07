package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.GroundOverlay;

public class EBaiduMapGroundOverlay extends EBaiduMapOverlay {

	private GroundOverlay groundOverlay = null;
	
	public EBaiduMapGroundOverlay(String id, EBaiduMapBaseFragment context, BaiduMap baiduMap) {
		super(id, context, baiduMap);
	}
	
	public void setGroundOverlay(GroundOverlay groundOverlay) {
		this.groundOverlay = groundOverlay;
	}

	@Override
	public void clearOverlay() {
		if(groundOverlay != null) {
			groundOverlay.getImage().recycle();
			groundOverlay.remove();
		}
	}

}
