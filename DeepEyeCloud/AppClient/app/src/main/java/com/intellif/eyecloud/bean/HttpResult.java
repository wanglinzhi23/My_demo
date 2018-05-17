package com.intellif.eyecloud.bean;

/**
 * Created by intellif on 2017/9/20.
 */

public class HttpResult<T> {
    public T data;
    public int errCode;
    public int maxPage;
    public int total;
}
