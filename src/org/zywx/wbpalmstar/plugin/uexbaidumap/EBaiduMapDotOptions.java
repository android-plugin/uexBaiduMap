package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

public class EBaiduMapDotOptions extends EBaiduMapOverlayOptions {
	private String fillColor;
	private String radius;
	private LatLng latLng;
	
	public String getFillColor() {
		return fillColor;
	}
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public LatLng getLatLng() {
		return latLng;
	}
	public void setLatLng(Double latitude, Double longitude) {
		this.latLng = new LatLng(latitude, longitude);
	}
	
}
