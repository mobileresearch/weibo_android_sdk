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
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.utils.Util;

/**
 * 执行流程： 1. 从本应用->微博->本应用
 * 
 * @author taibin@staff.sina.com.cn
 * 
 */
public class RequestMessageActivity extends Activity implements OnClickListener, IWeiboHandler.Response {
    IWeiboAPI weiboAPI = null;
    TextView  title;
    
    ImageView image;
    
    TextView  musicTitle;
    ImageView musicImage;
    TextView  musicContent;
    TextView  musicUrl;
    
    TextView  videoTitle;
    ImageView videoImage;
    TextView  videoContent;
    TextView  videoUrl;
    
    TextView  webpageTitle;
    ImageView webpageImage;
    TextView  webpageContent;
    TextView  webpageUrl;
    
    
//    Bundle    mBundle;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reqmessage);
        weiboAPI = WeiboSDK.createWeiboAPI(this, ConstantS.APP_KEY);

        initViews();

//        mBundle = getIntent().getExtras();
        weiboAPI.responseListener(getIntent(), this);
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
		switch (v.getId()) {
		case R.id.share_text:
			// 三方到微博
			reqTextMsg();
			break;
		case R.id.share_image:
			// 三方到微博
			reqImageMsg();
			break;
		case R.id.share_music:
			// 三方到微博
			reqMusicMsg();
			break;
			
		case R.id.share_video:
			// 三方到微博
			reqVideoMsg();
			break;
		case R.id.share_webpage:
			reqWebpageMsg();
			break;
		}
	}

    private void reqVideoMsg() {
    	// 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        // 媒体消息
        weiboMessage.mediaObject = getVideoObj();
        // 初始化从三方到微博的消息请求
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        req.message = weiboMessage;
        // 发送请求消息到微博
        weiboAPI.sendRequest(this, req);
		
	}

	private void reqMusicMsg() {
    	// 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        // 媒体消息
        weiboMessage.mediaObject = getMusicObj();
        // 初始化从三方到微博的消息请求
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        req.message = weiboMessage;
        // 发送请求消息到微博
        weiboAPI.sendRequest(this, req);
		
	}

	/**
     * 三方到微博文本消息
     */
    private void reqTextMsg() {
        // 三方到微博
        // 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        // 放文本消息
        weiboMessage.mediaObject = getTextObj();
        // 初始化从三方到微博的消息请求
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        req.message = weiboMessage;
        // 发送请求消息到微博
        weiboAPI.sendRequest(this, req);
    }

	/**
	 * 分享图片和文字到微博
	 */
	private void reqImageMsg() {
		// 初始化微博的分享消息
		WeiboMessage weiboMessage = new WeiboMessage();
		// 图片消息
		weiboMessage.mediaObject = getImageObj();
		
		// 初始化从三方到微博的消息请求
		SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
		req.message = weiboMessage;
		// 发送请求消息到微博
		weiboAPI.sendRequest(this, req);
	}

    /**
     * 应用到微博网页消息（与音乐消息、视频消息类似）
     */
    private void reqWebpageMsg() {
        // 初始化微博的分享消息
        WeiboMessage weiboMessage = new WeiboMessage();
        // 媒体消息
        weiboMessage.mediaObject = getWebpageObj();
        // 初始化从三方到微博的消息请求
        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());// 用transaction唯一标识一个请求
        req.message = weiboMessage;
        // 发送请求消息到微博
        weiboAPI.sendRequest(this, req);
    }

    private String getActionUrl() {
        return "http://sina.com?eet"+System.currentTimeMillis();
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
//        Drawable draw = getResources().getDrawable(R.drawable.testimg);
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) draw;
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) webpageImage.getDrawable();
        mediaObject.setThumbImage(bitmapDrawable.getBitmap());
        mediaObject.actionUrl = getActionUrl();
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) videoImage.getDrawable();
        videoObject.setThumbImage(bitmapDrawable.getBitmap());
        videoObject.actionUrl = getActionUrl();
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) musicImage.getDrawable();
        musicObject.setThumbImage(bitmapDrawable.getBitmap());
        musicObject.actionUrl = getActionUrl();
        musicObject.dataUrl = "www.weibo.com";
        musicObject.dataHdUrl = "www.weibo.com";
        musicObject.duration = 10;
        return musicObject;
    }

    @Override
    protected void onNewIntent( Intent intent ) {
        super.onNewIntent(intent);
        setIntent(intent);
//        mBundle = getIntent().getExtras();
        weiboAPI.responseListener(getIntent(), this);
    }

    /**
     * 从本应用->微博->本应用
     */
    @Override
    public void onResponse( BaseResponse baseResp ) {
        switch (baseResp.errCode) {
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
            Toast.makeText(this, "成功！！", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, "用户取消！！", Toast.LENGTH_LONG).show();
            break;
        case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, baseResp.errMsg + ":失败！！", Toast.LENGTH_LONG).show();
            break;
        }

    }

}
