package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */
public interface IRecordContact {
    interface  IRecordView{
        void setData(List<RecordBean> list);

        void showProgress();

        void hideProgress();

        int getPage();

        int getPageSize();

        String getAreaId();

        void showInfo(String message);

        void setList(List<RecordBean> list);

        int getSimilar();

    }

    interface  IRecordPresenter{
        void getRecordList();
        void referchData();

    }

    interface IRecordModel{
        void getRecord(int page, int pageSize, String areaId,int similar, OnHttpCallBack<List<RecordBean>> callBack);
    }

}
