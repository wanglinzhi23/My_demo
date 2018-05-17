package com.intellif.eyecloud.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.MessageBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IMessageContact;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit.HttpException;

/**
 * Created by intellif on 2017/9/8.
 */

public class MessageModel implements IMessageContact.IMessageModel {
    @Override
    public void getMessage(int page, int pageSize,int similar, String areaId, final OnHttpCallBack<List<MessageBean>> callBack) {
        JSONObject object = new JSONObject();
        try {

            object.put("threshold","0."+similar);
            object.put("ids",areaId);//门店ID
            object.put("page",page);
            object.put("onlyFirst",1);
            object.put("status","0");
            object.put("pageSize",pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("messageModel",object.toString());
        RequestParams params = new RequestParams(UrlConfig.QueryPeopleDetail);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.setAsJsonContent(true);
        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result==========",result);
                Logger.e(result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(data==null||data.isEmpty()||data.equals("[]")||data.equals("null")){
                        List<MessageBean> mlist = new ArrayList<MessageBean>();
                        callBack.onSuccessful(mlist);
                    }else {
                        if(errCode==0){
                            Gson gson = new Gson();
                            List<MessageBean> mlist =gson.fromJson(data,new TypeToken<List<MessageBean>>(){}.getType());
                            callBack.onSuccessful(mlist);
                        }else{
                            callBack.onFaild(data);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
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
    public void messageDone(String personId, int status, final OnHttpCallBack<MessageBean.EventsBean> callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("alarmId",personId);//personId
            object.put("type",status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("result==========",object.toString());
        RequestParams params = new RequestParams(UrlConfig.PersonStatus);
        Log.e("url",UrlConfig.PersonStatus+"/"+personId);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.setAsJsonContent(true);
        params.setBodyContent(object.toString());
        x.http().request(HttpMethod.POST,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result==========",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        MessageBean.EventsBean mlist =gson.fromJson(data,MessageBean.EventsBean.class);
                        callBack.onSuccessful(mlist);
                    }else{
                        callBack.onFaild(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
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


}
