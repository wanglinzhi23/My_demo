package com.intellif.eyecloud.view;

import android.content.Context;

import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

/**
 * Created by intellif on 2017/9/8.
 */

public interface ILoginContact {
    interface ILoginView {

        void showInfo(String info);//展示错误信息

        void intentMain();//跳转到主界面

        String getUserName();//获得用户名

        String getPassWord();//获得输入框密码

        void showProgress();//展示登录进度条

        void hideProgress();//隐藏登录进度条

        Context getContext();

    }

    /**
     * P视图与逻辑处理的连接层
     */
    interface ILoginPresenter {
        void login();//唯一的桥梁就是登录了
    }
    /**
     * 逻辑处理层
     */
    interface ILoginModel {
        void login(String username, String password, OnHttpCallBack<UserBean> callBack);//登录

        void saveUserInfo(Context context,UserBean userBean);//登录成功就保存用户信息

    }
}
