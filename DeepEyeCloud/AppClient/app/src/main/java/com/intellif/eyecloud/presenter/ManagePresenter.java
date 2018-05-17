package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.ManageModel;
import com.intellif.eyecloud.view.IManageContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/12.
 */

public class ManagePresenter implements IManageContact.IManagePresenter {
    private IManageContact.IManageView manageView;
    private IManageContact.IManageModel manageModel;
    public ManagePresenter(IManageContact.IManageView manageView){
        this.manageView = manageView;
        manageModel = new ManageModel();
    }
    @Override
    public void getSheBei() {
        manageView.showProgress();
        manageModel.getSheBei(manageView.getPage(),manageView.getPageSize(),manageView.getAreaId(), new OnHttpCallBack<List<ManageBean>>() {
            @Override
            public void onSuccessful(List<ManageBean> manageBeen) {
                manageView.setList(manageBeen);
                manageView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    manageView.showInfo(errorMsg);

                }
                manageView.hideProgress();
            }
        });
    }

    @Override
    public void getSheBeiReferch() {
        manageView.showProgress();
        manageModel.getSheBei(manageView.getPage(),manageView.getPageSize(),manageView.getAreaId(), new OnHttpCallBack<List<ManageBean>>() {
            @Override
            public void onSuccessful(List<ManageBean> manageBeen) {
                manageView.setListReferch(manageBeen);
                manageView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                manageView.showInfo(errorMsg);

                }
                manageView.hideProgress();
            }
        });
    }
}
