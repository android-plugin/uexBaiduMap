package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class EBaiduMapPolylineOptions extends EBaiduMapOverlayOptions {


    private List<LatLng> list;
    private String fillColor;
    private String strokeColor;
    private String lineWidth;

    public EBaiduMapPolylineOptions() {
        list = new ArrayList<LatLng>();
    }

    public List<LatLng> getList() {
        return list;
    }

    public void addList(LatLng latLng) {
        list.add(latLng);
    }

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

}
