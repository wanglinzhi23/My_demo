package com.intellif.eyecloud.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.main.LoginActivity;
import com.intellif.eyecloud.service.MQTTService;
import com.intellif.eyecloud.utils.ActivityControl;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 * Created by intellif on 2017/9/7.
 */
public abstract class BaseActivity extends FragmentActivity {
    public abstract int getContentViewId();
    public Activity mActivity;
    SystemBarTintManager tintManager;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        setContentView(getContentViewId());
        mActivity = this;
        ButterKnife.bind(mActivity);
        ActivityControl.addActivity(this);
        initAllMembersView(savedInstanceState);
    }
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.titleColor);//通知栏所需颜色
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    protected abstract void initAllMembersView(Bundle savedInstanceState);
    @Subscribe
    public void onEventMainThread(EventBusBean bean) {
        Log.e("test","jeishoujieshou jiesous oashdoasdoasdasdas========================================");
        if(bean.EventId==1){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            getApplicationContext().startActivity(intent);
            ActivityControl.finishAll();
            System.exit(0
            );
        }
        if(bean.EventId==2){
            Log.e("main","开启全局service监听");
            Intent intent = new Intent(getApplicationContext(), MQTTService.class);
            startService(intent);
        }

    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }
}