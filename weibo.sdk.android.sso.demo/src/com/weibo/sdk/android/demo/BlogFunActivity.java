package com.weibo.sdk.android.demo;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.FavoritesAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.RequestListener;

public class BlogFunActivity extends Activity implements OnClickListener{
    Button btnFav,btnForward,btnComment;
    JSONObject jsObject=null;
    long blogId;
    TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blogfun);
        Intent it=this.getIntent();
        String content=it.getStringExtra("content");
        try {
            jsObject= new JSONObject(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray array;
        StringBuilder sb=new StringBuilder();
        
        try {
            array = jsObject.getJSONArray("statuses");
            if(array.length()>0){
                JSONObject child=array.getJSONObject(0);
                String id=child.getString("id");
                sb.append("text:"+child.getString("text")).append("\r\n");
                sb.append("created_at:"+child.getString("created_at")).append("\r\n");
                sb.append("source:"+child.getString("source")).append("\r\n");
                sb.append("comments_count:"+child.getString("comments_count")).append("\r\n");
                sb.append("reposts_count:"+child.getString("reposts_count")).append("\r\n");
                JSONObject user=child.getJSONObject("user");
                sb.append("screen_name:"+user.getString("screen_name")).append("\r\n");
                sb.append("name:"+user.getString("name")).append("\r\n");
                sb.append("city:"+user.getString("city")).append("\r\n");
                sb.append("location:"+user.getString("location")).append("\r\n");
                sb.append("description:"+user.getString("description")).append("\r\n");
                sb.append("gender:"+user.getString("gender")).append("\r\n");
                blogId=Long.parseLong(id);
            }
           
        } catch (JSONException e) {
            e.printStackTrace();
        }
      
       
        mTv=(TextView)findViewById(R.id.textView1);
        mTv.setText(sb.toString());
        btnFav=(Button)this.findViewById(R.id.fav);
        btnFav.setOnClickListener(this);
        btnForward=(Button)this.findViewById(R.id.forward);
        btnForward.setOnClickListener(this);
        btnComment=(Button)this.findViewById(R.id.comment);
        btnComment.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v.equals(btnFav)){
            Util. showToast(BlogFunActivity.this,"api访问请求已执行，请等待结果");
            FavoritesAPI  fav=new FavoritesAPI(MainActivity.accessToken);
            fav.create(blogId,  new RequestListener() {
                @Override
                public void onIOException(IOException e) {
                    e.printStackTrace();
                }
                @Override
                public void onError(WeiboException e) {
                    Util.showToast(BlogFunActivity.this,"收藏失败:"+e.getMessage());
                }
                @Override
                public void onComplete(String response) {
//                    Util.showToast(BlogFunActivity.this,response);
                    if(mTv!=null){
                        Util.setTextViewContent(BlogFunActivity.this,mTv,"收藏结果:"+response);
                    }
                }
            });
        }
        else if(v.equals(btnForward)){
            Util.showToast(BlogFunActivity.this,"api访问请求已执行，请等待结果");
                    StatusesAPI  status=new StatusesAPI(MainActivity.accessToken);
                    status.repost(blogId, "无"+System.currentTimeMillis(), WeiboAPI.COMMENTS_TYPE.NONE, new RequestListener() {
                        
                        @Override
                        public void onIOException(IOException e) {
                            e.printStackTrace();
                        }
                        
                        @Override
                        public void onError(WeiboException e) {
                            Util.showToast(BlogFunActivity.this,"转发失败:"+e.getMessage());
                        }
                        
                        @Override
                        public void onComplete(String response) {
//                            Util.showToast(BlogFunActivity.this,response);
                            if(mTv!=null){
                                Util.setTextViewContent(BlogFunActivity.this,mTv,"转发结果:"+response);
                            }
                        }
                    });
        }
        else if(v.equals(btnComment)){
            Util.showToast(BlogFunActivity.this,"api访问请求已执行，请等待结果");
            CommentsAPI  comment=new CommentsAPI(MainActivity.accessToken);
            comment.create( "顶！"+System.currentTimeMillis(),blogId, false, new RequestListener() {
                @Override
                public void onIOException(IOException e) {
                    e.printStackTrace();
                }
                
                @Override
                public void onError(WeiboException e) {
                    Util. showToast(BlogFunActivity.this,"评论失败:"+e.getMessage());
                }
                
                @Override
                public void onComplete(String response) {
//                    Util.showToast(BlogFunActivity.this,response);
                    if(mTv!=null){
                        Util.setTextViewContent(BlogFunActivity.this,mTv,"评论结果:"+response);
                    }
                }
            });
        }
        
    }
  
}
