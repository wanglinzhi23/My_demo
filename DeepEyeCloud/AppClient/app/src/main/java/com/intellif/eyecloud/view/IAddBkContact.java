package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.ImageBean;
import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.io.File;
import java.util.List;

/**
 * Created by intellif on 2017/9/14.
 */

public interface IAddBkContact  {
    interface IAddBKView{
        void showProgress();
        void hideProgress();
        void showDialog(String message);
        String getArea();
        List<String> getImageId();
        String getPersonName();
        String getPersonDes();
        void setImage(ImageBean imageBean);
        void showInfo(String message);
        void activityFinish();
    }
    interface  IAddBKPresenter{
        void uploadImage(File file);
        void addInfo();
    }
    interface IAddBKModel{
        void uploadImage(File file, OnHttpCallBack<ImageBean> callBack);
        void upload (String images, String areaId, String personName, String personDes, OnHttpCallBack<PeopleBean> callBack);
    }
}
