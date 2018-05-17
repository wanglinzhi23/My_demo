package com.intellif.common.http.impl;

import java.io.File;

/**
 * Created by Administrator on 2017-04-25.
 * 文件下载的接口回调
 */

public interface XDownCallBack {
    void onstart();
    void onLoading(long total, long current, boolean isDownloading);
    void onSuccess(File result);
    void onFail(String result);
    void onFinished();
}
