package com.intellif.eyecloud.utils;

import android.content.Context;

import com.intellif.common.sp.SPUtils;

/**
 * Created by intellif on 2017/9/20.
 */

public class SPContent {
    //保存账号密码
    public static void saveLoginName(Context context,String loginName){
        SPUtils.putString(context,"loginName",loginName);
    }
    public static String getLoginName(Context context){
        String loginName = SPUtils.getString(context,"loginName","");
        return loginName;
    }
    public  static void  deleteLoginName(Context context){
        SPUtils.deleShare(context,"loginName");
    }


    public static void saveLoginPass(Context context,String loginPass){
        SPUtils.putString(context,"loginPass",loginPass);
    }
    public static String getLoginPass(Context context){
        String loginPass = SPUtils.getString(context,"loginPass","");
        return loginPass;
    }
    public  static void  deleteLoginPass(Context context){
        SPUtils.deleShare(context,"loginPass");
    }


    //保存用户登录信息
    public static void saveUser(Context context,String userString){
        SPUtils.putString(context,"user",userString);
    }
    public static String getUser(Context context){
        String user = SPUtils.getString(context,"user","");
        return user;
    }
    public  static void  deleteUser(Context context){
        SPUtils.deleShare(context,"user");
    }
    //保存用户登录token
    public static void saveToken(Context context,String token){
        SPUtils.putString(context,"token",token);
    }
    public static String getToken(Context context){
        String token = SPUtils.getString(context,"token","");
        return token;
    }
    public  static void  deleteToken(Context context){
        SPUtils.deleShare(context,"token");
    }

    //保存门店信息
    public static void saveArea(Context context,String areaId){
        SPUtils.putString(context,"area",areaId);
    }
    public static String getArea(Context context){
        String areaId = SPUtils.getString(context,"area","");
        return areaId;
    }
    public  static void  deleteArea(Context context){
        SPUtils.deleShare(context,"area");
    }
    public static void saveAreaName(Context context,String areaName){
        SPUtils.putString(context,"areaName",areaName);
    }
    public static String getAreaName(Context context){
        String areaName = SPUtils.getString(context,"areaName","");
        return areaName;
    }
    public  static void  deleteAreaName(Context context){
        SPUtils.deleShare(context,"areaName");
    }


    /**
     * 相似度
     * @param context
     * @param similar
     */
    public static void saveSimilar(Context context,int similar){
        SPUtils.putInt(context,"similar",similar);
    }
    public static int getSimilar(Context context){
        int similar = SPUtils.getInt(context,"similar",92);
        return similar;
    }
    public  static void  deleteSimilar(Context context){
        SPUtils.deleShare(context,"similar");
    }


    /**记住密码
     *
     * @param context
     * @param check
     */
    public static void saveCheck(Context context,boolean check){
        SPUtils.putBoolean(context,"check",check);
    }
    public static boolean getCheck(Context context){
        boolean check = SPUtils.getBoolean(context,"check",false);
        return check;
    }
    public  static void  deleteCheck(Context context){
        SPUtils.deleShare(context,"check");
    }

    /**
     * 判断是否接收消息
     * @param context
     * @param ring
     */
    public static void saveRing(Context context,boolean ring){
        SPUtils.putBoolean(context,"ring",ring);
    }
    public static boolean getRing(Context context){
        boolean ring = SPUtils.getBoolean(context,"ring",true);
        return ring;
    }
    public  static void  deleteRing(Context context){
        SPUtils.deleShare(context,"ring");
    }

    /**
     * 保存所有的topic
     * @param context
     * @param topic
     */
    public static void saveTopic(Context context,String topic){
        SPUtils.putString(context,"topic",topic);
    }
    public static String getTopic(Context context){
        String topic = SPUtils.getString(context,"topic","");
        return topic;
    }
    public  static void  deleteTopic(Context context){
        SPUtils.deleShare(context,"topic");
    }
}
