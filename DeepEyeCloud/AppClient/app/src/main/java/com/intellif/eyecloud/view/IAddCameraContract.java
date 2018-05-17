package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

/**
 * Created by intellif on 2017/9/12.
 */

public interface IAddCameraContract {
    interface  IAddCameraView{
        void showProgress();
        void hideProgress();
        void showInfo(String msg);
        String getCameraId();
        String getCamDes();
        String getAreaId();
        void onFinish(ManageBean manageBean);
    }
    interface  IAddCameraPresenter{
            void sendToServer();
    }
    interface  IAddCameraModel{
        void addCameraToServer(String CameraId, String camDes,String areaId, OnHttpCallBack<ManageBean> callBack);
    }
}
