package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.ToggleBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/20.
 */

public interface IToogleContact {
    interface  IToolgleView{
        void showProgress();
        void hideProgress();
        void showInfo(String msg);//展示错误信息
        int  getUserId();
        void setList(List<ToggleBean> mlist);
        void setTopicName(List<ManageBean> mlist);
        String getArea();
    }
    interface ITooglePresenter{
        void getArea();
        void getTopic();
    }
    interface  IToogleModel{
        void getArea(int userId,OnHttpCallBack<List<ToggleBean>> callBack);
        void getTopic(int page,int pageSize,String areaId, OnHttpCallBack<List<ManageBean>> callBack);

    }
}
