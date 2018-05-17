package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.DeleteBean;
import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.bean.post.FaceBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.PeopleDetailModel;
import com.intellif.eyecloud.view.IPeopleDetailContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/12.
 */

public class PeopleDetailPresenter implements IPeopleDetailContact.IPeopleDetailPresenter {
    private IPeopleDetailContact.IPeopleDetailView peopleDetailView;
    private IPeopleDetailContact.IPeopleDetailModel peopleDetailModel;
    public PeopleDetailPresenter(IPeopleDetailContact.IPeopleDetailView peopleDetailView){
            this.peopleDetailView  = peopleDetailView;
            peopleDetailModel = new PeopleDetailModel();
    }
    @Override
    public void getEvent() {
        peopleDetailModel.getEvent( peopleDetailView.getPeopleId(),peopleDetailView.getPage(), peopleDetailView.getPageSize(),peopleDetailView.getSimilar(), new OnHttpCallBack<EventBean>() {
            @Override
            public void onSuccessful(EventBean eventBeen) {
                peopleDetailView.setList(eventBeen);
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    peopleDetailView.showInfo(errorMsg);
                }
                peopleDetailView.hideProgress();
            }
        });
    }
    @Override
    public void getEventReferch() {
        peopleDetailView.showProgress();
        peopleDetailModel.getEvent(peopleDetailView.getPeopleId(),peopleDetailView.getPage(), peopleDetailView.getPageSize(),peopleDetailView.getSimilar(), new OnHttpCallBack<EventBean>() {
            @Override
            public void onSuccessful(EventBean eventBeen) {
                peopleDetailView.setListReferch(eventBeen);
                peopleDetailView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                peopleDetailView.showInfo(errorMsg);
                }
                peopleDetailView.hideProgress();
            }
        });
    }

    @Override
    public void personDelete() {
        peopleDetailModel.DeletePeson(peopleDetailView.getPeopleId(), new OnHttpCallBack<DeleteBean>() {
            @Override
            public void onSuccessful(DeleteBean deleteBean) {
                peopleDetailView.onActivityFinish();
            }
            @Override
            public void onFaild(String errorMsg) {
                peopleDetailView.showInfo(errorMsg);
            }
        });
    }

    @Override
    public void getImages() {
        peopleDetailView.showProgress();
        peopleDetailModel.getImages(peopleDetailView.getPeopleId(), new OnHttpCallBack<List<FaceBean>>() {
            @Override
            public void onSuccessful(List<FaceBean> list) {
                peopleDetailView.setFace(list);
                peopleDetailView.hideProgress();
            }

            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    peopleDetailView.showInfo(errorMsg);
                }
                peopleDetailView.hideProgress();
            }
        });
    }
}
