package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

public class EBaiduMapArcOptions extends EBaiduMapOverlayOptions {
	private String strokeColor;
	private String lineWidth;
	private LatLng start;
	private LatLng center;
	private LatLng end;
	public String getStrokeColor() {
		return strokeColor;
	}
	public void setStrokeColor(String strokeColor) {
		this.strokeColor = strokeColor;
	}
	public String getLineWidth() {
		return lineWidth;
	}
	public void setLineWidth(String lineWidth) {
		this.lineWidth = lineWidth;
	}
	public LatLng getStart() {
		return start;
	}
	public void setStart(Double latitude, Double longitude) {
		this.start = new LatLng(latitude, longitude);
	}
	public LatLng getCenter() {
		return center;
	}
	public void setCenter(Double latitude, Double longitude) {
		this.center = new LatLng(latitude, longitude);
	}
	public LatLng getEnd() {
		return end;
	}
	public void setEnd(Double latitude, Double longitude) {
		this.end = new LatLng(latitude, longitude);
	}
}
