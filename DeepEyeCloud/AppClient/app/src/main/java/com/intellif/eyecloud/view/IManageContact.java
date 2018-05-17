package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/12.
 */

public interface IManageContact {
    interface IManageView{
        void showProgress();
        void hideProgress();
        String getAreaId();
        int getPage();
        int getPageSize();
        void showInfo(String msg);
        void setListReferch(List<ManageBean> mlist);//设置设备列表
        void setList(List<ManageBean> mlist);//设置设备列表

    }
    interface IManagePresenter{
        void getSheBei();
        void getSheBeiReferch();
    }
    interface  IManageModel{
        void getSheBei(int page,int pageSize,String areaId, OnHttpCallBack<List<ManageBean>> callBack);
    }
}
