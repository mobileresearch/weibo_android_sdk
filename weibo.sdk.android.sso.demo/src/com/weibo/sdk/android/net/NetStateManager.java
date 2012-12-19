package com.weibo.sdk.android.net;

import org.apache.http.HttpHost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class NetStateManager {
	private static Context mContext;

	public static NetState CUR_NETSTATE = NetState.Mobile;

	public enum NetState {
		Mobile, WIFI, NOWAY
	}

	public class NetStateReceive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext = context;
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = wifiManager.getConnectionInfo();
				if (!wifiManager.isWifiEnabled() || -1 == info.getNetworkId()) {
					CUR_NETSTATE = NetState.Mobile;
				}
			}
		}

	}
	/**
	 * 获取 当前apn并返回httphost对象
	 * @return 
	 */
	public static HttpHost getAPN() {
		HttpHost proxy = null;
		Uri uri = Uri.parse("content://telephony/carriers/preferapn");
		Cursor mCursor = null;
		if (null != mContext) {
			mCursor = mContext.getContentResolver().query(uri, null, null, null, null);
		}
		if (mCursor != null && mCursor.moveToFirst()) {
			// 游标移至第一条记录，当然也只有一条
			String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
			if (proxyStr != null && proxyStr.trim().length() > 0) {
				proxy = new HttpHost(proxyStr, 80);
			}
			mCursor.close();
		}
		return proxy;
	}
}
