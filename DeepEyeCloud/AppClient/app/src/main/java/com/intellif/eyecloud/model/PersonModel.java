package com.intellif.eyecloud.model;

import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.view.IPersonContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public class PersonModel implements IPersonContact.IPersonModel {

    @Override
    public void getPersonDate(int page, int PageCode, OnHttpCallBack<List<EventBean>> callBack) {

    }
}
