package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;
import com.baidu.mapapi.map.BaiduMap;


public abstract class EBaiduMapOverlay {

    protected String mIDString;
    protected EBaiduMapBaseFragment mBaseFragment;
    protected Context mContext;
    protected BaiduMap mBaiduMap;

    public EBaiduMapOverlay(String id, EBaiduMapBaseFragment context, BaiduMap baiduMap) {

        mIDString = id;
        mBaseFragment = context;
        mContext = context.getActivity();
        mBaiduMap = baiduMap;
    }

    public abstract void clearOverlay();
}
