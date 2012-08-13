package com.weibo.sdk.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 此类封装了“access_token”，“expires_in”，"refresh_token"，
 *并提供了他们的管理功能
 * @author luopeng (luopeng@staff.sina.com.cn)
 */
public class Oauth2AccessToken {
	private String mAccessToken = "";
	private String mRefreshToken = "";
	private long mExpiresTime = 0;

//	private String mOauth_verifier = "";
//	protected String[] responseStr = null;
//	protected SecretKeySpec mSecretKeySpec;
	/**
	 * Oauth2AccessToken 的构造函数
	 */
	public Oauth2AccessToken() {
	}
	/**
	 * 根据服务器返回的responsetext生成Oauth2AccessToken 的构造函数，
	 * 此方法会将responsetext里的“access_token”，“expires_in”，"refresh_token"解析出来
	 * @param responsetext 服务器返回的responsetext
	 */
	public Oauth2AccessToken(String responsetext) {
		if (responsetext != null) {
			if (responsetext.indexOf("{") >= 0) {
				try {
					JSONObject json = new JSONObject(responsetext);
					setToken(json.optString("access_token"));
					setExpiresIn(json.optString("expires_in"));
					setRefreshToken(json.optString("refresh_token"));
				} catch (JSONException e) {
					
				}
			}
		}
	}
	/**
	 * Oauth2AccessToken的构造函数，根据accessToken 和expires_in 生成Oauth2AccessToken实例
	 * @param accessToken  访问令牌
	 * @param expires_in 有效期，单位：毫秒；仅当从服务器获取到expires_in时适用，表示距离超过认证时间还有多少秒
	 */
	public Oauth2AccessToken(String accessToken, String expires_in) {
		mAccessToken = accessToken;
		mExpiresTime = System.currentTimeMillis() + Long.parseLong(expires_in)*1000;
	}
	/**
	 *  AccessToken是否有效,如果accessToken为空或者expiresTime过期，返回false，否则返回true
	 *  @return 如果accessToken为空或者expiresTime过期，返回false，否则返回true
	 */
	public boolean isSessionValid() {
		return (!TextUtils.isEmpty(mAccessToken) && (mExpiresTime == 0 || (System
				.currentTimeMillis() < mExpiresTime)));
	}
	/**
	 * 获取accessToken
	 */
	public String getToken() {
		return this.mAccessToken;
	}
	/**
     * 获取refreshToken
     */
	public String getRefreshToken() {
		return mRefreshToken;
	}
	/**
	 * 设置refreshToken
	 * @param mRefreshToken
	 */
	public void setRefreshToken(String mRefreshToken) {
		this.mRefreshToken = mRefreshToken;
	}
	/**
	 * 获取超时时间，单位: 毫秒，表示从格林威治时间1970年01月01日00时00分00秒起至现在的总 毫秒数
	 */
	public long getExpiresTime() {
		return mExpiresTime;
	}

	/**
	 * 设置过期时间长度值，仅当从服务器获取到数据时使用此方法
	 *            
	 */
	public void setExpiresIn(String expiresIn) {
		if (expiresIn != null && !expiresIn.equals("0")) {
			setExpiresTime(System.currentTimeMillis() + Long.parseLong(expiresIn) * 1000);
		}
	}

	/**
	 * 设置过期时刻点 时间值
	 * @param mExpiresTime 单位：毫秒，表示从格林威治时间1970年01月01日00时00分00秒起至现在的总 毫秒数
	 *            
	 */
	public void setExpiresTime(long mExpiresTime) {
		this.mExpiresTime = mExpiresTime;
	}
	/**
	 * 设置accessToken
	 * @param mToken
	 */
	public void setToken(String mToken) {
		this.mAccessToken = mToken;
	}
//	/**
//	 * 设置检验者
//	 * @param verifier
//	 */
//	public void setVerifier(String verifier) {
//		mOauth_verifier = verifier;
//	}
//	/**
//	 * 获取检验者
//	 * @return
//	 */
//	public String getVerifier() {
//		return mOauth_verifier;
//	}
//	
//	public String getParameter(String parameter) {
//		String value = null;
//		for (String str : responseStr) {
//			if (str.startsWith(parameter + '=')) {
//				value = str.split("=")[1].trim();
//				break;
//			}
//		}
//		return value;
//	}

//	protected void setSecretKeySpec(SecretKeySpec secretKeySpec) {
//		this.mSecretKeySpec = secretKeySpec;
//	}
//
//	protected SecretKeySpec getSecretKeySpec() {
//		return mSecretKeySpec;
//	}
}