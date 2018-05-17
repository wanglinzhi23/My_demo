package com.intellif.eyecloud.fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.RecordAdapter;
import com.intellif.eyecloud.base.BaseFragment;
import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.bean.post.ListenerBean;
import com.intellif.eyecloud.main.RecordActivity;
import com.intellif.eyecloud.presenter.RecordPresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.IListener;
import com.intellif.eyecloud.utils.ListenerManager;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IRecordContact;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
/**
 * Created by intellif on 2017/9/7.
 */
public class RecordFragment extends BaseFragment implements IRecordContact.IRecordView, XRecyclerView.LoadingListener, IListener {
    @BindView(R.id.lv_record)
    XRecyclerView lv_record;
    private List<RecordBean> mlist = new ArrayList<>();
    private RecordAdapter adapter ;
    private int page=1;
    private int pageSize=20;
    private RecordPresenter presenter;
    private static  int DETAIL=101;
    private CustomProgress progress;
    private boolean isReferch=false;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_record;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        progress = new CustomProgress(getActivity(),"");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        presenter = new RecordPresenter(this);
        lv_record.setLayoutManager(layoutManager);
        lv_record.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_record.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        lv_record.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_record.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        lv_record.setArrowImageView(R.drawable.iconfont_downgrey);
//      lv_record.addItemDecoration(new DividerItemDecoration(
//      getActivity(), DividerItemDecoration.VERTICAL_LIST));
        lv_record.setLoadingListener(this);
        adapter = new RecordAdapter(mlist,getActivity());
        lv_record.setAdapter(adapter);
        presenter.referchData();
        initEvent();
    }
    /**
     * Item的点击事件
     */
    private void initEvent() {
        adapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                intent.putExtra("message",(Serializable) mlist.get(position));
                startActivityForResult(intent,DETAIL);
            }
        });
    }
    @Override
    public void setData(List<RecordBean> list) {
        mlist.clear();
        mlist.addAll(list);
        adapter.notifyDataSetChanged();
        isReferch=false;
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
    public int getPage() {
        return page;
    }
    @Override
    public int getPageSize() {
        return pageSize;
    }
    @Override
    public String getAreaId() {
        return SPContent.getArea(context);
    }
    @Override
    public void showInfo(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void setList(List<RecordBean> list) {
        mlist.addAll(list);
        adapter.notifyDataSetChanged();
    }
    @Override
    public int getSimilar() {
        return SPContent.getSimilar(getActivity());
    }

    @Override
    public void onRefresh() {
        page=1;
        presenter.referchData();
        lv_record.refreshComplete();
    }
    @Override
    public void onLoadMore() {
        page = page+1;
        presenter.getRecordList();
        lv_record.loadMoreComplete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ssss","aaaaaaaaaaaaaaaaaaaaaaqqq");
        if(requestCode==DETAIL){
            Log.e("ssss","aaaaaaaaaaaaaaaaaaaaaa");
            if (data==null){return;}
            int id = data.getIntExtra("postion",0);
            Log.e("RecordFragment",id+"===================aa");
            for (int i = 0; i <mlist.size(); i++) {
                if(mlist.get(i).id==id){
                    mlist.remove(i);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
    @Override
    public void notifyAllActivity(ListenerBean bean) {
        Log.e("cast","===================================================");
        if(bean.code==1){
            new updateTask().execute();
        } else if(bean.code==2){
            new updateTask().execute();
        }
    }
    /**
     * 异步更新界面信息
     */
    class updateTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isReferch==false){
                isReferch=true;
            presenter.referchData();
            }
        }
    }
}
