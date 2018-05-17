package com.intellif.eyecloud.presenter;

import android.content.Context;

import com.intellif.eyecloud.model.SettingModel;
import com.intellif.eyecloud.view.ISettingContact;

/**
 * Created by intellif on 2017/9/19.
 */

public class SettingPresenter implements ISettingContact.ISettingPresenter {
    private ISettingContact.ISettingView settingView;
    private  ISettingContact.ISettingModel settingModel;
    public SettingPresenter(ISettingContact.ISettingView settingView){
        this.settingView = settingView;
        settingModel = new SettingModel();
    }

    @Override
    public void getCacheSize(Context context) {
        String cache="";
        try {
         cache =  settingModel.getCacheSize(context);
            settingView.showDialog(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCache(Context context) {
        settingModel.clearCache(context);
        settingView.showToast();
    }
}
