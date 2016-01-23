package org.zywx.wbpalmstar.plugin.uexbaidumap;

/**
 * Created with IntelliJ IDEA.
 * User: liguangqiao
 * Date: 14/11/19
 * Time: 下午7:13
 * To change this template use File | Settings | File Templates.
 */
public abstract class EBaiduMapOverlayOptions {

    private String idStr;
    private String extraStr;
	private String visibleStr;
	private String zIndexStr;

    public EBaiduMapOverlayOptions() {

    }
	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public String getExtraStr() {
		return extraStr;
	}

	public void setExtraStr(String extraStr) {
		this.extraStr = extraStr;
	}

	public String getVisibleStr() {
		return visibleStr;
	}

	public void setVisibleStr(String visibleStr) {
		this.visibleStr = visibleStr;
	}

	public String getzIndexStr() {
		return zIndexStr;
	}

	public void setzIndexStr(String zIndexStr) {
		this.zIndexStr = zIndexStr;
	}

}
