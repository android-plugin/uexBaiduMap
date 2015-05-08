package org.zywx.wbpalmstar.plugin.uexbaidumap;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapapi.model.LatLng;

public class EBaiduMapGroundOptions extends EBaiduMapOverlayOptions {
	private String imageUrl;
	private String transparency;
	private String groundWidth;
	private String groundHeight;
	private List<LatLng> list;
	
	public EBaiduMapGroundOptions() {
		list = new ArrayList<LatLng>();
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getTransparency() {
		return transparency;
	}
	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}
	public List<LatLng> getList() {
		return list;
	}
	public void addList(LatLng latLng) {
		list.add(latLng);
	}
	public String getGroundWidth() {
		return groundWidth;
	}
	public void setGroundWidth(String groundWidth) {
		this.groundWidth = groundWidth;
	}
	public String getGroundHeight() {
		return groundHeight;
	}
	public void setGroundHeight(String groundHeight) {
		this.groundHeight = groundHeight;
	}
	
}
