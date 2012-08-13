/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weibo.sdk.android.demo;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

/**
 * A dialog activity for sharing any text or image message to weibo. Three
 * parameters , accessToken, tokenSecret, consumer_key, are needed, otherwise a
 * WeiboException will be throwed.
 * 
 * ShareActivity should implement an interface, RequestListener which will
 * return the request result.
 * 
 * @author (luopeng@staff.sina.com.cn zhangjie2@staff.sina.com.cn 官方微博：WBSDK
 *         http://weibo.com/u/2791136085)
 */

public class ShareActivity extends Activity implements OnClickListener, RequestListener {
	
    private TextView mTextNum;
	private Button mSend,mPhoto;
	private EditText mEdit;
	private FrameLayout mPiclayout;
	private ImageView mImage ;
	private String mPicPath = "";
	private String mContent = "";
	private String mAccessToken = "";

	public static final String EXTRA_WEIBO_CONTENT = "com.weibo.android.content";
	public static final String EXTRA_PIC_URI = "com.weibo.android.pic.uri";
	public static final String EXTRA_ACCESS_TOKEN = "com.weibo.android.accesstoken";
	public static final String EXTRA_EXPIRES_IN = "com.weibo.android.token.expires";

	public static final int WEIBO_MAX_LENGTH = 140;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.weibosdk_share_mblog_view);
		Intent in = this.getIntent();
		mAccessToken = in.getStringExtra(EXTRA_ACCESS_TOKEN);

		Button close = (Button) this.findViewById(R.id.weibosdk_btnClose);
		close.setOnClickListener(this);
		mSend = (Button) this.findViewById(R.id.weibosdk_btnSend);
		mSend.setOnClickListener(this);
		LinearLayout total = (LinearLayout) this.findViewById(R.id.weibosdk_ll_text_limit_unit);
		total.setOnClickListener(this);
		mTextNum = (TextView) this.findViewById(R.id.weibosdk_tv_text_limit);
		ImageView picture = (ImageView) this.findViewById(R.id.weibosdk_ivDelPic);
		picture.setOnClickListener(this);

		mEdit = (EditText) this.findViewById(R.id.weibosdk_etEdit);
		mEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String mText = mEdit.getText().toString();
				int len = mText.length();
				if (len <= WEIBO_MAX_LENGTH) {
					len = WEIBO_MAX_LENGTH - len;
					//mTextNum.setTextColor(R.color.weibosdk_text_num_gray);
					if (!mSend.isEnabled())
						mSend.setEnabled(true);
				} else {
					len = len - WEIBO_MAX_LENGTH;

					mTextNum.setTextColor(Color.RED);
					if (mSend.isEnabled())
						mSend.setEnabled(false);
				}
				mTextNum.setText(String.valueOf(len));
			}
		});
		mEdit.setText(mContent);
		mPiclayout = (FrameLayout) ShareActivity.this.findViewById(R.id.weibosdk_flPic);
		mPiclayout.setVisibility(View.GONE);
		
//		if (TextUtils.isEmpty(this.mPicPath)) {
//			mPiclayout.setVisibility(View.GONE);
//		} else {
		
//			File file = new File(mPicPath);
//			if (file.exists()) {
//				Bitmap pic = BitmapFactory.decodeFile(this.mPicPath);
//				
//				mImage.setImageBitmap(pic);
//			} else {
//				mPiclayout.setVisibility(View.GONE);
//			}
//		}
		mPhoto = (Button) this.findViewById(R.id.weibosdk_btnPhoto);
		mPhoto.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();

		if (viewId == R.id.weibosdk_btnClose) {
			finish();
		}
		else if(viewId==R.id.weibosdk_btnPhoto){
		    takePicture();
		}
		else if (viewId == R.id.weibosdk_btnSend) {
			StatusesAPI api = new StatusesAPI(MainActivity.accessToken);
			if (!TextUtils.isEmpty(mAccessToken)) {
				this.mContent = mEdit.getText().toString();
				if(TextUtils.isEmpty(mContent)){
				    Toast.makeText(this, "请输入内容!",
                            Toast.LENGTH_LONG).show();
				    return;
				}
				if (!TextUtils.isEmpty(mPicPath)) {
					api.upload( this.mContent, this.mPicPath, "90.0", "90.0", this);
					Toast.makeText(this, "api访问请求已执行，请等待结果",
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					// Just update a text weibo!
					api.update( this.mContent, "90.0", "90.0", this);
				}
			} else {
				Toast.makeText(this, this.getString(R.string.weibosdk_please_login),
						Toast.LENGTH_LONG).show();
			}

		} else if (viewId == R.id.weibosdk_ll_text_limit_unit) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.weibosdk_attention)
					.setMessage(R.string.weibosdk_delete_all)
					.setPositiveButton(R.string.weibosdk_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mEdit.setText("");
						}
					}).setNegativeButton(R.string.weibosdk_cancel, null).create();
			dialog.show();
		} else if (viewId == R.id.weibosdk_ivDelPic) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(R.string.weibosdk_attention)
					.setMessage(R.string.weibosdk_del_pic)
					.setPositiveButton(R.string.weibosdk_ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mPiclayout.setVisibility(View.GONE);
							mPicPath=null;
						}
					}).setNegativeButton(R.string.weibosdk_cancel, null).create();
			dialog.show();
		}
	}

	@Override
	public void onComplete(String response) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ShareActivity.this, R.string.weibosdk_send_sucess, Toast.LENGTH_LONG)
						.show();
			}
		});

		this.finish();
	}

	@Override
	public void onIOException(IOException e) {

	}

	@Override
	public void onError(final WeiboException e) {
		System.out.println(e);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(
						ShareActivity.this,
						String.format(ShareActivity.this.getString(R.string.weibosdk_send_failed)
								+ ":%s", e.getMessage()), Toast.LENGTH_LONG).show();
			}
		});

	}
	static Uri imageUri;
	 public  void takePicture()
	    {

	      Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	      
	      File photo = new File(Environment.getExternalStorageDirectory(),  "pic.jpg");
	      intent.putExtra(MediaStore.EXTRA_OUTPUT,
	      Uri.fromFile(photo));
	      imageUri = Uri.fromFile(photo);

	      this.startActivityForResult(intent, 32 +0 + 1);
	    }
	    
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        Bitmap photo=null;
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                photo = BitmapFactory.decodeFile(uri.getPath());
                System.out.println(mPicPath);
            }
            if (photo == null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    photo = (Bitmap) bundle.get("data");
                    mImage.setImageBitmap(photo);
                } else {
                    return;
                }
            }
        }
	        else{
	            Uri selectedImage = imageUri;
	            mPicPath=selectedImage.getPath();
	            BitmapFactory.Options options=new BitmapFactory.Options() ;
                options.inSampleSize=4;
                options.inDither=true;
	            photo= BitmapFactory.decodeFile(this.mPicPath,options);
	            mPiclayout.setVisibility(View.VISIBLE);
	            mImage = (ImageView) this.findViewById(R.id.weibosdk_ivImage);
	            mImage.setImageBitmap(photo);
	            Util.showToast(this, mPicPath);
	        }
	       
	    }

}
