package com.intellif.eyecloud.view;

import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.http.OnHttpCallBack;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public interface IPeopleContact {
    interface  IPeopleView{
        void showProgress();
        void hideProgress();
        void setPersonData(UserBean userBean);
        int getPageSize();
        int getPage();
        void showInfo(String msg);
        String getArea();
        void setList(List<PeopleBean> list);
        void setListReferch(List<PeopleBean> list);
    }
    interface  IPeoplePresenter{
        void getPeopleData();
        void getPeopleDataReferch();
    }
    interface IPeopleModel{
        void getPeopleDate(int page, int PageCode,String areaId, OnHttpCallBack<List<PeopleBean>> callBack);
    }
}
