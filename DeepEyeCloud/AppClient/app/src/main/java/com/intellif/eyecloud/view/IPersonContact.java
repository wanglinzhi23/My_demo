package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public interface IPersonContact {
    interface  IPersonView{
        void setPersonData(UserBean userBean);
    }

    interface  IPersonPresenter{
        void getPersonData();
    }
    interface IPersonModel{
        void getPersonDate(int page, int PageCode, OnHttpCallBack<List<EventBean>> callBack);
    }
}
