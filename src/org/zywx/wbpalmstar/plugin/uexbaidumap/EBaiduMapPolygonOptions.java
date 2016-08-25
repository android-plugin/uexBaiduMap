package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装多边形overlay信息
 *
 * @author zhenyu.fang
 */
public class EBaiduMapPolygonOptions extends EBaiduMapOverlayOptions {

    private String fillColor;
    private String strokeColor;
    private String lineWidth;
    private List<LatLng> list;

    public EBaiduMapPolygonOptions() {
        list = new ArrayList<LatLng>();
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

    public List<LatLng> getList() {
        return list;
    }

    public void addList(LatLng latLng) {
        list.add(latLng);
    }

}
