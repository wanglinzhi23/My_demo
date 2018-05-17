package com.intellif.eyecloud.model;

import com.google.gson.Gson;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IAddCameraContract;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.HttpException;

/**
 * Created by intellif on 2017/9/12.
 */

public class AddCameraModel  implements IAddCameraContract.IAddCameraModel{

    @Override
    public void addCameraToServer(String CameraId, String camDes,String areaId, final OnHttpCallBack<ManageBean> callBack) {
        JSONObject object = new JSONObject();
        try {
            object.put("name",CameraId);
            object.put("addr",camDes);
            object.put("stationId",areaId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestParams params = new RequestParams(UrlConfig.addCamera);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.setAsJsonContent(true);
        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Logger.e(result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    if(errCode==0){
                        Gson gson = new Gson();
                        ManageBean bean =gson.fromJson(data,ManageBean.class);
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
