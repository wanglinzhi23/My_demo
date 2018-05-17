package com.intellif.eyecloud.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellif.eyecloud.api.Constant;
import com.intellif.eyecloud.api.UrlConfig;
import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IRecordContact;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
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
public class RecordModel implements IRecordContact.IRecordModel {
    @Override
    public void getRecord(int page, int pageSize, String areaId,int similar,final OnHttpCallBack<List<RecordBean>> callBack) {
        JSONObject object = new JSONObject();
        try {
           // object.put("threshold","0."+similar);
            object.put("ids",areaId);//门店ID
            object.put("page",page);
           // object.put("status","1,2");
            object.put("pageSize",pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("RecordModel==========",object.toString());
        RequestParams params = new RequestParams(UrlConfig.QueryRecord);
        params.setHeader("Authorization","Bearer "+ Constant.token);
        params.setAsJsonContent(true);
        params.setBodyContent(object.toString());
        params.setConnectTimeout(5000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("result==========",result);
                try {
                    JSONObject jsonobject = new JSONObject(result);
                    int errCode = jsonobject.getInt("errCode");
                    String data = jsonobject.getString("data");
                    Log.e("aaaaa",data);
                    if(data==null||data.isEmpty()||data.equals("[]")||data.equals("null")){
                        Log.e("ssss","nullnullnullnullnull");
                        List<RecordBean> mlist = new ArrayList<RecordBean>();
                        callBack.onSuccessful(mlist);
                    }else {
                        if (errCode == 0) {
                            Log.e("ssss","nullnullnullaaaaaaaaaaaaanullnull");
                            Gson gson = new Gson();
                            List<RecordBean> mlist = gson.fromJson(data, new TypeToken<List<RecordBean>>() {
                            }.getType());
                            callBack.onSuccessful(mlist);
                        } else {
                            callBack.onFaild(data);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable e, boolean isOnCallback) {
                Log.e("qqqqqqqqqqqqqqqqqqq",e.getMessage());
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
