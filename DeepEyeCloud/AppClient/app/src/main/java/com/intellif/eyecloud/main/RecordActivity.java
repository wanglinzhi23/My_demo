package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.presenter.RecordDetailPresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.utils.Tools;
import com.intellif.eyecloud.view.IRecordDetailContact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecordActivity extends BaseActivity implements IRecordDetailContact.IRecordDetailView {
    @BindView(R.id.bt_record_delete)
    Button bt_record_delete;
    @BindView(R.id.record_img)
    CircleImageView record_img;
    @BindView(R.id.record_leftimg)
    ImageView record_leftimg;
    @BindView(R.id.record_rightimg)
    ImageView record_rightimg;
    @BindView(R.id.record_area)
    TextView record_area;
    @BindView(R.id.record_name)
    TextView record_name;
    @BindView(R.id.record_time)
    TextView record_time;  @BindView(R.id.record_shenfen)
    TextView record_shenfen;
    @BindView(R.id.record_status)
    TextView record_status; @BindView(R.id.record_similar1)
    TextView record_similar1;
    RecordDetailPresenter presenter;
    private RecordBean bean;
    private CustomProgress progress;
    @Override
    public int getContentViewId() {
        return R.layout.activity_record;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        bean = (RecordBean) intent.getSerializableExtra("message");
        progress = new CustomProgress(mActivity,"");
        presenter = new RecordDetailPresenter(this);
        initTitle();
        setData();
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
    public int getId() {
        return bean.id;
    }

    public void setData() {
        record_name.setText(bean.bName);
        record_shenfen.setText(bean.description);
        record_similar1.setText(("相似度")+(bean.threshold+"").substring(2,4)+"%");
        record_time.setText(Tools.dateToStamp(bean.processTime+""));
        record_area.setText(bean.address);
        Picasso.with(RecordActivity.this).load(bean.blackSmallurl).into(record_img);
        Picasso.with(RecordActivity.this).load(bean.alarmSmallurl).into(record_rightimg);
        Picasso.with(RecordActivity.this).load(bean.blackSmallurl).into(record_leftimg);
        if(Integer.parseInt(bean.type+"")==1){
            record_status.setText("已处理");
        }else {
            record_status.setText("误报");
        }
    }
    @Override
    public void onActivityFinish() {
        Intent intent = new Intent();
        intent.putExtra("postion",bean.id);
        Log.e("sss",""+bean.id);
        setResult(RESULT_OK,intent);
        finish();
    }
    @Override
    public void showInfo(String info) {
        Toast.makeText(mActivity,info, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.record_leftimg)
        void BigLeftPhoto(){
        ArrayList<String> string = new ArrayList<String>();
        string.add(bean.blackSmallurl);
        Intent intent = new Intent(mActivity, PicViewerActivity.class);
        intent.putExtra("postion",0);
        intent.putStringArrayListExtra("img",string);
        startActivity(intent);
    }
    @OnClick(R.id.record_rightimg)
        void BigRightPhoto(){
        ArrayList<String> string = new ArrayList<String>();
        string.add(bean.alarmBigurl);
        Intent intent = new Intent(mActivity, PicViewerActivity.class);
        intent.putExtra("postion",0);
        intent.putStringArrayListExtra("img",string);
        startActivity(intent);
    }
    @OnClick(R.id.bt_record_delete)
    void recordDelete(){
        showDialog(this,"提示","是否确定删除当前记录?");
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.jilu));
    }
    public void showDialog(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                presenter.sendToServer();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
}
