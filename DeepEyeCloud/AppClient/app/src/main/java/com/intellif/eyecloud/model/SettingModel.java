package com.intellif.eyecloud.model;

import android.content.Context;

import com.intellif.eyecloud.utils.MyDataCleanManager;
import com.intellif.eyecloud.view.ISettingContact;

/**
 * Created by intellif on 2017/9/19.
 */

public class SettingModel implements ISettingContact.ISettingModel {
    @Override
    public String getCacheSize(Context context) {
        String cache = "0";
        try {
          cache =  MyDataCleanManager.getTotalCacheSize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    @Override
    public void clearCache(Context context) {
        MyDataCleanManager.clearAllCache(context);
    }
}
