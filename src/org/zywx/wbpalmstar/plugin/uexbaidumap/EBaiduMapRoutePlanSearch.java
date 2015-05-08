package org.zywx.wbpalmstar.plugin.uexbaidumap;

import java.util.HashMap;

import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class EBaiduMapRoutePlanSearch implements OnGetRoutePlanResultListener {
	private String TAG = "EBaiduMapRoutePlanSearch";
	protected Context mContext;
	protected BaiduMap mBaiduMap;
	protected MapView mMapView;
	private HashMap<String, OverlayManager> mRoutePlanOverlays;
	private EBaiduMapRoutePlanOptions mRoutePlanOptions;
	private RoutePlanSearch mRoutePlanSearch = null;
    private RouteLine mRouteLine = null; //保存路径数据的变量，供浏览节点时使用
	private int routeNodeIndex = -1; //路径节点索引,供浏览节点时使用

	public EBaiduMapRoutePlanSearch(Context context, BaiduMap baiduMap,
			MapView mapView) {
		mContext = context;
		mBaiduMap = baiduMap;
		mMapView = mapView;
		mRoutePlanOverlays = new HashMap<String, OverlayManager>();
		// 初始化搜索模块，注册搜索事件监听
		mRoutePlanSearch = RoutePlanSearch.newInstance();
		mRoutePlanSearch.setOnGetRoutePlanResultListener(this);
	}
	
    /**
     * 发起路线规划搜索
     * @param routePlanOptions
     */
	public void searchRoutePlan(EBaiduMapRoutePlanOptions routePlanOptions) {
		Log.i(TAG, "searchRoutePlan");
        //重置浏览节点的路线数据
        mRouteLine = null;
        mBaiduMap.clear();
        mRoutePlanOptions = routePlanOptions;
        //设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = routePlanOptions.getStartNode();
        PlanNode enNode = routePlanOptions.getEndNode();

        // 处理搜索按钮响应
        // 实际使用中请对起点终点城市进行正确的设定
        switch (routePlanOptions.getType()) {
			case EBaiduMapRoutePlanOptions.PLAN_TYPE_DRIVE:
				mRoutePlanSearch.drivingSearch((new DrivingRoutePlanOption())
						.from(stNode).to(enNode));
				break;
			case EBaiduMapRoutePlanOptions.PLAN_TYPE_WALK:
				mRoutePlanSearch.walkingSearch((new WalkingRoutePlanOption())
						.from(stNode).to(enNode));
				break;
			case EBaiduMapRoutePlanOptions.PLAN_TYPE_TRANS:
				mRoutePlanSearch.transitSearch((new TransitRoutePlanOption())
						.from(stNode).to(enNode).city(routePlanOptions.getStartCity()));
				break;
    		default:
    			break;
		}
    }
	
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null) {
        	Log.i(TAG, "onGetDrivingRouteResult result is null");
			return;
		}
		jsonRouteResultCallback(result);
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            routeNodeIndex = -1;
            mRouteLine = result.getRouteLines().get(0);
            MyDrivingRouteOverlay drivingRouteOverlay = new MyDrivingRouteOverlay(
					mBaiduMap);
			drivingRouteOverlay.setData(result.getRouteLines().get(0));
			drivingRouteOverlay.addToMap();
			drivingRouteOverlay.zoomToSpan();
			mBaiduMap.setOnMarkerClickListener(drivingRouteOverlay);
			mRoutePlanOverlays.put(mRoutePlanOptions.getId(),
					drivingRouteOverlay);
        }
	}

	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onRouteNodeClick(int index) {
			mBaiduMap.hideInfoWindow();
			routeNodeIndex = index;
        	showRouteNode();
			return false;
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		if (result == null) {
        	Log.i(TAG, "onGetTransitRouteResult result is null");
			return;
		}
		jsonRouteResultCallback(result);
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            routeNodeIndex = -1;
            mRouteLine = result.getRouteLines().get(0);
            MyTransitRouteOverlay transitRouteOverlay = new MyTransitRouteOverlay(
					mBaiduMap);
			transitRouteOverlay.setData(result.getRouteLines().get(0));
			transitRouteOverlay.addToMap();
			transitRouteOverlay.zoomToSpan();
			mBaiduMap.setOnMarkerClickListener(transitRouteOverlay);
			mRoutePlanOverlays.put(mRoutePlanOptions.getId(),
					transitRouteOverlay);
        }
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {
		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onRouteNodeClick(int index) {
			mBaiduMap.hideInfoWindow();
			routeNodeIndex = index;
        	showRouteNode();
			return false;
		}
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null) {
        	Log.i(TAG, "onGetWalkingRouteResult result is null");
			return;
		}
		jsonRouteResultCallback(result);
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            routeNodeIndex = -1;
            mRouteLine = result.getRouteLines().get(0);
			MyWalkingRouteOverlay walkingRouteOverlay = new MyWalkingRouteOverlay(
					mBaiduMap);
			walkingRouteOverlay.setData(result.getRouteLines().get(0));
			walkingRouteOverlay.addToMap();
			walkingRouteOverlay.zoomToSpan();
			mBaiduMap.setOnMarkerClickListener(walkingRouteOverlay);
			mRoutePlanOverlays.put(mRoutePlanOptions.getId(),
					walkingRouteOverlay);
        }
	}
	
	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {
		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onRouteNodeClick(int index) {
			mBaiduMap.hideInfoWindow();
			routeNodeIndex = index;
        	showRouteNode();
			return false;
		}
	}
	
    /**
     * 得到线路的上一个节点
     */
    public void preRouteNode() {
        if (routeNodeIndex == -1) {
        	return;
        }
        //设置节点索引
    	if (routeNodeIndex > 0) {
    		routeNodeIndex--;
        	showRouteNode();
    	} else {
        	return;
        }
    }
    
    /**
     * 得到线路的下一个节点
     */
    public void nextRouteNode() {
        //设置节点索引
        if (mRouteLine != null 
        		&& routeNodeIndex < mRouteLine.getAllStep().size() - 1) {
        	routeNodeIndex++;
        	showRouteNode();
        } else {
        	return;
        }
    }
	
    /**
     * 路径节点显示
     */
    private void showRouteNode() {
        if (mRouteLine == null ||
        		mRouteLine.getAllStep() == null) {
            return;
        }
        //获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = mRouteLine.getAllStep().get(routeNodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }
		showRouteNodeInfo(nodeLocation, nodeTitle);
    }

	private void showRouteNodeInfo(LatLng location, String title) {
		if (location == null || title == null) {
            return;
        }
		// 移动到指定索引的坐标
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(location));
		// 弹出泡泡
		TextView popupText = new TextView(mContext);
		popupText.setBackgroundResource(EUExUtil.getResDrawableID("plugin_map_bubble_bg_default"));
		popupText.setTextColor(0xFF000000);
		popupText.setText(title);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, location, 0));
	}
	
	/**
	 * 清除路线
	 * 
	 * @param routePlanOptions
	 */
	public void removeRoutePlan(String routePlanId) {
		OverlayManager overlayManager = mRoutePlanOverlays.get(routePlanId);
		if (overlayManager != null) {
			overlayManager.removeFromMap();
			mRoutePlanOverlays.remove(routePlanId);
		}
		mBaiduMap.hideInfoWindow();
		mRouteLine = null;
	}

	public void destroy() {
		mRoutePlanSearch.destroy();
	}
	
	private void jsonRouteResultCallback(SearchResult result) {
		EBaiduMapBaseActivity activity;
		activity = (EBaiduMapBaseActivity) mContext;
		if (activity != null) {
			int resultId = -1;
			switch (result.error) {
			case NO_ERROR:
				resultId = 0;
				break;
			case AMBIGUOUS_KEYWORD:
				resultId = 1;
				break;
			case AMBIGUOUS_ROURE_ADDR:
				resultId = 2;
				break;
			case NOT_SUPPORT_BUS:
				resultId = 3;
				break;
			case NOT_SUPPORT_BUS_2CITY:
				resultId = 4;
				break;
			case RESULT_NOT_FOUND:
				resultId = 5;
				break;
			case ST_EN_TOO_NEAR:
				resultId = 6;
				break;
			default:
				break;
			}
			EUExBaiduMap uexBaiduMap = activity.getUexBaseObj();
			String js = EUExBase.SCRIPT_HEADER + "if("
					+ EBaiduMapUtils.MAP_FUN_ON_SEARCH_ROUTE_PLAN + "){"
					+ EBaiduMapUtils.MAP_FUN_ON_SEARCH_ROUTE_PLAN + "("
					+ resultId + ");}";
			uexBaiduMap.onCallback(js);
		}
	}
}
