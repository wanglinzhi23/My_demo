package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.DeleteBean;
import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.bean.post.FaceBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/12.
 */

public interface IPeopleDetailContact {
    interface IPeopleDetailView{
        void showProgress();
        void hideProgress();
        void showInfo(String msg);
        int getPage();
        int getPageSize();
        int getPeopleId();
        int getSimilar();
        void setList(EventBean bean);
        void setListReferch(EventBean bean);
        void onActivityFinish();
        void setFace(List<FaceBean> faceBeen);
    }
    interface  IPeopleDetailPresenter{
       void  getEvent();
       void  getEventReferch();
        void personDelete();
        void getImages();
    }
    interface IPeopleDetailModel{
       void  getEvent(int PersonId, int page, int pageSize, int similar,OnHttpCallBack<EventBean> callBack);
        void DeletePeson(int personId, OnHttpCallBack<DeleteBean> callBack);
       void getImages(int personId,OnHttpCallBack<List<FaceBean>> callBack);
    }
}
