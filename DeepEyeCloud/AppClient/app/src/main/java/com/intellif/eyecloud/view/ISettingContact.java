package com.intellif.eyecloud.view;

import android.content.Context;

/**
 * Created by intellif on 2017/9/19.
 */

public interface ISettingContact  {
    interface ISettingView{
        void showToast();
        void showDialog(String cache);

    }

    interface ISettingPresenter{
        void getCacheSize(Context context);
        void clearCache(Context context);
    }

    interface  ISettingModel{
        String getCacheSize(Context context);
        void clearCache(Context context);
    }
}
