package com.weibo.sdk.android.util;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.weibo.sdk.android.WeiboParameters;

public class Utility {
    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static byte[] decodes = new byte[256];  
	public static Bundle parseUrl(String url) {
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
			}
		}
		return params;
	}

	public static String encodeUrl(WeiboParameters parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++) {
			if (first){
			    first = false;
			}
			else{
			    sb.append("&");
			}
			String _key=parameters.getKey(loc);
			String _value=parameters.getValue(_key);
			if(_value==null){
			    Log.i("encodeUrl", "key:"+_key+" 's value is null");
			}
			else{
			    sb.append(URLEncoder.encode(parameters.getKey(loc)) + "="
                        + URLEncoder.encode(parameters.getValue(loc)));
			}
			
		}
		return sb.toString();
	}

	public static String encodeParameters(WeiboParameters httpParams) {
		if (null == httpParams || isBundleEmpty(httpParams)) {
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++) {
			String key = httpParams.getKey(loc);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(httpParams.getValue(key), "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();

	}

	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	private static boolean isBundleEmpty(WeiboParameters bundle) {
		if (bundle == null || bundle.size() == 0) {
			return true;
		}
		return false;
	}
	/**
	 * 将data编码成Base62的字符串
	 * @param data 
	 * @return
	 */
	public static String encodeBase62(byte[] data) {  
	    StringBuffer sb = new StringBuffer(data.length * 2);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        val = (val << 8) | (data[i] & 0xFF);  
	        pos += 8;  
	        while (pos > 5) {  
	            char c = encodes[val >> (pos -= 6)];  
	            sb.append(  
	            /**/c == 'i' ? "ia" :  
	            /**/c == '+' ? "ib" :  
	            /**/c == '/' ? "ic" : c);  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    if (pos > 0) {  
	        char c = encodes[val << (6 - pos)];  
	        sb.append(  
	        /**/c == 'i' ? "ia" :  
	        /**/c == '+' ? "ib" :  
	        /**/c == '/' ? "ic" : c);  
	    }  
	    return sb.toString();  
	}  
	  /**
	   * 将字符串解码成byte数组
	   * @param data
	   * @return
	   */
	public static byte[] decodeBase62(String string) {  
	    if(string==null){
	        return null;
	    }
	    char[] data=string.toCharArray();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(string.toCharArray().length);  
	    int pos = 0, val = 0;  
	    for (int i = 0; i < data.length; i++) {  
	        char c = data[i];  
	        if (c == 'i') {  
	            c = data[++i];  
	            c =  
	            /**/c == 'a' ? 'i' :  
	            /**/c == 'b' ? '+' :  
	            /**/c == 'c' ? '/' : data[--i];  
	        }  
	        val = (val << 6) | decodes[c];  
	        pos += 6;  
	        while (pos > 7) {  
	            baos.write(val >> (pos -= 8));  
	            val &= ((1 << pos) - 1);  
	        }  
	    }  
	    return baos.toByteArray();  
	}  
}
