package com.intellif.common.http;

import android.os.Handler;
import android.os.Looper;

import com.intellif.common.http.impl.XCallBack;
import com.intellif.common.http.impl.XDownCallBack;
import com.intellif.common.net.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Map;


/**
 * Created by Administrator on 2017-04-25.
 * 封装xutils的get，post，put，delete方法,基于restful模式的网络请求
 */

public class HttpTool {
    private static String TOKEN_KEY="Authorization";

    /**
     * 单例模式创建该类对象，避免反复调用，
     */
    private static HttpTool ourInstance;
        private Handler handler;
        public static HttpTool getInstance() {
            if (ourInstance == null){
                synchronized (HttpTool.class){
                    if (ourInstance == null){
                        ourInstance = new HttpTool();
                    }
                }
            }
            return ourInstance;
        }

        private HttpTool() {
            handler = new Handler(Looper.getMainLooper());
        }
    /**
     * xutils get方法获取网络数据，
     * @param url 请求地址
     * @param maps key value 键值对
     * @param callback 回调
     */
    public void get(String url, Map<String,String> maps, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     *
     * @param url 请求地址
     * @param maps 数据
     * @param token token数据请求认证
     * @param callback 接口回调
     */
    public void get(String url, Map<String,String> maps, String token, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        params.addHeader(TOKEN_KEY,token);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * POST方法向服务器提交数据
     * @param url
     * @param maps
     * @param callback
     */
    public void post(String url, Map<String,String> maps, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {

                    if (result != null) {
                        this.result = result;
                    }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {

                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * POST向服务器提交数据，带header
     * @param url
     * @param maps
     * @param token
     * @param callback
     */
    public void post(String url, Map<String,String> maps, String token, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        params.addHeader(TOKEN_KEY,token);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * PUT方法向服务器提交数据，
     * @param url
     * @param maps
     * @param callback
     */
    public void put(String url, Map<String,String> maps, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * PUT方式向服务器提交数据，带header
     * @param url
     * @param maps
     * @param token
     * @param callback
     */
    public void put(String url, Map<String,String> maps, String token, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        params.addHeader(TOKEN_KEY,token);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * delete向服务器提交数据
     * @param url
     * @param maps
     * @param callback
     */
    public void delete(String url, Map<String,String> maps, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
                //httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }

    /**
     * DELETE方法向服务器提交数据。带header
     * @param url
     * @param maps
     * @param token
     * @param callback
     */
    public void delete(String url, Map<String,String> maps, String token, final XCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        params.addHeader(TOKEN_KEY,token);
        if (null != maps && !maps.isEmpty()){
            for (Map.Entry<String,String> entry : maps.entrySet()){
                params.addParameter(entry.getKey(),entry.getValue());
            }
        }
        params.setConnectTimeout(5*1000);
        x.http().request(HttpMethod.PUT,params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hasError = true;
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                if (!hasError && result != null) {
                    callback.onResponse(result);
                }
            }
        });
    }
    /**
     * 下载文件
     * @param url
     * @param filePath
     * @param callback
     */
    public void downFile(String url, String filePath, final XDownCallBack callback){
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(filePath);
        params.setAutoRename(true);
        params.setConnectTimeout(5*1000);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                //下载完成会走该方法
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){
                            callback.onSuccess(result);
                        }
                    }
                });
            }
            @Override
            public void onError(final Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != callback){
                            callback.onFail(ex.getMessage());

                        }
                    }
                });
            }
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){
                            callback.onFinished();
                        }
                    }
                });
            }
            //网络请求之前回调
            @Override
            public void onWaiting() {
            }
            //网络请求开始的时候回调
            @Override
            public void onStarted() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != callback){
                            callback.onstart();
                        }
                    }
                });
            }
            //下载的时候不断回调的方法
            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                //当前进度和文件总大小
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback!=null){
                            callback.onLoading(total,current,isDownloading);
                        }
                    }
                });
            }
        });
    }
    /**
     * 文件上传
     *
     * @param url
     * @param maps
     * @param file
     * @param callback
     */
    public void upLoadFile(String url, Map<String, String> maps, Map<String, File> file, final XCallBack callback) {
        if(NetworkUtils.isConnected()==false){
            callback.onFail("网络连接失败!请检查网络");
            return;
        }
        RequestParams params = new RequestParams(url);
//        params.addHeader(TOKEN_KEY,token);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        if (file != null) {
            for (Map.Entry<String, File> entry : file.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue().getAbsoluteFile());
            }
        }
        // 有上传文件时使用multipart表单, 否则上传原始文件流.
        params.setMultipart(true);
        params.setConnectTimeout(5*1000);
        x.http().post(params, new Callback.CommonCallback<String>() {
            private boolean hasError = false;
            private String result = null;
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!hasError && result != null && callback!=null) {
                    onSuccess(result);
                }
            }
        });

    }
    /**
     * 上传Json串到服务器
     * @param url
     * @param maps 将需要传的各个参数放在Map集合里面
     */
    public void upLoadJson(String url, Map<String, String> maps, final XCallBack callback){
//        if(NetworkUtils.isConnected()==false){
//            callback.onFail("网络连接失败!请检查网络");
//            return;
//        }
        JSONObject js_request = new JSONObject();//服务器需要传参的json对象
        try {
            for (Map.Entry<String,String> entry : maps.entrySet()){
                js_request.put(entry.getKey(),entry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestParams params = new RequestParams(url);
        params.setAsJsonContent(true);
        params.setBodyContent(js_request.toString());
        params.setConnectTimeout(5*1000);
        x.http().post(params, new Callback.CommonCallback<String>() {//发起传参为json的post请求，
            // Callback.CacheCallback<String>的泛型为后台返回数据的类型，
            // 根据实际需求更改
            private boolean hasError = false;
            private String result = null;

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!hasError && result != null && callback!=null) {
                    onSuccess(result);
                }
            }
        });

    } /**
     * 上传Json串到服务器
     * @param url
     *
     */
    public void upLoadJson(String url,String jsonString,String token, final XCallBack callback){
//        if(NetworkUtils.isConnected()==false){
//            callback.onFail("网络连接失败!请检查网络");
//            return;
//        }

        RequestParams params = new RequestParams(url);
        params.addHeader(TOKEN_KEY,token);
        params.setAsJsonContent(true);
        params.setBodyContent(jsonString);
        params.setConnectTimeout(5*1000);
        x.http().post(params, new Callback.CommonCallback<String>() {//发起传参为json的post请求，
            // Callback.CacheCallback<String>的泛型为后台返回数据的类型，
            // 根据实际需求更改
            private boolean hasError = false;
            private String result = null;

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    this.result = result;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (ex instanceof HttpException) {
                    HttpException httpException = (HttpException) ex;
//httpException.response().errorBody().string()
                    int code = httpException.getCode();
                    if (code == 500 || code == 404) {
                        callback.onFail("服务器出错");
                    }
                } else if (ex instanceof ConnectException) {
                    callback.onFail("网络断开,请打开网络!");
                } else if (ex instanceof SocketTimeoutException) {
                    callback.onFail("网络连接超时!!");
                } else {
                    callback.onFail("发生未知错误" + ex.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                if (!hasError && result != null && callback!=null) {
                    onSuccess(result);
                }
            }
        });

    }
}
