package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.SimilarAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.presenter.SimilarPresenter;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.view.ISimilarContact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SimilarActivity extends BaseActivity implements ISimilarContact.ISimailarView{
    @BindView(R.id.lv_similar)
    RecyclerView lv_similar;
    private SimilarAdapter similarAdapter;
    private List<String> mlist = new ArrayList<>();
    private SimilarPresenter presenter;
    @Override
    public int getContentViewId() {
        return R.layout.activity_similar;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        initTitle();
        similarAdapter = new SimilarAdapter(mlist,SimilarActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lv_similar.setLayoutManager(layoutManager);
        lv_similar.setAdapter(similarAdapter);
        presenter  = new SimilarPresenter(this);
        presenter.getList(SimilarActivity.this);
        similarAdapter.setOnItemClickListener(new SimilarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showDialog(SimilarActivity.this,"提示","是否选择当前相似度",mlist.get(position));
            }
        });
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.xiangshidushezhi));
    }
    @Override
    public void setList(List<String> list) {
        mlist.addAll(list);
        similarAdapter.notifyDataSetChanged();
    }
    @Override
    public void showToast(String message) {
        similarAdapter.notifyDataSetChanged();
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }
    public void showDialog(Context context, String title, String message, final String data) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                presenter.setSimilar(SimilarActivity.this,Integer.parseInt(data));
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
}
