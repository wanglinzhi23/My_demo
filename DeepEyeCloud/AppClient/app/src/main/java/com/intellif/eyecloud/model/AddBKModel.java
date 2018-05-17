package com.intellif.eyecloud.model;

import android.util.Log;

import com.google.gson.Gson;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.ImageBean;
import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IAddBkContact;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.HttpException;

/**
 * Created by intellif on 2017/9/14.
 */

public class AddBKModel implements IAddBkContact.IAddBKModel {
    @Override
    public void uploadImage(File file, final OnHttpCallBack<ImageBean> callBack) {
        Log.e("file",file+"");
        RequestParams params = new RequestParams(UrlConfig.ImageUP);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.addBodyParameter("file",file);
        params.setMultipart(true);
        params.setConnectTimeout(5000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result)
            {
                Log.e("result",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        ImageBean imageBean =gson.fromJson(data,ImageBean.class);
                        callBack.onSuccessful(imageBean);
                    }else{
                        callBack.onFaild(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                Log.e("sss", e.getMessage());
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    int code = httpException.code();
                    if (code == 500 || code == 404) {
                        callBack.onFaild("服务器出错");
                    }else if(code==401){
                        callBack.onFaild("登录认证已过期，清重新登录");
                        EventBusBean bean = new EventBusBean();
                        bean.EventId=1;
                        bean.msg="";
                        EventBus.getDefault().post(bean);
                    }
                } else if (e instanceof ConnectException) {
                    callBack.onFaild("网络断开,请打开网络!");
                } else if (e instanceof SocketTimeoutException) {
                    callBack.onFaild("网络连接超时!!");
                } else {
                    callBack.onFaild("登录过期，请重新登录");
                    EventBusBean bean = new EventBusBean();
                    bean.EventId=1;
                    bean.msg="";
                    EventBus.getDefault().post(bean);
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
    public void upload(String imageId, String areaId, String personName, String personDes,  final OnHttpCallBack<PeopleBean> callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("realName",personName);
            object.put("starttime","2017-09-30T04:35:21.477Z");
            object.put("endtime","2100-09-30T04:35:21.477Z");
            object.put("imageIds",imageId);
            object.put("stationIds",areaId);
            object.put("description",personDes);

            //差一个描述的字段
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("json",object.toString());
        RequestParams params = new RequestParams(UrlConfig.AddBk);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.setAsJsonContent(true);
        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        PeopleBean bean =gson.fromJson(data,PeopleBean.class);
                        callBack.onSuccessful(bean);
                    }else{
                        callBack.onFaild(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                Log.e("result",e.getMessage());
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    //httpException.response().errorBody().string()
                    int code = httpException.code();
                    if (code == 500 || code == 404) {
                        callBack.onFaild("服务器出错");
                    }
                    if(code==400||code==401){
                        callBack.onFaild("登录过期，请重新登录");
                        EventBusBean bean = new EventBusBean();
                        bean.EventId=1;
                        bean.msg="";
                        EventBus.getDefault().post(bean);
                    }
                } else if (e instanceof ConnectException) {
                    callBack.onFaild("网络断开,请打开网络!");
                } else if (e instanceof SocketTimeoutException) {
                    callBack.onFaild("网络连接超时!!");
                } else {
                    callBack.onFaild("登录过期，请重新登录");
                    EventBusBean bean = new EventBusBean();
                    bean.EventId=1;
                    bean.msg="";
                    EventBus.getDefault().post(bean);
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
}
