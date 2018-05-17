package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.presenter.ILoginPresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.ILoginContact;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * 用户登录界面
 */
public class LoginActivity extends BaseActivity implements ILoginContact.ILoginView {
    @BindView(R.id.et_login_username)
    EditText login_username;
    @BindView(R.id.et_login_password)
    EditText login_password;
    @BindView(R.id.bt_login)
    Button bt_login;
    @BindView(R.id.tv_login_error)
    TextView tv_login_error;
    @BindView(R.id.login_check)
    CheckBox login_check;
    private ILoginPresenter mLoginPresenter;
    private CustomProgress progress;
    @Override
    public int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        mLoginPresenter = new ILoginPresenter(this);
        progress = new CustomProgress(mActivity,"");
        boolean check = SPContent.getCheck(mActivity);
        login_username.setText(SPContent.getLoginName(mActivity));
        if(check){
            String password = SPContent.getLoginPass(mActivity);
            login_password.setText(SPContent.getLoginPass(mActivity));
            login_check.setChecked(true);
        }

    }
    //用户点击效果
    @OnClick(R.id.bt_login)
    void login(){
        mLoginPresenter.login();
    }
    @Override
    public void showInfo(String info) {

        tv_login_error.setText(info);
    }

    @Override
    public void intentMain() {
        SPContent.saveLoginName(LoginActivity.this,getUserName());
        if(login_check.isChecked()){
            SPContent.saveCheck(mActivity,true);
            SPContent.saveLoginPass(mActivity,getPassWord());

        }else{
            SPContent.saveCheck(mActivity,false);
            SPContent.deleteLoginPass(mActivity);
        }
        Intent intent = new Intent(LoginActivity.this,ToggleMainActivity.class);
        startActivity(intent);
        this.finish();
    }
    @Override
    public String getUserName() {
        return login_username.getText().toString().trim();
    }
    @Override
    public String getPassWord() {
        return login_password.getText().toString().trim();
    }
    @Override
    public void showProgress() {
        progress.show();
    }
    @Override
    public void hideProgress() {
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
    @Override
    public Context getContext() {
        Context context1 = this;
        return context1;
    }
}
