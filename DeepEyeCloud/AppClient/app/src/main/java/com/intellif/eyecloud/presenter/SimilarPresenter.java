package com.intellif.eyecloud.presenter;

import android.content.Context;

import com.intellif.eyecloud.model.SimilarModel;
import com.intellif.eyecloud.view.ISimilarContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/18.
 */

public class SimilarPresenter implements ISimilarContact.ISimilarPresenter {
    private ISimilarContact.ISimailarView simailarView;
    private ISimilarContact.ISimilarModel similarModel;
    public  SimilarPresenter(ISimilarContact.ISimailarView simailarView){
        this.simailarView = simailarView;
        similarModel = new SimilarModel();
    }
    @Override
    public void getList(Context context) {
        List<String> list = similarModel.getList(context);
        simailarView.setList(list);
    }
    @Override
    public void setSimilar(Context context, int data) {
        boolean  flag = similarModel.setSimilar(context,data);
        if(flag){
            simailarView.showToast("设置成功");
        }else{
            simailarView.showToast("设置失败");
        }
    }
}
