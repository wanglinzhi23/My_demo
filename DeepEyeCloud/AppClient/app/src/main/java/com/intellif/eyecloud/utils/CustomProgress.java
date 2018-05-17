package com.intellif.eyecloud.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.intellif.eyecloud.R;

/**
 * Created by intellif on 2017/9/26.
 */

public class CustomProgress extends ProgressDialog {
    private AnimationDrawable mAnimation;
    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
//    private TextView mLoadingTv;
    private int count = 0;
    private String oldLoadingTip;

    public CustomProgress(Context context, String content) {
        super(context);
        this.mContext = context;
        this.mLoadingTip = content;
        setCanceledOnTouchOutside(true);

    }
    public CustomProgress(Context context, int theme) {
        super(context,theme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    private void initData() {
        mImageView.setBackgroundResource(R.drawable.frame);
        // 通过ImageView对象拿到背景显示的AnimationDrawable
        mAnimation = (AnimationDrawable) mImageView.getBackground();
        // 为了防止在onCreate方法中只显示第一帧的解决方案之一
        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnimation.start();

            }
        });
//        mLoadingTv.setText(mLoadingTip);
    }
    public void setContent(String str) {
//        mLoadingTv.setText(str);
    }
    private void initView() {
        setContentView(R.layout.progress_dialog);
//        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        mImageView = (ImageView) findViewById(R.id.loadingIv);
//        mLoadingTv.setVisibility(View.GONE);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        mAnimation.start();
        super.onWindowFocusChanged(hasFocus);
    }
}
