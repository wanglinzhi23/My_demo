package com.intellif.eyecloud.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.bean.ManageBean;
import com.intellif.eyecloud.presenter.AddCameraPresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.view.IAddCameraContract;

import butterknife.BindView;
import butterknife.OnClick;

public class AddCameraActivity extends BaseActivity implements IAddCameraContract.IAddCameraView{
    @BindView(R.id.et_add_camdes)
    EditText et_add_camdes;
    @BindView(R.id.et_add_cameraId)
    EditText et_add_cameraId;
    @BindView(R.id.bt_add_camera)
    Button bt_add_camrea;
    private AddCameraPresenter presenter;
    private CustomProgress progress;
    @Override
    public int getContentViewId() {
        return R.layout.activity_add_camera;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
    initTitle();
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.tianjiashebei));
        progress  = new CustomProgress(mActivity,"");
        presenter = new AddCameraPresenter(this);
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
    public String getCameraId() {
        return et_add_cameraId.getText().toString();
    }
    @Override
    public String getCamDes() {
        return et_add_camdes.getText().toString();
    }
    @Override
    public String getAreaId() {
        return SPContent.getArea(mActivity);
    }
    @Override
    public void onFinish(ManageBean manageBean) {
        Log.e("name", manageBean.name);
        Intent intent = new Intent();
        intent.putExtra("manage",manageBean);
        setResult(RESULT_OK,intent);
        finish();
    }
    //点击添加camera的点击事件
    @OnClick(R.id.bt_add_camera)
    void addCamera(){
        showDialog(AddCameraActivity.this,"提示","确定添加当前设备");
    }
    /**
     * 显示dialog
     * @param context
     * @param title
     * @param message
     */
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