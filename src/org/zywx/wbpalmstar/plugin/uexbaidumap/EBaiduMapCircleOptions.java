package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

public class EBaiduMapCircleOptions extends EBaiduMapOverlayOptions {

	private String fillColor;
	private String strokeColor;
	private String lineWidth;
	private String radius;
	private LatLng centerPoint;
	public String getFillColor() {
		return fillColor;
	}
	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}
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
	public String getRadius() {
		return radius;
	}
	public void setRadius(String radius) {
		this.radius = radius;
	}
	public LatLng getCenterPoint() {
		return centerPoint;
	}
	public void setCenterPoint(Double latitude, Double longitude) {
		this.centerPoint = new LatLng(latitude, longitude);
	}
}
