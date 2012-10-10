package com.weibo.sdk.android.demo;

import java.io.IOException;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.ActivityInvokeAPI;
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
        else if("打开启动微博客户端".equals(title)){
            ActivityInvokeAPI.openWeibo(this);
        }
        else if("打开用户信息页面 uid".equals(title)){
            ActivityInvokeAPI.openUserInfoByUid(this, "2890441681");
        }
        else if("打开用户信息页面 nickname".equals(title)){
            ActivityInvokeAPI.openUserInfoByNickName(this, "佛爷zhang");
        }
        else if("打开微博发送页面 content".equals(title)){
            ActivityInvokeAPI.openSendWeibo(this, "===&啦啦");
        }
        else if("打开微博发送页面 多参数".equals(title)){
            ActivityInvokeAPI.openSendWeibo(this,  "测试打开微博发送页面", null, "", "理想国际大厦", "116.39794", "39.90817");
        }
        else if("打开查看附近人页面".equals(title)){
            ActivityInvokeAPI.openNearbyPeople(this);
        }
        else if("打开查看附近微博界面".equals(title)){
            ActivityInvokeAPI.openNearbyWeibo(this);
        }
        else if("打开打开摇一摇界面".equals(title)){
            ActivityInvokeAPI.openShake(this);
        }
        else if("打开通讯录界面".equals(title)){
            ActivityInvokeAPI.openContact(this);
        }
        else if("打开内置浏览器界面".equals(title)){
            ActivityInvokeAPI.openWeiboBrowser(this, "http://www.sina.com.cn");
        }
        else if("打开私信对话界面".equals(title)){
            ActivityInvokeAPI.openMessageListByUid(this, "2890441681");
        }
        else if("打开私信对话界面 nick".equals(title)){
            ActivityInvokeAPI.openMessageListByNickName(this, "testblog78");
        }
        else if("打开用户话题列表页面".equals(title)){
            ActivityInvokeAPI.openUserTrends(this, "2890441681");
        }
        else if("打开某条微博正文".equals(title)){
            ActivityInvokeAPI.openDetail(this, "3483953283884795");
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
