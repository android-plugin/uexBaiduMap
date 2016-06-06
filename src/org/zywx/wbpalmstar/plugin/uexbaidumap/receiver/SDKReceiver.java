package org.zywx.wbpalmstar.plugin.uexbaidumap.receiver;

import org.zywx.wbpalmstar.plugin.uexbaidumap.utils.MLog;

import com.baidu.mapapi.SDKInitializer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 验证Key是否配置正确的BroadcastReceiver
 * 
 * @author waka
 * @version createTime:2016年5月25日 上午9:33:21
 */
public class SDKReceiver extends BroadcastReceiver {

	/**
	 * onReceive
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		MLog.getIns().i("action = " + intent.getAction());

		if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {

			MLog.getIns().e("key验证出错! 请在AndroidManifest.xml文件中检查key设置");
			Toast.makeText(context, "key验证出错! 请在AndroidManifest.xml文件中检查key设置", Toast.LENGTH_LONG).show();

		} else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {

			MLog.getIns().d("key 验证成功! 功能可以正常使用");

		} else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {

			MLog.getIns().e("网络出错");
			Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();

		}
	}

}
