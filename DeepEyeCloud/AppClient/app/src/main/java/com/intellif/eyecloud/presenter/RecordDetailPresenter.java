package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.DeleteBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.RecordDetailModel;
import com.intellif.eyecloud.view.IRecordDetailContact;

/**
 * Created by intellif on 2017/9/12.
 */

public class RecordDetailPresenter implements IRecordDetailContact.IRecordDetailPresenter{
    private IRecordDetailContact.IRecordDetailModel recordDetailModel;
    private IRecordDetailContact.IRecordDetailView recordDetailView;
    public RecordDetailPresenter(IRecordDetailContact.IRecordDetailView recordDetailView){
        this.recordDetailView = recordDetailView;
        recordDetailModel = new RecordDetailModel();
    }


    @Override
    public void sendToServer() {
        recordDetailView.showProgress();
        recordDetailModel.sendToServer(recordDetailView.getId(), new OnHttpCallBack<DeleteBean>() {
            @Override
            public void onSuccessful(DeleteBean deleteBean) {
                recordDetailView.onActivityFinish();
                recordDetailView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                recordDetailView.showInfo(errorMsg);
                recordDetailView.hideProgress();
            }
        });
    }
}
