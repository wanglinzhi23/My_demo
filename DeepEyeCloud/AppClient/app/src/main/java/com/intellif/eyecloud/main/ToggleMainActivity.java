package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.intellif.common.gson.GsonUtils;
import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.ToggleMainAdapter;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.bean.ToggleBean;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.presenter.TooglePresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IToogleContact;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ToggleMainActivity extends BaseActivity implements IToogleContact.IToolgleView{
    @BindView(R.id.lv_area)
    RecyclerView lv_area;
    @BindView(R.id.tv_dianpu_warm)
    TextView tv_dianpu_warm;
    @BindView(R.id.ln_back)
    LinearLayout ln_back;
    private List<ToggleBean> mlist = new ArrayList<>();
    private ToggleMainAdapter toggleAdapter;
    private TooglePresenter presenter;
    private CustomProgress progress;
    private long time=0;
    @Override
    public int getContentViewId() {
        return R.layout.activity_toggle1;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        presenter = new TooglePresenter(this);
        progress = new CustomProgress(mActivity,"");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        lv_area.setLayoutManager(layoutManager);
        toggleAdapter = new ToggleMainAdapter(mlist,mActivity);
        lv_area.setAdapter(toggleAdapter);
        presenter.getArea();
        toggleAdapter.setOnItemClickListener(new ToggleMainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            showDialog(mActivity,"提示","确定选择到当前店铺",position);
            }
        });
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
    void toLogin(){
        Intent intent = new Intent(ToggleMainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public void showInfo(String msg) {
        Toast.makeText(mActivity,msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getUserId() {
        int userId = GsonUtils.GsonToBean(SPContent.getUser(ToggleMainActivity.this), UserBean.OauthAIKRoleInfoSBean.class).id;
        return userId;
    }
    @Override
    public void setList(List<ToggleBean> mlist) {
        if(mlist.size()>0){
        this.mlist.addAll(mlist);
        toggleAdapter.notifyDataSetChanged();
        lv_area.setVisibility(View.VISIBLE);
        tv_dianpu_warm.setVisibility(View.GONE);
        }else{
            lv_area.setVisibility(View.GONE);
            tv_dianpu_warm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setTopicName(List<ManageBean> mlist) {
        Log.e("sss",mlist.toString());
        String topic="";
//        List<String> list = new ArrayList<>();
//        for (int i = 0; i <mlist.size(); i++) {
//            list.add(mlist.get(i).id+"");
//        }
        Gson gson = new Gson();
        topic = gson.toJson(mlist);
        Log.e("topic",topic);
        SPContent.saveTopic(ToggleMainActivity.this,topic);
        Intent intent = new Intent(ToggleMainActivity.this,MainActivity.class);
        startActivity(intent);
        EventBusBean bean = new EventBusBean();
        bean.EventId=2;
        bean.msg="";
        EventBus.getDefault().post(bean);
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
                SPContent.saveArea(ToggleMainActivity.this,mlist.get(postion).id);
                SPContent.saveAreaName(ToggleMainActivity.this,mlist.get(postion).name);
                Log.e("ssss",mlist.get(postion).id);
                toggleAdapter.notifyDataSetChanged();
                presenter.getTopic();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void exit() {
        if ((System.currentTimeMillis() - time) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
