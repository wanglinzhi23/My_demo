package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.MessageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public interface IMessageContact {
    interface  IMessageView{

        void showProgress();

        void hideProgress();

        void inform();//是否开启通知

        void setMessageList(List<MessageBean> list);

        void setMessageListReferch(List<MessageBean> list);

        void showInfo(String msg);

        int getPage();

        int getPageSize();

        int getSimilar();

        String getArea();


        void doneComplete(MessageBean.EventsBean bean);
    }

    interface  IMessagePresenter{
        List<MessageBean> getData();
        List<MessageBean> getDataReferch();
        void setError(String personIdm, int status);
        void setDone(String personId,int status);
    }

    interface IMessageModel{
        void getMessage(int page, int pageSize,int similar, String areaId, OnHttpCallBack<List<MessageBean>> callBack);
        void messageDone(String personId,int status, OnHttpCallBack<MessageBean.EventsBean> callBack);
    }

}
