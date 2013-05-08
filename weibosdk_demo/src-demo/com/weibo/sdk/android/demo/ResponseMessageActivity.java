package com.weibo.sdk.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseRequest;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.ProvideMessageForWeiboRequest;
import com.sina.weibo.sdk.api.ProvideMessageForWeiboResponse;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.log.Log;
import com.sina.weibo.sdk.utils.Util;

/**
 * 执行流程： 2. 从微博->本应用->微博
 * 
 * @author taibin@staff.sina.com.cn
 * 
 */
public class ResponseMessageActivity extends Activity implements
		OnClickListener, IWeiboHandler.Request {
	IWeiboAPI weiboAPI = null;
	TextView title;

	ImageView image;

	TextView musicTitle;
	ImageView musicImage;
	TextView musicContent;
	TextView musicUrl;

	TextView videoTitle;
	ImageView videoImage;
	TextView videoContent;
	TextView videoUrl;

	TextView webpageTitle;
	ImageView webpageImage;
	TextView webpageContent;
	TextView webpageUrl;

	// Bundle mBundle;
	Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_responsemsg);
		weiboAPI = WeiboSDK.createWeiboAPI(this, ConstantS.APP_KEY);	
		mBundle = getIntent().getExtras();
		initViews();
		weiboAPI.requestListener(getIntent(), this);
	}

	private void initViews() {
		((Button) findViewById(R.id.share_text)).setOnClickListener(this);
		((Button) findViewById(R.id.share_image)).setOnClickListener(this);
		((Button) findViewById(R.id.share_webpage)).setOnClickListener(this);
		((Button) findViewById(R.id.share_music)).setOnClickListener(this);
		((Button) findViewById(R.id.share_video)).setOnClickListener(this);

		title = (TextView) findViewById(R.id.titleText);

		image = (ImageView) findViewById(R.id.image);

		musicTitle = (TextView) findViewById(R.id.music_title);
		musicImage = (ImageView) findViewById(R.id.music_image);
		musicContent = (TextView) findViewById(R.id.music_desc);
		musicUrl = (TextView) findViewById(R.id.music_url);

		videoTitle = (TextView) findViewById(R.id.video_title);
		videoImage = (ImageView) findViewById(R.id.video_image);
		videoContent = (TextView) findViewById(R.id.video_desc);
		videoUrl = (TextView) findViewById(R.id.video_url);

		webpageTitle = (TextView) findViewById(R.id.webpage_title);
		webpageImage = (ImageView) findViewById(R.id.webpage_image);
		webpageContent = (TextView) findViewById(R.id.webpage_desc);
		webpageUrl = (TextView) findViewById(R.id.webpage_url);
	}

	@Override
	public void onClick(View v) {
		if (mBundle == null) {
			Toast.makeText(this, "未收到微博发起的请求！", Toast.LENGTH_LONG).show();
		}
		switch (v.getId()) {
		case R.id.share_text:
			respTextMsg();
			break;
		case R.id.share_image:
			respImageMsg();
			break;
		case R.id.share_music:
			respMusicMsg();
			break;
		case R.id.share_video:
			respVideoMsg();
			break;
		case R.id.share_webpage:
			respWebpageMsg();
			break;
		}
		// 防止创建多个
		finish();
	}
	
	
	
	private void respWebpageMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 多媒体（网页）消息
		weiboMessage.mediaObject = getWebpageObj();

		// 初始化从三方到微博的消息请求
		ProvideMessageForWeiboResponse resp = new ProvideMessageForWeiboResponse();
		resp.transaction = new ProvideMessageForWeiboRequest(mBundle).transaction;
		Log.i("msg", resp.transaction);
		resp.message = weiboMessage;
		// 发送请求消息到微博
		weiboAPI.sendResponse(resp);
	}

	private void respMusicMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 多媒体（网页）消息
		weiboMessage.mediaObject = getMusicObj();

		// 初始化从三方到微博的消息请求
		ProvideMessageForWeiboResponse resp = new ProvideMessageForWeiboResponse();
		resp.transaction = new ProvideMessageForWeiboRequest(mBundle).transaction;
		Log.i("msg", resp.transaction);
		resp.message = weiboMessage;
		// 发送请求消息到微博
		weiboAPI.sendResponse(resp);

	}

	private void respImageMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 图片消息
		weiboMessage.mediaObject = getImageObj();

		// 初始化从三方到微博的消息请求
		ProvideMessageForWeiboResponse resp = new ProvideMessageForWeiboResponse();
		resp.transaction = new ProvideMessageForWeiboRequest(mBundle).transaction;
		Log.i("msg", resp.transaction);
		resp.message = weiboMessage;
		// 发送请求消息到微博
		weiboAPI.sendResponse(resp);

	}

	private void respTextMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 放文本消息
		weiboMessage.mediaObject = getTextObj();

		// 初始化从三方到微博的消息请求
		ProvideMessageForWeiboResponse resp = new ProvideMessageForWeiboResponse();
		resp.transaction = new ProvideMessageForWeiboRequest(mBundle).transaction;
		Log.i("msg", resp.transaction);
		resp.message = weiboMessage;
		// 发送请求消息到微博
		weiboAPI.sendResponse(resp);

	}


	private void respVideoMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		weiboMessage.mediaObject = getVideoObj();
		// 三方应用响应微博的消息
		ProvideMessageForWeiboResponse resp = new ProvideMessageForWeiboResponse();
		resp.transaction = new ProvideMessageForWeiboRequest(mBundle).transaction;
		resp.message = weiboMessage;
		// 发送响应消息到微博
		weiboAPI.sendResponse(resp);
	}


	/**
	 * 文本消息构造方法
	 * 
	 * @return
	 */
	private TextObject getTextObj() {
		TextObject textObject = new TextObject();
		textObject.text = title.getText().toString();
		return textObject;
	}

	/**
	 * 图片消息构造方法
	 * 
	 * @return 图片消息对象
	 */
	private ImageObject getImageObj() {
		ImageObject imageObject = new ImageObject();
		// imageObject.imagePath="/sdcard/com.sina.weibo.sdk.demo/test.jpg";
		// Drawable draw = getResources().getDrawable(R.drawable.testimg);
		// BitmapDrawable bitmapDrawable = (BitmapDrawable) draw;
		BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
		imageObject.setImageObject(bitmapDrawable.getBitmap());

		return imageObject;
	}

	/**
	 * 多媒体（网页）消息构造方法
	 * 
	 * @return 多媒体（网页）消息对象
	 */
	private WebpageObject getWebpageObj() {
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Util.generateId();// 创建一个唯一的ID
		mediaObject.title = webpageTitle.getText().toString();
		mediaObject.description = webpageContent.getText().toString();
		// 设置bitmap类型的图片到视频对象里
		BitmapDrawable bitmapDrawable = (BitmapDrawable) webpageImage
				.getDrawable();
		mediaObject.setThumbImage(bitmapDrawable.getBitmap());
		mediaObject.actionUrl = webpageUrl.getText().toString();
		return mediaObject;
	}

	/**
	 * 多媒体（视频）消息构造方法
	 * 
	 * @return 多媒体（视频）消息对象
	 */
	private VideoObject getVideoObj() {
		// 创建媒体消息
		VideoObject videoObject = new VideoObject();
		videoObject.identify = Util.generateId();// 创建一个唯一的ID
		videoObject.title = videoTitle.getText().toString();
		videoObject.description = videoContent.getText().toString();
		// 设置bitmap类型的图片到视频对象里
		BitmapDrawable bitmapDrawable = (BitmapDrawable) videoImage
				.getDrawable();
		videoObject.setThumbImage(bitmapDrawable.getBitmap());
		videoObject.actionUrl = videoUrl.getText().toString();
		videoObject.dataUrl = "www.weibo.com";
		videoObject.dataHdUrl = "www.weibo.com";
		videoObject.duration = 10;
		return videoObject;
	}

	/**
	 * 多媒体（音乐）消息构造方法
	 * 
	 * @return 多媒体（音乐）消息对象
	 */
	private MusicObject getMusicObj() {
		// 创建媒体消息
		MusicObject musicObject = new MusicObject();
		musicObject.identify = Util.generateId();// 创建一个唯一的ID
		musicObject.title = musicTitle.getText().toString();
		musicObject.description = musicContent.getText().toString();
		// 设置bitmap类型的图片到视频对象里
		BitmapDrawable bitmapDrawable = (BitmapDrawable) musicImage
				.getDrawable();
		musicObject.setThumbImage(bitmapDrawable.getBitmap());
		musicObject.actionUrl = musicUrl.getText().toString();
		musicObject.dataUrl = "www.weibo.com";
		musicObject.dataHdUrl = "www.weibo.com";
		musicObject.duration = 10;
		return musicObject;
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		weiboAPI.requestListener(getIntent(), this);
	}
	@Override
	public void onRequest(BaseRequest baseReq) {
		// TODO
		Log.i("msg", "收到请求消息");
	}

}
