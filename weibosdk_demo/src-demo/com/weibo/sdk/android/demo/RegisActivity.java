package com.weibo.sdk.android.demo;

import com.sina.weibo.sdk.log.Log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class RegisActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ly_more);
		this.findViewById(R.id.oauth).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RegisActivity.this,MainActivity.class));
			}
		});
		
		this.findViewById(R.id.shareTo).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(RegisActivity.this,MainShareActivity.class));
			}
		});
	}

}
