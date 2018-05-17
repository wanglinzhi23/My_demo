package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.ILoginModel;
import com.intellif.eyecloud.utils.Tools;
import com.intellif.eyecloud.view.ILoginContact;

/**
 * Created by intellif on 2017/9/8.
 */

public class ILoginPresenter implements ILoginContact.ILoginPresenter{
    private ILoginContact.ILoginView mLoginView;
    private ILoginContact.ILoginModel mLoginModel;
    public ILoginPresenter(ILoginContact.ILoginView mLoginView) {
        this.mLoginView = mLoginView;
        mLoginModel = new ILoginModel();
    }
    @Override
    public void login() {
        mLoginView.showProgress();
        if(mLoginView.getUserName().isEmpty()){
            mLoginView.showInfo("用户名不能为空");
            mLoginView.hideProgress();
            return;
        }
        if(mLoginView.getPassWord().isEmpty()){
            mLoginView.showInfo("密码不能为空");
            mLoginView.hideProgress();
            return;
        }
        mLoginModel.login(mLoginView.getUserName(), Tools.md5(mLoginView.getPassWord()), new OnHttpCallBack<UserBean>() {
            @Override
            public void onSuccessful(UserBean userBean) {
                mLoginModel.saveUserInfo(mLoginView.getContext(),userBean);
                mLoginView.intentMain();
                mLoginView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                mLoginView.showInfo(errorMsg);
                mLoginView.hideProgress();
            }
        });

    }

}
