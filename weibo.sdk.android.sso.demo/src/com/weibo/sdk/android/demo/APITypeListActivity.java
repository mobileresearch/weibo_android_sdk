package com.weibo.sdk.android.demo;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.FEATURE;
import com.weibo.sdk.android.demo.adapter.APITpyeListAdapter;
import com.weibo.sdk.android.net.RequestListener;

public class APITypeListActivity extends ListActivity implements RequestListener{
	private String[] apiArr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_typelist);
//		apiArr = getResources().getStringArray(R.array.api_types);
		apiArr=getResources().getStringArray(R.array.examples);
		APITpyeListAdapter adapter = new APITpyeListAdapter(getApplicationContext(), apiArr);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
//		Intent it = new Intent(this, APIMethodsListActivity.class);
//		it.putExtra("api", apiArr[position].split(":")[1]);
//		startActivity(it);
		String title=apiArr[position];
		if("发送微博".equals(title)){
		    Intent it = new Intent(APITypeListActivity.this, ShareActivity.class);
            it.putExtra(ShareActivity.EXTRA_ACCESS_TOKEN, MainActivity.accessToken.getToken());
            it.putExtra(ShareActivity.EXTRA_EXPIRES_IN, MainActivity.accessToken.getExpiresTime());
            startActivity(it);
        }
        else if("获取关注好友微博".equals(title)){
            StatusesAPI statusApi=new StatusesAPI(MainActivity.accessToken);
            statusApi.friendsTimeline(0, 0, 50 , 1, false,  FEATURE.ALL,false,this);
            Toast.makeText(this, "api访问请求已执行，请等待结果",
                    Toast.LENGTH_LONG).show();
        }
	}

    @Override
    public void onComplete(String response) {
        Intent intent=new Intent();
        intent.setClass(this, BlogFunActivity.class);
        intent.putExtra("content", response);
        startActivity(intent);
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
