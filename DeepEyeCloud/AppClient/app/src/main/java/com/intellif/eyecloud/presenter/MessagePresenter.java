package com.intellif.eyecloud.presenter;

import com.intellif.eyecloud.bean.MessageBean;
import com.intellif.eyecloud.http.OnHttpCallBack;
import com.intellif.eyecloud.model.MessageModel;
import com.intellif.eyecloud.view.IMessageContact;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */

public class MessagePresenter implements IMessageContact.IMessagePresenter{
    private IMessageContact.IMessageView messageView;
    private IMessageContact.IMessageModel messageModel;
    public MessagePresenter(IMessageContact.IMessageView messageView){
        this.messageView = messageView;
        messageModel = new MessageModel();
    }
    @Override
    public List<MessageBean> getData() {
        messageModel.getMessage(messageView.getPage(), messageView.getPageSize(),messageView.getSimilar(), messageView.getArea(), new OnHttpCallBack<List<MessageBean>>() {
            @Override
            public void onSuccessful(List<MessageBean> messageBean) {
                messageView.setMessageList(messageBean);
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    messageView.showInfo(errorMsg);
                }

            }
        });
        return null;
    }
    @Override
    public List<MessageBean> getDataReferch() {
        messageView.showProgress();
        messageModel.getMessage(messageView.getPage(), messageView.getPageSize(),messageView.getSimilar(), messageView.getArea(), new OnHttpCallBack<List<MessageBean>>() {
            @Override
            public void onSuccessful(List<MessageBean> messageBean) {
                messageView.setMessageListReferch(messageBean);
                messageView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    messageView.showInfo(errorMsg);
                }
                messageView.hideProgress();

            }
        });
        return null;
    }

    @Override
    public void setError(String personId, int status) {
        messageView.showProgress();
        messageModel.messageDone(personId, status, new OnHttpCallBack<MessageBean.EventsBean>() {
            @Override
            public void onSuccessful(MessageBean.EventsBean messageBean) {
                messageView.doneComplete(messageBean);
                messageView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                if(!errorMsg.isEmpty()){
                    messageView.showInfo(errorMsg);
                }
                messageView.hideProgress();

            }
        });
    }
    @Override
    public void setDone(String personId, int status) {
        messageView.showProgress();
        messageModel.messageDone(personId, status, new OnHttpCallBack<MessageBean.EventsBean>() {
            @Override
            public void onSuccessful(MessageBean.EventsBean messageBean) {
                messageView.doneComplete(messageBean);
                messageView.hideProgress();
            }
            @Override
            public void onFaild(String errorMsg) {
                messageView.showInfo(errorMsg);
                messageView.hideProgress();
            }
        });
    }
}
