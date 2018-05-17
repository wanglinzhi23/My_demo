package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.DeleteBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

/**
 * Created by intellif on 2017/9/12.
 */

public interface IRecordDetailContact {
    interface  IRecordDetailView{
        void showProgress();
        void hideProgress();
        int getId();
        void onActivityFinish();
        void showInfo(String info);
    }
    interface  IRecordDetailPresenter{
        void sendToServer();
    }
    interface  IRecordDetailModel{
        void sendToServer(int id, OnHttpCallBack<DeleteBean> callBack);
    }
}
