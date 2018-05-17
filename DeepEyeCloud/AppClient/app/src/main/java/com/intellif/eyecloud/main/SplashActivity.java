package com.intellif.eyecloud.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGHT = 2000;
    private Handler handler;
    @Override
    public int getContentViewId() {
        return R.layout.activity_splash;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        handler = new Handler();
        // 延迟SPLASH_DISPLAY_LENGHT时间然后跳转到MainActivity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}