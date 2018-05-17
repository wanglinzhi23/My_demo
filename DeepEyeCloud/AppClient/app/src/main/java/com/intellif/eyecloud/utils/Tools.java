package com.intellif.eyecloud.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by intellif on 2017/9/20.
 */

public class Tools {
    /**
     * MD5转换
     *
     * @param plainText
     * @return
     */
    public static String md5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }

        return md5code;
    }

    public static String getLoginToken(Context context){
        String token = SPContent.getToken(context);
        return token;
    }


    /*
   * 将时间转换为时间戳
   */
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    public static String dateToStampTime(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    public static String dateToStampTime4(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    public static String timeTranslate(long time){
        Long now = System.currentTimeMillis();
        long timeLeaveNow = now - time;
        String returnText = "";
        timeLeaveNow = timeLeaveNow/1000;
        Log.e("ssssss",timeLeaveNow+"");
        if(timeLeaveNow > 60*60*24*31){
            returnText = Math.floor(timeLeaveNow/(60*60*24*31))+"个月";
        }else if(timeLeaveNow > 1000*60*60*24*7){
            returnText = Math.floor(timeLeaveNow/(60*60*24*7))+"周";
        }else if(timeLeaveNow > 60*60*24){
            returnText = Math.floor(timeLeaveNow/(60*60*24))+"天";
        }else if(timeLeaveNow > 60*60){
            returnText = Math.floor(timeLeaveNow/(60*60))+"小时";
        }else if(timeLeaveNow > 60){
            returnText = Math.floor(timeLeaveNow/60)+"分钟";
        }else if(timeLeaveNow > 5){
            returnText = "刚刚";
        }else{
            returnText = "0分钟";
        }
        return  returnText.replace(".0","");
    }

    /**
     * 获取版本名称
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context){
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(),0);
        //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
