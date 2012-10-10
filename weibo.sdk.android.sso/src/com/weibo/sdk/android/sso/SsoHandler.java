package com.weibo.sdk.android.sso;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.sina.sso.RemoteSSO;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.util.Utility;
/**
 * 该类用于处理sso 认证功能，通过sso，无需输入用户名、密码即可以通过微博账号访问经过授权的第三方应用，\r\n
 * 使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，目前仅3.0.0及以上微博客户端版本支持SSO；
 * 如果未安装，将自动转为Oauth2.0进行认证
 * 
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class SsoHandler {
    private ServiceConnection conn = null;
    private static final int DEFAULT_AUTH_ACTIVITY_CODE = 32973;
    private static final String WEIBO_SIGNATURE = "30820295308201fea00302010202044b4ef1bf300d"
            + "06092a864886f70d010105050030818d310b300906035504061302434e3110300e0603550408130"
            + "74265694a696e673110300e060355040713074265694a696e67312c302a060355040a132353696e"
            + "612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c7464312c302a0603550"
            + "40b132353696e612e436f6d20546563686e6f6c6f677920284368696e612920436f2e204c746430"
            + "20170d3130303131343130323831355a180f32303630303130323130323831355a30818d310b300"
            + "906035504061302434e3110300e060355040813074265694a696e673110300e0603550407130742"
            + "65694a696e67312c302a060355040a132353696e612e436f6d20546563686e6f6c6f67792028436"
            + "8696e612920436f2e204c7464312c302a060355040b132353696e612e436f6d20546563686e6f6c"
            + "6f677920284368696e612920436f2e204c746430819f300d06092a864886f70d010101050003818"
            + "d00308189028181009d367115bc206c86c237bb56c8e9033111889b5691f051b28d1aa8e42b66b7"
            + "413657635b44786ea7e85d451a12a82a331fced99c48717922170b7fc9bc1040753c0d38b4cf2b2"
            + "2094b1df7c55705b0989441e75913a1a8bd2bc591aa729a1013c277c01c98cbec7da5ad7778b2fa"
            + "d62b85ac29ca28ced588638c98d6b7df5a130203010001300d06092a864886f70d0101050500038"
            + "181000ad4b4c4dec800bd8fd2991adfd70676fce8ba9692ae50475f60ec468d1b758a665e961a3a"
            + "edbece9fd4d7ce9295cd83f5f19dc441a065689d9820faedbb7c4a4c4635f5ba1293f6da4b72ed3"
            + "2fb8795f736a20c95cda776402099054fccefb4a1a558664ab8d637288feceba9508aa907fc1fe2"
            + "b1ae5a0dec954ed831c0bea4";

//    private String[] mAuthPermissions;
    private int mAuthActivityCode;
    private static String ssoPackageName = "";// "com.sina.weibo";
    private static String ssoActivityName = "";// "com.sina.weibo.MainTabActivity";
    private WeiboAuthListener mAuthDialogListener;
    private Oauth2AccessToken mAccessToken = null;
    private Activity mAuthActivity;
    private Weibo mWeibo;
    public SsoHandler(Activity activity,Weibo weibo) {
        mAuthActivity = activity;
        mWeibo=weibo;
        Weibo.isWifi=Utility.isWifi(activity);
        conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                RemoteSSO remoteSSOservice = RemoteSSO.Stub
                        .asInterface(service);
                try {
                    ssoPackageName = remoteSSOservice.getPackageName();
                    ssoActivityName = remoteSSOservice.getActivityName();
                    boolean singleSignOnStarted = startSingleSignOn(
                            mAuthActivity, Weibo.app_key, new String[]{},
                            mAuthActivityCode);
                    if (!singleSignOnStarted) {
//                        startDialogAuth(mAuthActivity, new String[]{});
                        mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    /**
     * 进行sso认证
     * 
     * @param activity 发起认证的Activity
     * 
     * @param listener 用于接收认证信息的监听者
     */
    public void authorize( final WeiboAuthListener listener) {
        authorize( DEFAULT_AUTH_ACTIVITY_CODE,
                listener);
    }

    private void authorize(
            int activityCode, final WeiboAuthListener listener) {
        mAuthActivityCode = activityCode;

        boolean bindSucced = false;
        mAuthDialogListener = listener;

        // Prefer single sign-on, where available.
        bindSucced = bindRemoteSSOService(mAuthActivity);
        // Otherwise fall back to traditional dialog.
        if (!bindSucced) {
            if(mWeibo!=null){
                mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
            }
            
        }

    }

    private boolean bindRemoteSSOService(Activity activity) {
        Context context = activity.getApplicationContext();
        Intent intent = new Intent("com.sina.weibo.remotessoservice");
        return context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private boolean startSingleSignOn(Activity activity, String applicationId,
            String[] permissions, int activityCode) {
        boolean didSucceed = true;
        Intent intent = new Intent();
        intent.setClassName(ssoPackageName, ssoActivityName);
        intent.putExtra("appKey", applicationId);// applicationId //"2745207810"
        intent.putExtra("redirectUri", Weibo.redirecturl);

        if (permissions.length > 0) {
            intent.putExtra("scope", TextUtils.join(",", permissions));
        }

        // validate Signature
        if (!validateAppSignatureForIntent(activity, intent)) {
            return false;
        }

        try {
            activity.startActivityForResult(intent, activityCode);
        } catch (ActivityNotFoundException e) {
            didSucceed = false;
        }

        activity.getApplication().unbindService(conn);
        return didSucceed;
    }

    private boolean validateAppSignatureForIntent(Activity activity,
            Intent intent) {
        ResolveInfo resolveInfo = activity.getPackageManager().resolveActivity(
                intent, 0);
        if (resolveInfo == null) {
            return false;
        }

        String packageName = resolveInfo.activityInfo.packageName;
        try {
            PackageInfo packageInfo = activity.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                if (WEIBO_SIGNATURE.equals(signature.toCharsString())) {
                    return true;
                }
            }
        } catch (NameNotFoundException e) {
            return false;
        }

        return false;
    }

    /**
     * 重要:发起认证的Activity必须重写onActivityResult， 这个方法必须在onActivityResult 方法内调用，
     * 例如：<br/>
     * 
     * @Override
     * protected void onActivityResult(int requestCode, int resultCode, Intent data) {<br/>
     *    super.onActivityResult(requestCode, resultCode, data);<br/>
     *    if(mSsoHandler!=null){<br/>
     *       mSsoHandler.authorizeCallBack(requestCode, resultCode, data);<br/>
     *   }<br/>
     * }
     */
    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (requestCode == mAuthActivityCode) {

            // Successfully redirected.
            if (resultCode == Activity.RESULT_OK) {

                // Check OAuth 2.0/2.10 error code.
                String error = data.getStringExtra("error");
                if (error == null) {
                    error = data.getStringExtra("error_type");
                }

                // error occurred.
                if (error != null) {
                    if (error.equals("access_denied")
                            || error.equals("OAuthAccessDeniedException")) {
                        Log.d("Weibo-authorize", "Login canceled by user.");
                        mAuthDialogListener.onCancel();
                    } else {
                        String description = data
                                .getStringExtra("error_description");
                        if (description != null) {
                            error = error + ":" + description;
                        }
                        Log.d("Weibo-authorize", "Login failed: " + error);
                        mAuthDialogListener.onError(new WeiboDialogError(error,
                                resultCode, description));
                    }

                    // No errors.
                } else {
                    if (null == mAccessToken) {
                        mAccessToken = new Oauth2AccessToken();
                    }
                    mAccessToken.setToken(data.getStringExtra(Weibo.KEY_TOKEN));
                    mAccessToken.setExpiresIn(data
                            .getStringExtra(Weibo.KEY_EXPIRES));
                    mAccessToken.setRefreshToken(data
                            .getStringExtra(Weibo.KEY_REFRESHTOKEN));
                    if (mAccessToken.isSessionValid()) {
                        Log.d("Weibo-authorize",
                                "Login Success! access_token="
                                        + mAccessToken.getToken() + " expires="
                                        + mAccessToken.getExpiresTime()
                                        + "refresh_token="
                                        + mAccessToken.getRefreshToken());
                        mAuthDialogListener.onComplete(data.getExtras());
                    } else {
                        Log.d("Weibo-authorize",
                                "Failed to receive access token by SSO");
//                        startDialogAuth(mAuthActivity, mAuthPermissions);
                        mWeibo.startAuthDialog(mAuthActivity, mAuthDialogListener);
                    }
                }

                // An error occurred before we could be redirected.
            } else if (resultCode == Activity.RESULT_CANCELED) {

                // An Android error occured.
                if (data != null) {
                    Log.d("Weibo-authorize",
                            "Login failed: " + data.getStringExtra("error"));
                    mAuthDialogListener.onError(new WeiboDialogError(data
                            .getStringExtra("error"), data.getIntExtra(
                            "error_code", -1), data
                            .getStringExtra("failing_url")));

                    // User pressed the 'back' button.
                } else {
                    Log.d("Weibo-authorize", "Login canceled by user.");
                    mAuthDialogListener.onCancel();
                }
            }
        }
    }

}
