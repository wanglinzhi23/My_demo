package com.intellif.eyecloud.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.PeopleAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.PeopleBean;
import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.presenter.PeoplePresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.view.IPeopleContact;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PeopleActivity extends BaseActivity implements XRecyclerView.LoadingListener,IPeopleContact.IPeopleView {
    @BindView(R.id.lv_people)
    XRecyclerView lv_people;
    private List<PeopleBean> mlist = new ArrayList<>();
    private PeopleAdapter adapter ;
    private PeoplePresenter presenter;
    private int page=1;
    private int pageSize=20;
    private CustomProgress progress;
    private static int DETAIL=101;
    @Override
    public int getContentViewId() {
        return R.layout.activity_people;
    }


    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        progress = new CustomProgress(mActivity,"");
        presenter = new PeoplePresenter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_people.setLayoutManager(layoutManager);
        lv_people.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_people.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        lv_people.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_people.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        lv_people.setArrowImageView(R.drawable.iconfont_downgrey);
//        lv_people.addItemDecoration(new DividerItemDecoration(
//                this, DividerItemDecoration.VERTICAL_LIST));
        lv_people.setLoadingListener(this);
        adapter = new PeopleAdapter(mlist,this);
        lv_people.setAdapter(adapter);
        RecordBean bean = new RecordBean();
//        for (int i = 0; i <20 ; i++) {
//            mlist.add(bean);
//        }
        adapter.notifyDataSetChanged();
        initEvent();
        initTitle();
        presenter.getPeopleData();
    }

    private void initEvent() {
       adapter.setOnItemClickListener(new PeopleAdapter.OnItemClickListener() {
           @Override
           public void onItemClick(View view, int position) {
               Intent intent = new Intent(PeopleActivity.this,PeopleDetailActivity.class);
               intent.putExtra("people",mlist.get(position));
               startActivityForResult(intent,DETAIL);
           }
       });
    }
    @Override
    public void onRefresh() {
        page=1;
        presenter.getPeopleDataReferch();
        lv_people.refreshComplete();
    }
    @Override
    public void onLoadMore() {
        page=page+1;
        presenter.getPeopleData();
        lv_people.loadMoreComplete();
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.renyuanku));
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
    public void setPersonData(UserBean userBean) {
    }
    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getPage() {
        return page;
    }
    @Override
    public void showInfo(String msg) {
        Toast.makeText(mActivity,msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getArea() {
        return SPContent.getArea(mActivity);
    }

    @Override
    public void setList(List<PeopleBean> list) {
        mlist.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setListReferch(List<PeopleBean> list) {
        mlist.clear();
        mlist.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==DETAIL){
            if(data==null){return;}
            int PersonId =data.getIntExtra("personId",0);
            for (int i = 0; i <mlist.size() ; i++) {
                if(mlist.get(i).id==PersonId){
                    mlist.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
