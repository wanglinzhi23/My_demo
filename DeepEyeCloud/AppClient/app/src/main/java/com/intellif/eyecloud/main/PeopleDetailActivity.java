package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.FaceAdapter;
import com.intellif.eyecloud.adapter.HistoryAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.EventBean;
import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.bean.post.FaceBean;
import com.intellif.eyecloud.presenter.PeopleDetailPresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.utils.Tools;
import com.intellif.eyecloud.view.IPeopleDetailContact;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class PeopleDetailActivity extends BaseActivity implements XRecyclerView.LoadingListener,IPeopleDetailContact.IPeopleDetailView {
    @BindView(R.id.lv_history)
    XRecyclerView lv_history;
    @BindView(R.id.history_img)
    RecyclerView history_img;
    @BindView(R.id.history_name)
    TextView history_name;
    @BindView(R.id.history_date)
    TextView history_time;
    @BindView(R.id.ln_back)
    LinearLayout ln_back;
    @BindView(R.id.ln_person_delete)
    LinearLayout ln_person_delete;
    @BindView(R.id.person_detail_name)
    TextView person_detail_name;
    private HistoryAdapter adapter;
    private FaceAdapter faceAdapter;
    private List<EventBean.EventsBean> mlist = new ArrayList<>();
    private List<FaceBean> face_list = new ArrayList<>();
    private PeopleBean peopleBean;
    private PeopleDetailPresenter presenter;
    private int page=1;
    private int pageSize=40;
    private CustomProgress progress;
    @Override
    public int getContentViewId() {
        return R.layout.activity_people_detail;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        peopleBean = (PeopleBean) intent.getSerializableExtra("people");
        progress = new CustomProgress(mActivity,"");
        GridLayoutManager layoutManager = new GridLayoutManager(this,5);
        LinearLayoutManager layoutManager_face = new LinearLayoutManager(this);
        layoutManager_face.setOrientation(LinearLayout.HORIZONTAL);
        lv_history.setLayoutManager(layoutManager);
        history_img.setLayoutManager(layoutManager_face);
        presenter = new PeopleDetailPresenter(this);
        lv_history.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_history.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        lv_history.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_history.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        lv_history.setArrowImageView(R.drawable.iconfont_downgrey);
        lv_history.setLoadingListener(this);
        adapter  =new  HistoryAdapter(mlist,this);
        faceAdapter = new FaceAdapter(face_list,this);
        lv_history.setAdapter(adapter);
        history_img.setAdapter(faceAdapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList<String> stringList =new ArrayList<String>();
                for (int i = 0; i <mlist.size() ; i++) {
                    stringList.add(mlist.get(i).scene);
                }
                Intent intent = new Intent(PeopleDetailActivity.this,PicViewerActivity.class);
                intent.putStringArrayListExtra("img",stringList);
                intent.putExtra("postion",position);
                startActivity(intent);
            }
        });
        setData();
        presenter.getEvent();
        presenter.getImages();
    }
    private void setData() {
        person_detail_name.setText(peopleBean.realName);
        if(peopleBean.description.isEmpty()||peopleBean.description==null){
            history_name.setText("会员");
        }else {
            history_name.setText(peopleBean.description);
        }
        history_time.setText(Tools.dateToStamp(peopleBean.updated+""));
    }
    @OnClick(R.id.ln_person_delete)
    void onPersonDelete(){
        showDialog(mActivity,"提示","确定删除当前布控人员？");
    }
    @OnClick(R.id.ln_back)
    void OnActivitydBck(){
        finish();
    }
    @Override
    public void onRefresh() {
            page=1;
        presenter.getEventReferch();
        lv_history.refreshComplete();
    }
    @Override
    public void onLoadMore() {
        page = page+1;
        presenter.getEvent();
        lv_history.loadMoreComplete();
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
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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
    public int getPeopleId() {
        return peopleBean.id;
    }

    @Override
    public int getSimilar() {
        return SPContent.getSimilar(mActivity);
    }

    @Override
    public void setList(EventBean list) {
        if(list==null){

        }else{
            Log.e("aaaa","9999999999999");
        mlist.addAll(list.events);
        adapter.notifyDataSetChanged();

        }
    }

    @Override
    public void setListReferch(EventBean bean) {
        mlist.clear();
        mlist.addAll(bean.events);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityFinish() {
        Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("personId",peopleBean.id);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void setFace(List<FaceBean> faceBeen) {
        face_list.addAll(faceBeen);
        faceAdapter.notifyDataSetChanged();
    }

    //展示删除的弹框
    public void showDialog(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                presenter.personDelete();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
}
