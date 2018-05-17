package com.intellif.eyecloud.base;

import android.app.Application;
import android.content.Intent;

import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.main.LoginActivity;
import com.intellif.eyecloud.utils.ActivityControl;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.x;

/**
 * Created by intellif on 2017/9/8.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }


}
