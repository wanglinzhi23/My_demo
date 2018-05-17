package com.intellif.eyecloud.presenter;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.ToggleBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.ToogleModel;
import com.intellif.eyecloud.view.IToogleContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/21.
 */
public class TooglePresenter implements IToogleContact.ITooglePresenter {
    private IToogleContact.IToolgleView toolgleView;
    private  IToogleContact.IToogleModel toogleModel;
    public TooglePresenter(IToogleContact.IToolgleView toolgleView){
        this.toolgleView = toolgleView;
        this.toogleModel = new ToogleModel();
    }

    @Override
    public void getArea() {
        toolgleView.showProgress();
        toogleModel.getArea(toolgleView.getUserId(), new OnHttpCallBack<List<ToggleBean>>() {
            @Override
            public void onSuccessful(List<ToggleBean> httpResult) {
                toolgleView.setList(httpResult);
                toolgleView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                toolgleView.showInfo(errorMsg);
                toolgleView.hideProgress();
            }
        });
    }

    @Override
    public void getTopic() {
        toogleModel.getTopic(1, 10000, toolgleView.getArea(), new OnHttpCallBack<List<ManageBean>>() {
            @Override
            public void onSuccessful(List<ManageBean> manageBeen) {
                toolgleView.setTopicName(manageBeen);
            }
            @Override
            public void onFaild(String errorMsg) {
                toolgleView.showInfo(errorMsg);
            }
        });
    }
}
