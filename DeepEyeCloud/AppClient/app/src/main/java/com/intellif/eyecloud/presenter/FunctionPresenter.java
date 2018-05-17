package com.intellif.eyecloud.presenter;

import android.content.Context;

import com.intellif.eyecloud.model.FunctionModel;
import com.intellif.eyecloud.view.IFunctionContact;

/**
 * Created by intellif on 2017/9/8.
 */

public class FunctionPresenter implements IFunctionContact.IFunctionPresenter {
    private IFunctionContact.IFunctionView mfunctionView;
    private IFunctionContact.IFunctionModel mfunctionModel;
    public FunctionPresenter(IFunctionContact.IFunctionView mfunctionView) {
        this.mfunctionView = mfunctionView;
        mfunctionModel = new FunctionModel();
    }
    @Override
    public void showDialog(Context context, String title, String message) {
        mfunctionModel.showDialog(context,title,message);
    }
}
