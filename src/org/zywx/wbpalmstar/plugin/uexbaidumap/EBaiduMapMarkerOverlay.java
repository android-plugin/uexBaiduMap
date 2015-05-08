package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;


public class EBaiduMapMarkerOverlay extends EBaiduMapOverlay implements OnInfoWindowClickListener {

	private Marker mMarker = null;
	private View bubbleView = null;
	private InfoWindow mInfoWindow = null;
	private String bubbleTitleStr = null;
	private final int bubbleDefaultYOffset = -75;
	
	
	public EBaiduMapMarkerOverlay(String id, Context context, BaiduMap baiduMap) {
		
		super(id, context, baiduMap);

		
	}

	
	public void setMarker(Marker maker) {
		mMarker = maker;
	}
	
	public Marker getMarker() {
		return mMarker;
	}
	
	public void setBubbleViewData(String title, String subTitlte, String imgPath, int yOffset, boolean isUseYOffset) {
		
		Button bubbleBtn;
		
		if (title == null) {
			
			return;
		}
		
		setBubbleTitleStr(title);
		
		if (bubbleView == null) {
			
			int bubbleResId = EUExUtil.getResLayoutID("plugin_uexbaidumap_marker_bubble");
			bubbleView = LayoutInflater.from(mContext).inflate(bubbleResId, null);

		}
		
		bubbleBtn = (Button) bubbleView.findViewById(EUExUtil.getResIdID("imageView1"));

		

		String titleString;
		
		if (imgPath != null) { //
			
			Bitmap defaultImage = EBaiduMapUtils.getBitMapFromImageUrl(mContext, imgPath);
			
			if (defaultImage != null) {
				bubbleBtn.setBackgroundDrawable(EBaiduMapUtils.bgColorDrawableSelector(defaultImage,defaultImage));
			}
			
			titleString = bubbleTitleStr;
		} else { //使用默认的
			titleString = bubbleTitleStr;
			bubbleBtn.setBackgroundResource(EUExUtil.getResDrawableID("plugin_map_bubble_bg_default"));
		}
		
		bubbleBtn.setText(titleString);
		
		
		LatLng ll = mMarker.getPosition();
		
		if (isUseYOffset) { //重新生成对象，设置位置偏移
			
			mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(bubbleView), ll, yOffset, this);
		} else {
			mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(bubbleView), ll, bubbleDefaultYOffset, this);
		}
		
	}
	
	
	public void setBubbleShow(boolean isShow) {
			
		if (isShow == true) {
			
			if (bubbleTitleStr != null) {
				
				if (mInfoWindow != null) {
					mBaiduMap.showInfoWindow(mInfoWindow);
				}
				
			}
			
		} else {
			mBaiduMap.hideInfoWindow();
		}
		
	}

	public String getBubbleTitleStr() {
		return bubbleTitleStr;
	}

	public void setBubbleTitleStr(String bubbleTitleStr) {
		this.bubbleTitleStr = bubbleTitleStr;
	}

	@Override
	public void onInfoWindowClick() {
		// TODO Auto-generated method stub
		
		EBaiduMapBaseActivity activity = (EBaiduMapBaseActivity)mContext;
		
		if (activity != null && activity instanceof EBaiduMapBaseActivity) {
			
			EUExBaiduMap uexBaiduMap = activity.getUexBaseObj();
			
			String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_MAKER_BUBBLE_CLICK_LISTNER + "){" + EBaiduMapUtils.MAP_FUN_ON_MAKER_BUBBLE_CLICK_LISTNER + "('"
                    + mIDString
                    + "');}";
			uexBaiduMap.onCallback(js);
			
			String json = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_MARKER_BUBBLE_CLICK_LISTENER + "){" + EBaiduMapUtils.MAP_FUN_ON_MARKER_BUBBLE_CLICK_LISTENER + "('"
					+ mIDString
					+ "');}";
			uexBaiduMap.onCallback(json);

		}
		
	}


	@Override
	public void clearOverlay() {
		 mMarker.remove();
	     mMarker.getIcon().recycle();
	     bubbleView = null;
	     mInfoWindow = null;
	}


	
}
