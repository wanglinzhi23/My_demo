package com.intellif.eyecloud.api;

/**
 * Created by intellif on 2017/9/20.
 */

public class UrlConfig {
//    public static final String HOST="http://192.168.2.15:8082/api/";//科学馆开发环境
    //api接口地址
    public static final String HOST="http://39.108.169.236:8083/api/";//阿里云环境
//    public static final String HOST="http://192.168.11.241/api/";//阿里云环境
    //消息接收mqtt地址
    public static final String MQTT_HOST = "tcp://39.108.169.236:8083";
//    public static final String MQTT_HOST = "tcp://192.168.11.241";
    //mqtt消息接口地址
    public static final String USERLOGIN=HOST+"oauth/token";
    public static final String addCamera=HOST+"intellif/camera";
    public static final String QueryArea=HOST+"intellif/area/query";
    public static final String QueryCamera=HOST+"intellif/camera/query";
    public static final String QueryPeople=HOST+"intellif/person/detail/query";
    public static final String QueryPeopleDetail=HOST+"intellif/alarm/station/query";
    public static final String QueryRecord=HOST+"intellif/alarm/process/query";
    public static final String QueryEvent=HOST+"intellif/alarm/person/query";
    public static final String ImageUP=HOST+"intellif/image/upload/true?type=1";
    public static final String PersonStatus=HOST+"intellif/alarm/process";
    public static final String AddBk=HOST+"intellif/person/detail";
    public static final String DeleteAlarm=HOST+"intellif/alarm/process/";
    public static final String PersonDelete=HOST+"intellif/person/detail/";
    public static final String PeopleImages=HOST+"intellif/black/detail/person/";
}
