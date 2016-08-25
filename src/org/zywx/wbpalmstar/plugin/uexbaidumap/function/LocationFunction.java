package org.zywx.wbpalmstar.plugin.uexbaidumap.function;

import android.content.Context;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.plugin.uexbaidumap.EBaiduMapUtils;
import org.zywx.wbpalmstar.plugin.uexbaidumap.EUExBaiduMap;
import org.zywx.wbpalmstar.plugin.uexbaidumap.utils.MLog;

/**
 * 定位功能
 *
 * @author waka
 * @version createTime:2016年4月26日 下午2:54:19
 */
public class LocationFunction implements BDLocationListener {

    private Context mContext;

    private EUExBaiduMap mEUExBaiduMap;

    private LocationClient mLocationClient;

    /**
     * 构造方法
     *
     * @param context:需要时全进程有效的context,推荐用getApplicationConext获取全进程有效的context
     */
    public LocationFunction(Context context, EUExBaiduMap uexBaiduMap) {

        mContext = context;
        mEUExBaiduMap = uexBaiduMap;

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mContext);
        }
        mLocationClient.registerLocationListener(this);// 注册监听

        initLocation();
    }

    /**
     * 初始化
     */
    private void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(2000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(false);// 可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);// 可选，默认false,设置是否使用gps
        option.setLocationNotify(false);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

    }

    public void start() {
        mLocationClient.start();
    }

    /**
     * 接收异步返回的定位结果
     */
    @Override
    public void onReceiveLocation(BDLocation location) {

        if (location == null) {
            MLog.getIns().i("location == null");
            jsonReceiveLocationCallback(null, EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION);
            return;
        }

        jsonReceiveLocationCallback(location, EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION);

        // 每次定位后停止定位服务
        mLocationClient.stop();
        // 取消注册监听
        mLocationClient.unRegisterLocationListener(this);
        mLocationClient = null;
        mContext = null;
        mEUExBaiduMap = null;
    }

    /**
     * 把数据返回给前端
     *
     * @param location
     * @param header
     */
    private void jsonReceiveLocationCallback(BDLocation location, String header) {

        if (mEUExBaiduMap != null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT, Double.toString(location.getLatitude()));
                jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG, Double.toString(location.getLongitude()));
                jsonObject.put(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TIMESTAMP, location.getTime());
                MLog.getIns().i("jsonObject = " + jsonObject.toString());
                String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + jsonObject.toString() + "');}";
                mEUExBaiduMap.onCallback(js);
                if (null != mEUExBaiduMap.getCurrentLocationFuncId && EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION.equals(header)) {
                    mEUExBaiduMap.callbackToJs(Integer.parseInt(mEUExBaiduMap.getCurrentLocationFuncId), false, jsonObject);
                }
            } catch (JSONException e) {
                String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + header + "){" + header + "('" + null + "');}";
                mEUExBaiduMap.onCallback(js);
                if (null != mEUExBaiduMap.getCurrentLocationFuncId && EBaiduMapUtils.MAP_FUN_CB_CURRENT_LOCATION.equals(header)) {
                    mEUExBaiduMap.callbackToJs(Integer.parseInt(mEUExBaiduMap.getCurrentLocationFuncId), false);
                }
                e.printStackTrace();
            }
        }
    }

}
