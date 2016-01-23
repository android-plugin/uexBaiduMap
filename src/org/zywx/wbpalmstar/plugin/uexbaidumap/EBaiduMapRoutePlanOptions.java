package org.zywx.wbpalmstar.plugin.uexbaidumap;

import com.baidu.mapapi.search.route.PlanNode;

public class EBaiduMapRoutePlanOptions {
	// 驾车
	public static final int PLAN_TYPE_DRIVE = 0;
	// 公交
	public static final int PLAN_TYPE_TRANS = 1;
	// 步行
	public static final int PLAN_TYPE_WALK = 2;
	private String id;
	private int type;
	private String startCity;
	private PlanNode startNode;
	private String endCity;
	private PlanNode endNode;

	public EBaiduMapRoutePlanOptions() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String getStartCity() {
		return startCity;
	}

	public void setStartCity(String startCity) {
		this.startCity = startCity;
	}

	public PlanNode getStartNode() {
		return startNode;
	}

	public String getEndCity() {
		return endCity;
	}

	public void setEndCity(String endCity) {
		this.endCity = endCity;
	}

	public PlanNode getEndNode() {
		return endNode;
	}

	public void setStartNode(PlanNode startNode) {
		this.startNode = startNode;
	}

	public void setEndNode(PlanNode endNode) {
		this.endNode = endNode;
	}

	public static interface OnRoutePlanCallback {
		void onRoutePlanResultOk(boolean success);
	}
}
