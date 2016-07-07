package com.yztc.myumengshare;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.Iterator;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
  private boolean flage=false;

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.DOUBAN
            };
    UMShareAPI mShareAPI;
    @InjectView(R.id.share)
    Button share;
    @InjectView(R.id.authorization)
    Button authorization;
    @InjectView(R.id.getData)
    Button getData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        String[] mPermissionList = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP, Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS};
        ActivityCompat.requestPermissions(MainActivity.this, mPermissionList, 100);
        mShareAPI = UMShareAPI.get(this);
        pushInit();

    }
    /**
     *初始化推送
     */

    private void pushInit() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        PushAgent.getInstance(this).onAppStart();
        //推送的点击监听
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom+"dianji", Toast.LENGTH_LONG).show();
            }
        };
        //设置推送的监听
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        String device_token = UmengRegistrar.getRegistrationId(this);
        Log.e("tag", "================" + device_token + "============");
    }

   //分享到qq
    private void share() {
        new ShareAction(this).setDisplayList(displaylist)
                .withText("呵呵")
                .withTitle("title")
                .withTargetUrl("http://www.baidu.com")
                .withMedia(new UMImage(this, "https://ss1.baidu.com/9vo3dSag_xI4khGko9WTAnF6hhy/image/h%3D360/sign=a813da3172094b36c4921deb93ce7c00/810a19d8bc3eb135aa449355a21ea8d3fc1f4458.jpg"))
                .setListenerList(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        Log.e("tag", "成功");
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Log.e("tag", "失败");
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        Log.e("tag", "退出");
                    }
                })
                .open();
    }

    /**
     * qq授权
     */
    public void authorization() {
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Iterator<String> keys = data.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Log.d("------", "key==" + key + "    value==" + data.get(key));
                }
                Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
               flage=true;

            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        };
        mShareAPI.doOauthVerify(this, platform, umAuthListener);

    }
    //必须回调的方法 由于给mShareAPI回调结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
    //获取授权后qq的用户数据
    public void getData(){
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Iterator<String> keys = data.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Log.d("------", "key==" + key + "    value==" + data.get(key));
                }
                Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        };
        mShareAPI.getPlatformInfo(this, platform, umAuthListener);
    }

    @OnClick({R.id.share, R.id.authorization, R.id.getData})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
                share();
                break;
            case R.id.authorization:
                authorization();
                break;
            case R.id.getData:
               if(flage){
                   getData();
               }else {
                   Toast.makeText(this,"还未授权请授权",Toast.LENGTH_SHORT).show();
               }
                break;
        }
    }
}
