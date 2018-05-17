package com.intellif.eyecloud.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.intellif.common.gson.GsonUtils;
import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.ToggleAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.ToggleBean;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.bean.post.ListenerBean;
import com.intellif.eyecloud.presenter.TooglePresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.IListener;
import com.intellif.eyecloud.utils.ListenerManager;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IToogleContact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ToggleActivity extends BaseActivity implements IToogleContact.IToolgleView, IListener {
    @BindView(R.id.lv_area)
    RecyclerView lv_area;
    @BindView(R.id.ln_back)
    LinearLayout ln_back;
    private List<ToggleBean> mlist = new ArrayList<>();
    private ToggleAdapter toggleAdapter;
    private TooglePresenter presenter;
    private CustomProgress progress;
    @Override
    public int getContentViewId() {
        return R.layout.activity_toggle;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        presenter = new TooglePresenter(this);
        progress = new CustomProgress(mActivity,"");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lv_area.setLayoutManager(layoutManager);
        toggleAdapter = new ToggleAdapter(mlist,mActivity);
        lv_area.setAdapter(toggleAdapter);
        presenter.getArea();
        toggleAdapter.setOnItemClickListener(new ToggleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            showDialog(mActivity,"提示","确定切换到当前店铺",position);
            }
        });
    }
    @OnClick(R.id.ln_back)
    void activityFinish(){
        this.finish();
    }
    @Override
    public void showProgress() {
        progress.show();
    }

    @Override
    public void hideProgress() {
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
    @Override
    public void showInfo(String msg) {
        Toast.makeText(mActivity,msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getUserId() {
        int userId = GsonUtils.GsonToBean(SPContent.getUser(ToggleActivity.this), UserBean.OauthAIKRoleInfoSBean.class).id;
        return userId;
    }
    @Override
    public void setList(List<ToggleBean> mlist) {
        this.mlist.addAll(mlist);
        toggleAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTopicName(List<ManageBean> mlist) {
        String topic="";
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i <mlist.size(); i++) {
//            list.add(mlist.get(i).id+"");
//        }
        Gson gson = new Gson();
        topic = gson.toJson(mlist);
        Log.e("topic",topic);
        SPContent.saveTopic(ToggleActivity.this,topic);
        ListenerBean bean = new ListenerBean();
        bean.code=1;
        ListenerManager.getInstance().sendBroadCast(bean);
//        EventBusBean bean = new EventBusBean();
//        bean.EventId=2;
//        bean.msg="";
//        EventBus.getDefault().post(bean);
        finish();
    }

    @Override
    public String getArea() {
        return SPContent.getArea(mActivity);
    }

    public void showDialog(Context context, String title, String message, final int postion) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                SPContent.saveArea(ToggleActivity.this,mlist.get(postion).id);
                SPContent.saveAreaName(ToggleActivity.this,mlist.get(postion).name);
                Log.e("ssss",mlist.get(postion).id);
                toggleAdapter.notifyDataSetChanged();
                presenter.getTopic();
//                EventBusBean bean = new EventBusBean();
//                bean.EventId=2;
//                bean.msg="";
//                EventBus.getDefault().post(bean);
                Log.e("ssss","already");
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void notifyAllActivity(ListenerBean bean) {
        Log.e("sss","aaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }
}
