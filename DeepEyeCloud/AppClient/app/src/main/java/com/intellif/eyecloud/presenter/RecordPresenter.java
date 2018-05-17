package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.RecordModel;
import com.intellif.eyecloud.view.IRecordContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public class RecordPresenter implements IRecordContact.IRecordPresenter {
    private IRecordContact.IRecordView mRecordView;
    private IRecordContact.IRecordModel mRecordModel;
    public RecordPresenter(IRecordContact.IRecordView mRecordView) {
        this.mRecordView = mRecordView;
        mRecordModel = new RecordModel();
    }

    @Override
    public void getRecordList() {
        mRecordModel.getRecord(mRecordView.getPage(), mRecordView.getPageSize(), mRecordView.getAreaId(),mRecordView.getSimilar(), new OnHttpCallBack<List<RecordBean>>() {
            @Override
            public void onSuccessful(List<RecordBean> messageBeen) {
                mRecordView.setList(messageBeen);
            }
            @Override
            public void onFaild(String errorMsg) {
                mRecordView.showInfo(errorMsg);
            }
        });
    }

    @Override
    public void referchData() {
        mRecordView.showProgress();
        mRecordModel.getRecord(mRecordView.getPage(), mRecordView.getPageSize(), mRecordView.getAreaId(),mRecordView.getSimilar(), new OnHttpCallBack<List<RecordBean>>() {
            @Override
            public void onSuccessful(List<RecordBean> messageBeen) {
                mRecordView.setData(messageBeen);
                mRecordView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                mRecordView.showInfo(errorMsg);
                mRecordView.hideProgress();
            }
        });
    }
}
