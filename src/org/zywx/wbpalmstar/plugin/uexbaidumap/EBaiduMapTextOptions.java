package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.model.LatLng;

public class EBaiduMapTextOptions extends EBaiduMapOverlayOptions {
    private String fontSize;
    private LatLng latLng;
    private String text;
    private String bgColor;
    private String fontColor;
    private String rotate;

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(Double latitude, Double longitude) {
        this.latLng = new LatLng(latitude, longitude);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getRotate() {
        return rotate;
    }

    public void setRotate(String rotate) {
        this.rotate = rotate;
    }
}
