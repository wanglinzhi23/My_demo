package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.AddCameraModel;
import com.intellif.eyecloud.view.IAddCameraContract;

/**
 * Created by intellif on 2017/9/12.
 */

public class AddCameraPresenter implements IAddCameraContract.IAddCameraPresenter {
    private IAddCameraContract.IAddCameraView addCameraView;
    private IAddCameraContract.IAddCameraModel addCameraModel;
    public AddCameraPresenter(IAddCameraContract.IAddCameraView addCameraView){
        this.addCameraView = addCameraView;
        addCameraModel = new AddCameraModel();
    }
    @Override
    public void sendToServer() {
        addCameraView.showProgress();
        if(addCameraView.getCameraId().isEmpty()){
            addCameraView.showInfo("设备编码输入不能为空");
            addCameraView.hideProgress();
            return;
        }
        if(addCameraView.getCameraId().length()>8){
            addCameraView.showInfo("设备编码字数不能超过8位");
            addCameraView.hideProgress();
            return;
        }
        if(addCameraView.getCamDes().isEmpty()){
            addCameraView.showInfo("设备描述不能为空");
            addCameraView.hideProgress();
            return;
        }
        addCameraModel.addCameraToServer(addCameraView.getCameraId(), addCameraView.getCamDes(),addCameraView.getAreaId(), new OnHttpCallBack<ManageBean>() {
            @Override
            public void onSuccessful(ManageBean manageBeen) {
                    addCameraView.onFinish(manageBeen);
            }
            @Override
            public void onFaild(String errorMsg) {
                addCameraView.showInfo(errorMsg);
                addCameraView.hideProgress();
            }
        });

    }
}
