package com.intellif.eyecloud.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.DeleteBean;
import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.bean.post.FaceBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IPeopleDetailContact;
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
import java.util.List;

import retrofit.HttpException;

/**
 * Created by intellif on 2017/9/12.
 */

public class PeopleDetailModel implements IPeopleDetailContact.IPeopleDetailModel {
    @Override
    public void getEvent(int PersonId, int page, int pageSize, int similar,final OnHttpCallBack<EventBean> callBack) {

    JSONObject object = new JSONObject();
        try {
        object.put("personId",PersonId);
            object.put("threshold","0."+similar);
            object.put("page",page);
            object.put("pageSize",pageSize);
    } catch (JSONException e) {
        e.printStackTrace();
    }
        Logger.e(object.toString());
        RequestParams params = new RequestParams(UrlConfig.QueryEvent);
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
                JSONObject jsonobject1 = new JSONObject(data);
                String events = jsonobject1.getString("events");
                Log.e("ppppppppppppppppp",events);
                    if(errCode==0){
                        if(events.isEmpty()||events.equals("null")||events.equals("[]")){
                            EventBean mlist = new EventBean();
                            callBack.onFaild("");
                        }else {
                            Log.e("ppppppppppppppppp","2222222222222222222222");
                            Gson gson = new Gson();
                            EventBean mlist = gson.fromJson(data, EventBean.class);
                            callBack.onSuccessful(mlist);
                        }
                    }else{
                        callBack.onFaild(data);
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(Throwable e, boolean isOnCallback) {
            Log.e("ssss3",e.getMessage());
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
    public void DeletePeson(int personId, final OnHttpCallBack<DeleteBean> callBack) {

        RequestParams params = new RequestParams(UrlConfig.PersonDelete+personId);
        params.setHeader("Authorization","Bearer "+ Constant.token);
//        params.setAsJsonContent(true);
//        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().request(HttpMethod.DELETE,params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        DeleteBean mlist =gson.fromJson(data,DeleteBean.class);
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
                Log.e("ssss1",e.getMessage());
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    //httpException.response().errorBody().string()
                    int code = httpException.code();
                    if (code == 500 || code == 404) {
                        callBack.onFaild("服务器出错");
                    }
                    if(code==400){
                        callBack.onFaild("登录失效");
                    }
                } else if (e instanceof ConnectException) {
                    callBack.onFaild("网络断开,请打开网络!");
                } else if (e instanceof SocketTimeoutException) {
                    callBack.onFaild("网络连接超时!!");
                } else {
                    callBack.onFaild("登录过期，请重新登录");
//                    EventBusBean bean = new EventBusBean();
//                    bean.EventId=1;
//                    bean.msg="";
//                    EventBus.getDefault().post(bean);
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
    public void getImages(int personId, final OnHttpCallBack<List<FaceBean>> callBack) {
        RequestParams params = new RequestParams(UrlConfig.PeopleImages+personId);
        Log.e("url",UrlConfig.PeopleImages+personId);
        params.setHeader("Authorization","Bearer "+ Constant.token);
//        params.setAsJsonContent(true);
//        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        List<FaceBean> mlist =gson.fromJson(data,new TypeToken<List<FaceBean>>(){}.getType());
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
                Log.e("ssss2",e.getMessage());
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    //httpException.response().errorBody().string()
                    int code = httpException.code();
                    if (code == 500 || code == 404) {
                        callBack.onFaild("服务器出错");
                    }
                    if(code==400){
                        callBack.onFaild("登录失效");
                    }
                } else if (e instanceof ConnectException) {
                    callBack.onFaild("网络断开,请打开网络!");
                } else if (e instanceof SocketTimeoutException) {
                    callBack.onFaild("网络连接超时!!");
                } else {
                    callBack.onFaild("登录过期，请重新登录");
//                    EventBusBean bean = new EventBusBean();
//                    bean.EventId=1;
//                    bean.msg="";
//                    EventBus.getDefault().post(bean);
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
