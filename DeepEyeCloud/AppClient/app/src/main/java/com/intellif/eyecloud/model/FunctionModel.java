package com.intellif.eyecloud.model;

import android.content.Context;

import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.view.IFunctionContact;

/**
 * Created by intellif on 2017/9/8.
 */

public class FunctionModel implements IFunctionContact.IFunctionModel {
    @Override
    public void showDialog(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setRingMiss();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
}
