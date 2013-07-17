package com.weibo.sdk.android.demo;

import java.io.File;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.keep.AccessTokenKeeper;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;
/**
 * 
 * @author liyan (liyan9@staff.sina.com.cn)
 */
public class MainActivity extends Activity{

    private Weibo mWeibo;   
  
    private TextView mText;
    
    public static Oauth2AccessToken accessToken;
    
    public static final String TAG = "sinasdk";

    /**
     * SsoHandler 仅当sdk支持sso时有效，
     */
    private SsoHandler mSsoHandler;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_main);
        mWeibo = Weibo.getInstance(ConstantS.APP_KEY, ConstantS.REDIRECT_URL,ConstantS.SCOPE);
        mText = (TextView) findViewById(R.id.show);
        MainActivity.accessToken = AccessTokenKeeper.readAccessToken(this);
        
        // 取token测试button
        findViewById(R.id.auth).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Utility.showToast("暂时封闭此功能，不提供Token算法，请访问code算法自行取token:)",
            			MainActivity.this.getApplicationContext());           
            }
        });
      
        // 触发sso测试button
        findViewById(R.id.sso).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler = new SsoHandler(MainActivity.this, mWeibo);
                mSsoHandler.authorize(new AuthDialogListener(),null);
            }
        });
        // 取code测试button
        findViewById(R.id.code).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeibo.anthorize(MainActivity.this, new AuthDialogListener());
            }
        });
    
        
        if (MainActivity.accessToken.isSessionValid()) {
            String date = new java.text.SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
                    .format(new java.util.Date(MainActivity.accessToken
                            .getExpiresTime()));
            mText.setText("access_token 仍在有效期内,无需再次登录: \naccess_token:"
                    + MainActivity.accessToken.getToken() + "\n有效期：" + date);
        } else {
            mText.setText("使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，" +
            		"目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
        }
        
   }

    class AuthDialogListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
        	
        	String code = values.getString("code");
        	if(code != null){
	        	mText.setText("取得认证code: \r\n Code: " + code);
	        	Toast.makeText(MainActivity.this, "认证code成功", Toast.LENGTH_SHORT).show();
	        	return;
        	}
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            MainActivity.accessToken = new Oauth2AccessToken(token, expires_in);
            if (MainActivity.accessToken.isSessionValid()) {
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                        .format(new java.util.Date(MainActivity.accessToken
                                .getExpiresTime()));
                mText.setText("认证成功: \r\n access_token: " + token + "\r\n"
                        + "expires_in: " + expires_in + "\r\n有效期：" + date);
             
                AccessTokenKeeper.keepAccessToken(MainActivity.this,
                        accessToken);
                Toast.makeText(MainActivity.this, "认证成功", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        public void onError(WeiboDialogError e) {
            Toast.makeText(getApplicationContext(),
                    "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "Auth cancel",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getApplicationContext(),
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // sso 授权回调
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

}
