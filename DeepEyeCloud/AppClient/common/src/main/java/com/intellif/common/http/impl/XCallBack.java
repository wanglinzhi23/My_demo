package com.intellif.common.http.impl;

/**
 * Created by Administrator on 2017-04-25.
 * 普通的get以及post请求的回调接口
 */

public interface XCallBack {
    void onResponse(String result);
    void onFail(String result);
}
