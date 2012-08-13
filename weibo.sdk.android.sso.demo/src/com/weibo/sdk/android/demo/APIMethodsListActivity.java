package com.weibo.sdk.android.demo;

import java.io.IOException;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.demo.adapter.APIMethodListAdapter;
import com.weibo.sdk.android.net.RequestListener;

public class APIMethodsListActivity extends ListActivity {
	private Class clz = null;
	private Method[] methods = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String apiName=intent.getStringExtra("api");
		try {
			clz = Class.forName("com.weibo.sdk.android.api." + apiName);
			methods = clz.getDeclaredMethods();
			APIMethodListAdapter adapter = new APIMethodListAdapter(getApplicationContext(),
					methods);
			setListAdapter(adapter);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Method method = methods[position];
		String methodName=method.getName();
		Class<?>[] types = method.getParameterTypes();
		int size = types.length - 1;
		System.out.print("方法名："+methodName+":参数 ");
		for (int index = 1; index < size; index++) {
			System.out.print(types[index].getName());
			System.out.print("  ");
		}
		exeMehtod(methodName);
		System.out.println();
	}
	private void exeMehtod(String methodName){
        if("bilateralTimeline".equals(methodName)){//
            StatusesAPI statusApi=new StatusesAPI(MainActivity.accessToken);
            statusApi.bilateralTimeline(  0, 0, 50, 1, false, WeiboAPI.FEATURE.ALL, false, new MyRequestListener(methodName));
        }
        else if("friendsTimeline".equals(methodName)){
            StatusesAPI statusApi=new StatusesAPI(MainActivity.accessToken);
            statusApi.friendsTimeline(0, 0, 50 , 1, false,  FEATURE.ALL,false,new MyRequestListener(methodName));
        }
        
        
    }
	void displayResponse(final String method,final String response){
	    Log.i(MainActivity.TAG, response);
	    this.runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                Util.showToast(APIMethodsListActivity.this,method+":数据已获取，请查看logcat");
            }
        });
	    
	}

	class MyRequestListener implements RequestListener {   
	    private String methodName;
	    public MyRequestListener(String method){
	        methodName=method;
	    }
        @Override
        public void onComplete(String response) {
            displayResponse(methodName,response);
            if("friendsTimeline".equals(methodName)){//取到好友的微博
                try {
                    Log.i(MainActivity.TAG, response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array=jsonObject.getJSONArray("statuses");
                    String[] ids=new String[array.length()];//获取关注的好友的最新微博的id
                    
                    for(int i=0;i<array.length();i++){
                        JSONObject child=array.getJSONObject(i);
                        ids[i]=child.getString("id");
                    }
                    StatusesAPI statusApi=new StatusesAPI(MainActivity.accessToken);//批量获取指定微博的转发数评论数
                    statusApi.count(ids, new MyRequestListener("count"));
                    
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if("count".equals(methodName)){//批量获取指定微博的转发数评论数
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject child=jsonArray.getJSONObject(i);
                        Log.i(MainActivity.TAG, child.getString("id")+":"+child.getString("comments")+":"+child.getString("reposts"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onIOException(IOException e) {
           e.printStackTrace();

        }

        @Override
        public void onError(WeiboException e) {
            e.printStackTrace();

        }

    }
	
}
