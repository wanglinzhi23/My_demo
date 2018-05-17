package com.intellif.eyecloud.view;

import android.content.Context;

import java.util.List;

/**
 * Created by intellif on 2017/9/18.
 */

public interface ISimilarContact {
    interface ISimailarView{
        void setList(List<String> list);
        void showToast(String message);
    }
    interface  ISimilarPresenter{
         void getList(Context context);
         void setSimilar(Context context,int data);
    }
    interface ISimilarModel{
        List<String> getList(Context context);
        boolean setSimilar(Context context,int data);
    }
}
