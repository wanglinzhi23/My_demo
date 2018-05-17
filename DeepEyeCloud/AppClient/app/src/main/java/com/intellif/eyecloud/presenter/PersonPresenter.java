package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.model.PersonModel;
import com.intellif.eyecloud.view.IPersonContact;

/**
 * Created by intellif on 2017/9/8.
 */

public class PersonPresenter implements IPersonContact.IPersonPresenter {
    private IPersonContact.IPersonView mPersonView;
    private IPersonContact.IPersonModel mPersonModel;
    public PersonPresenter(IPersonContact.IPersonView mPersonView){
        this.mPersonView = mPersonView;
        this.mPersonModel = new PersonModel();
    }
    @Override
    public void getPersonData() {
    }
}
