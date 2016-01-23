package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Text;

public class EBaiduMapTextOverlay extends EBaiduMapOverlay {

	private Text text = null;
	
	public EBaiduMapTextOverlay(String id, Context context, BaiduMap baiduMap) {
		super(id, context, baiduMap);
	}

	public void setText(Text text) {
		this.text = text;
	}
	@Override
	public void clearOverlay() {
		if(text != null) {
			text.remove();
		}
	}

}
