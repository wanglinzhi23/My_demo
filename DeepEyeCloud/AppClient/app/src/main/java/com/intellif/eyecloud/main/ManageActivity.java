package com.intellif.eyecloud.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.ManageAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.presenter.ManagePresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IManageContact;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManageActivity extends BaseActivity implements IManageContact.IManageView, XRecyclerView.LoadingListener {
    @BindView(R.id.ln_manage_add)
    LinearLayout ln_manage_add;
    @BindView(R.id.ln_back)
    LinearLayout ln_back;
    @BindView(R.id.lv_manage)
    XRecyclerView lv_manage;
    private ManageAdapter manageAdapter;
    private List<ManageBean> mlist = new ArrayList<>();
    private ManagePresenter presenter;
    private final int ADD=101;
    private CustomProgress progress;
    private int page=1;
    private int pageSize=40;
    @Override
    public int getContentViewId() {
        return R.layout.activity_manage;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        presenter = new ManagePresenter(this);
        progress = new CustomProgress(ManageActivity.this,"");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_manage.setLayoutManager(layoutManager);
        lv_manage.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_manage.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        lv_manage.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_manage.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        lv_manage.setArrowImageView(R.drawable.iconfont_downgrey);
        lv_manage.setLoadingListener(this);
        manageAdapter = new ManageAdapter(mlist,mActivity);
        lv_manage.setAdapter(manageAdapter);
        presenter.getSheBeiReferch();
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
    @OnClick(R.id.ln_back)
    void onFinish(){
        finish();
    }
    @Override
    public String getAreaId() {
        String areaId = SPContent.getArea(mActivity);
        return areaId;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void showInfo(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setListReferch(List<ManageBean> mlist) {
        this.mlist.clear();
        this.mlist.addAll(mlist);
        manageAdapter.notifyDataSetChanged();
    }
    @Override
    public void setList(List<ManageBean> mlist) {
        this.mlist.addAll(mlist);
        manageAdapter.notifyDataSetChanged();
    }
    @OnClick(R.id.ln_manage_add)
    void addSheBei(){
        Intent intent = new Intent(ManageActivity.this,AddCameraActivity.class);
        startActivityForResult(intent,ADD);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==ADD){
            if(data==null){return;}
            ManageBean bean = (ManageBean) data.getSerializableExtra("manage");
            mlist.add(0,bean);
            String topic = SPContent.getTopic(ManageActivity.this);
            if (mlist.size()>0){

            topic = topic+",0/"+bean.id;
            }else{
                topic="0/"+bean.id;
            }
            SPContent.saveTopic(ManageActivity.this,topic);
            EventBusBean bean1 = new EventBusBean();
            bean1.EventId=2;
            bean1.msg="";
            EventBus.getDefault().post(bean1);
            manageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        page=1;
        presenter.getSheBeiReferch();
        lv_manage.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        page = page+1;
        presenter.getSheBei();
        lv_manage.loadMoreComplete();
    }
}
