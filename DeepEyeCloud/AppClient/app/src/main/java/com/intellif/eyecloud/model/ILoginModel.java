package com.intellif.eyecloud.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.intellif.common.gson.GsonUtils;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.ILoginContact;
import com.orhanobut.logger.Logger;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.HttpException;

/**
 * Created by intellif on 2017/9/8.
 */

public class ILoginModel implements ILoginContact.ILoginModel {

    @Override
    public void login(String username, String password, final OnHttpCallBack<UserBean> callBack) {
        //登录的网络请求
//        RetrofitUtils.newInstence(UrlConfig.HOST)//实例化Retrofit对象
//                .create(ApiService.class)//创建Rxjava---->LoginService对象
//                .userLogin(username, password,"password","read+write","123456","clientapp")//调用登录的接口
//                .subscribeOn(Schedulers.newThread())//在新线程中执行登录请求
//                .observeOn(AndroidSchedulers.mainThread())//在主线程中执行
//                .subscribe(new Subscriber<UserBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        Logger.e(e.getMessage());
//                        if (e instanceof HttpException) {
//                            HttpException httpException = (HttpException) e;
//                            //httpException.response().errorBody().string()
//                            int code = httpException.code();
//                            if (code == 500 || code == 404) {
//                                callBack.onFaild("服务器出错");
//                            }
//                            if(code==400){
//                                callBack.onFaild("账号或者密码错误");
//                            }
//                        } else if (e instanceof ConnectException) {
//                            callBack.onFaild("网络断开,请打开网络!");
//                        } else if (e instanceof SocketTimeoutException) {
//                            callBack.onFaild("网络连接超时!!");
//                        } else {
//                            callBack.onFaild("发生未知错误");
//                        }
//                    }
//                    @Override
//                    public void onNext(UserBean userBean) {
////                        Logger.e("ssss");
////                        if(!userBean.error.isEmpty()){
////                            callBack.onFaild("发生未知错误www");
////                        }else {
//                            Logger.e(userBean.access_token);
//                            Logger.e(userBean.error);
//                            callBack.onSuccessful(userBean);
//                    }
//                });

        RequestParams params = new RequestParams(UrlConfig.USERLOGIN);
        params.addHeader("Authorization", "Basic Y2xpZW50YXBwOjEyMzQ1Ng==");
        params.addHeader("content-type", "application/x-www-form-urlencoded");
        params.addBodyParameter("password", password);
        params.addBodyParameter("username",username);
        params.addBodyParameter("grant_type","password");
        params.addBodyParameter("scope", "read+write");
        params.addBodyParameter("client_secret", "123456");
        params.addBodyParameter("client_id", "clientapp");
        Log.e("sss",params.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result==========",result);
//                try {
//                    JSONObject jsonobject = new JSONObject(result);
//                    int errCode = jsonobject.getInt("errCode");
//                    String data = jsonobject.getString("data");
//                    if(errCode==0){
                        Gson gson = new Gson();
                      UserBean userbean =gson.fromJson(result,UserBean.class);
                        callBack.onSuccessful(userbean);
//                    }else{
//                        callBack.onFaild(data);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                Logger.e(e.getMessage());
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            int code = httpException.code();
                            if (code == 500 || code == 404) {
                                callBack.onFaild("服务器出错");
                            } if(code==400){
                                callBack.onFaild("发生未知错误");
                            }
                        } else if (e instanceof ConnectException) {
                            callBack.onFaild("网络断开,请打开网络!");
                        } else if (e instanceof SocketTimeoutException) {
                            callBack.onFaild("网络连接超时!!");
                        } else {
                            callBack.onFaild("用户名或密码错误");
                        }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }
    @Override
    public void saveUserInfo(Context context, UserBean userBean) {
        SPContent.saveToken(context,userBean.access_token);//保存了用户登录的token
        Constant.token = SPContent.getToken(context);
        SPContent.saveUser(context, GsonUtils.GsonString(userBean.oauth_AIK_user_info));
    }
}
