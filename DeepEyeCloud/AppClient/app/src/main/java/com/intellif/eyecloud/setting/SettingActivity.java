package com.intellif.eyecloud.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.main.LoginActivity;
import com.intellif.eyecloud.presenter.SettingPresenter;
import com.intellif.eyecloud.utils.ActivityControl;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.view.ISettingContact;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements ISettingContact.ISettingView {
    @BindView(R.id.rl_setting_alerts)
    RelativeLayout rl_setting_alerts;
    @BindView(R.id.rl_setting_question)
    RelativeLayout rl_setting_question;
    @BindView(R.id.rl_setting_about)
    RelativeLayout rl_setting_about;
    @BindView(R.id.rl_setting_clear)
    RelativeLayout rl_setting_clear;
    @BindView(R.id.rl_setting_exit)
    RelativeLayout rl_setting_exit;
    SettingPresenter presenter;
    @Override
    public int getContentViewId() {
        return R.layout.activity_setting;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
    initTitle();
        presenter = new SettingPresenter(this);
    }


    @OnClick(R.id.rl_setting_question)
    void intentQusetion(){
        Intent intent = new Intent(SettingActivity.this,QuestionActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.rl_setting_alerts)
    void intentAlerts(){
        Intent intent = new Intent(SettingActivity.this,AlertsActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.rl_setting_about)
    void intentAbout(){
        Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
        startActivity(intent);
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.setting));
    }
    @OnClick(R.id.rl_setting_clear)
    void clearCache(){
    presenter.getCacheSize(SettingActivity.this);
    }

    @Override
    public void showToast() {
        Toast.makeText(mActivity, "清除缓存成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(String cache) {
        showDialog(SettingActivity.this,"清除缓存","当前缓存大小"+cache);
        Logger.e("aaaaaaaaaaaaaaaaaaaaa");
    }

    @OnClick(R.id.rl_setting_exit)
    void onExit(){
        showDialogExit(this,"提示","确定退出当前登录？");
    }
    public void showDialog(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                presenter.clearCache(SettingActivity.this);
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
    public void showDialogExit(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                ActivityControl.finishAll();

            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
}
