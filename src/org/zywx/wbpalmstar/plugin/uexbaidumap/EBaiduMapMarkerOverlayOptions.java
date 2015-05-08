package org.zywx.wbpalmstar.plugin.uexbaidumap;

/**
 * Created with IntelliJ IDEA. User: liguangqiao Date: 14/11/19 Time: 下午7:19 To
 * change this template use File | Settings | File Templates.
 */
public class EBaiduMapMarkerOverlayOptions extends EBaiduMapOverlayOptions {
	private String lngStr;
	private String latStr;
	private String iconPath;
	private String bubbleTitle;
	private String bubbleSubTitle;
	private String bubbleBgImgPath;
	private int yOffset;
	private boolean iUseYOffset = false;

	public EBaiduMapMarkerOverlayOptions() {

	}
	
	public String getLngStr() {
		return lngStr;
	}

	public void setLngStr(String lngStr) {
		this.lngStr = lngStr;
	}

	public String getLatStr() {
		return latStr;
	}

	public void setLatStr(String latStr) {
		this.latStr = latStr;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getBubbleTitle() {
		return bubbleTitle;
	}

	public void setBubbleTitle(String bubbleTitle) {
		this.bubbleTitle = bubbleTitle;
	}

	public String getBubbleSubTitle() {
		return bubbleSubTitle;
	}

	public void setBubbleSubTitle(String bubbleSubTitle) {
		this.bubbleSubTitle = bubbleSubTitle;
	}

	public String getBubbleBgImgPath() {
		return bubbleBgImgPath;
	}

	public void setBubbleBgImgPath(String bubbleBgImgPath) {
		this.bubbleBgImgPath = bubbleBgImgPath;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public boolean isiUseYOffset() {
		return iUseYOffset;
	}

	public void setiUseYOffset(boolean iUseYOffset) {
		this.iUseYOffset = iUseYOffset;
	}
}
