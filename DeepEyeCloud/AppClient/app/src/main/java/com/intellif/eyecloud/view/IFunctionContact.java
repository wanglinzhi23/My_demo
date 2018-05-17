package com.intellif.eyecloud.view;

import android.content.Context;

/**
 * Created by intellif on 2017/9/8.
 */

public interface IFunctionContact {
    interface  IFunctionView {

    }
    interface  IFunctionPresenter{
        void showDialog(Context context,String title,String message);
    }
    interface IFunctionModel{
        void showDialog(Context context,String title,String message);
    }
}
