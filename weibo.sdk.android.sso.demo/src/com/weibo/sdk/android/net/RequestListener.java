package com.weibo.sdk.android.net;

import java.io.IOException;

import com.weibo.sdk.android.WeiboException;

/**
 * 发起访问接口的请求时所需的回调接口
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public interface RequestListener {
    /**
     * 用于获取服务器返回的响应内容
     * @param response
     */
	public void onComplete(String response);

	public void onIOException(IOException e);

	public void onError(WeiboException e);

}
