package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.PeopleModel;
import com.intellif.eyecloud.view.IPeopleContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public class PeoplePresenter implements IPeopleContact.IPeoplePresenter {
    private IPeopleContact.IPeopleView mPeopleView;
    private IPeopleContact.IPeopleModel mPeopleModel;
    public PeoplePresenter(IPeopleContact.IPeopleView mPeopleView){
        this.mPeopleView = mPeopleView;
        this.mPeopleModel = new PeopleModel();
    }
    @Override
    public void getPeopleData() {
        mPeopleModel.getPeopleDate(mPeopleView.getPage(), mPeopleView.getPageSize(), mPeopleView.getArea(),new OnHttpCallBack<List<PeopleBean>>() {

            @Override
            public void onSuccessful(List<PeopleBean> peopleBeen) {
                mPeopleView.setList(peopleBeen);
            }
            @Override
            public void onFaild(String errorMsg) {

            }
        });
    }

    @Override
    public void getPeopleDataReferch() {
        mPeopleView.showProgress();
        mPeopleModel.getPeopleDate(mPeopleView.getPage(), mPeopleView.getPageSize(), mPeopleView.getArea(),new OnHttpCallBack<List<PeopleBean>>() {

            @Override
            public void onSuccessful(List<PeopleBean> peopleBeen) {
                mPeopleView.setListReferch(peopleBeen);
                mPeopleView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                mPeopleView.showInfo(errorMsg);
                mPeopleView.hideProgress();
            }
        });
    }


}
