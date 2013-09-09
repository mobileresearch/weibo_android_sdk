package com.weibo.sdk.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.sdk.api.message.InviteApi;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.AccessTokenKeeper;
import com.weibo.sdk.view.LoginButton;


public class DemoLoginButton extends Activity {
	
	private LoginButton mLoginBt ;
	private Button mInviteButton;
	private EditText mEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_login_button);
		
		mLoginBt = (LoginButton) findViewById(R.id.login_bt);
		mLoginBt.setCurrentActivity(this);
		
		mEditText = (EditText) findViewById(R.id.uid);
		
		mInviteButton = (Button) findViewById(R.id.invite);
		mInviteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString(InviteApi.KEY_TEXT, "songxiaoxue 邀请接口");
				bundle.putString(InviteApi.KEY_URL, "http://weibo.com/u/1846671692?wvr=5&");
				String uid = "";
				if(!TextUtils.isEmpty(mEditText.getText())){
					uid = mEditText.getText().toString();
					new InviteApi(getApplicationContext(), bundle, uid).sendInvite();
				}else{
					Toast.makeText(getApplicationContext(), "请输入被邀请人的Uid!", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SsoHandler mSsoHandler = mLoginBt.getSsoHandler();
		 if(mSsoHandler!=null){
         	 mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
         	TextView tv = (TextView) findViewById(R.id.result);
         	Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
         	if(token != null && !TextUtils.isEmpty(token.getToken())){
         		tv.setText(token.getToken());
         	}
		 }
	}
}

