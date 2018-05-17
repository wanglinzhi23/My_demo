package com.intellif.eyecloud.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.BKAdapter;
import com.intellif.eyecloud.bean.ImageBean;
import com.intellif.eyecloud.bean.post.EventBusBean;
import com.intellif.eyecloud.presenter.AddBKPersenter;
import com.intellif.eyecloud.utils.ActivityControl;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.ExitDialog;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IAddBkContact;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.orhanobut.logger.Logger;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBKActivity extends TakePhotoActivity implements IAddBkContact.IAddBKView, TakePhoto.TakeResultListener,InvokeListener {
    @BindView(R.id.lv_bk)
    RecyclerView lv_bk;
    @BindView(R.id.bt_add_bk)
    Button bt_add_bk;
    @BindView(R.id.tv_addbk_name)
    EditText et_addbk_name;
    @BindView(R.id.tv_addbk_des)
    EditText getEt_addbk_des;
    private BKAdapter adapter;
    private List<String> mlist = new ArrayList<>();
    private List<String> list_id = new ArrayList<>();
    private int OPENCAMERA=0;
    private int OPENXE=0;
    private InvokeParam invokeParam;
    private  Uri uri;
    private TakePhoto takePhoto;
    private CropOptions cropOptions;  //裁剪参数
    private CompressConfig compressConfig;  //压缩参数
    private CustomProgress progress;
    private AddBKPersenter presenter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindow();
        getTakePhoto().onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bk);
        ActivityControl.addActivity(this);
        ButterKnife.bind(this);
        presenter = new AddBKPersenter(this);
        takePhoto = getTakePhoto();
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        lv_bk.setLayoutManager(layoutManager);
        adapter  =new BKAdapter(mlist,this);
        lv_bk.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progress = new CustomProgress(AddBKActivity.this,"");
        initEvent();
        initTitle();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEventMainThread(EventBusBean bean) {
        if(bean.EventId==1){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            getApplicationContext().startActivity(intent);
            ActivityControl.finishAll();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    private void initData() {
        //设置裁剪参数
        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
        //设置压缩参数
        compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
        getTakePhoto().onEnableCompress(compressConfig,true);  //设置为需要压缩
    }
    private void initTitle() {
     findViewById(R.id.ln_back).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             finish();
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
    @Override
    public void showDialog(String message) {
        final ExitDialog dialog=new ExitDialog(AddBKActivity.this,"提示",message);
        dialog.show();
        dialog.setRingMiss();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public String getArea() {
        String areaId = SPContent.getArea(AddBKActivity.this);
        return areaId;
    }
    @Override
    public List<String> getImageId() {
        return list_id;
    }
    @Override
    public String getPersonName() {
        return et_addbk_name.getText().toString().trim();
    }
    @Override
    public String getPersonDes() {
        return getEt_addbk_des.getText().toString().trim();
    }
    @Override
    public void setImage(ImageBean imageBean) {
        mlist.add(imageBean.uri);
        list_id.add(imageBean.id);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showInfo(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void activityFinish() {
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     *
     * 添加布控照片的点击事件
     */
    private void initEvent() {
        adapter.setOnItemClickListener(new BKAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mlist.size()==position){
                    showPopueWindow();
                }else{
                    mlist.remove(position);
                    list_id.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    @OnClick(R.id.bt_add_bk)
        void selectPic(){
        showDialog(AddBKActivity.this,"提示","确定提交当前布控");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    private void showPopueWindow(){
        View view = View.inflate(this,R.layout.pop_camera,null);
        final TextView camera = (TextView) view.findViewById(R.id.pop_camera);
        TextView openxe = (TextView) view.findViewById(R.id.pop_openxe);
        TextView cancle = (TextView) view.findViewById(R.id.pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/5;
        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                uri =  getImageCropUri();
                takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
                Logger.e("sssssssssssssssssssssssssssssssssssssssssssssssssss");
            }
        });
        openxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                uri =  getImageCropUri();
                takePhoto.onPickFromGalleryWithCrop(uri,cropOptions);

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.5f;
        getWindow().setAttributes(lp);
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }
    public void showDialog(Context context, String title, String message) {
        final ExitDialog dialog=new ExitDialog(context,title,message);
        dialog.show();
        dialog.setCallBack(new ExitDialog.DialogCallBack() {
            @Override
            public void onSure() {
                dialog.dismiss();
                presenter.addInfo();
            }
            @Override
            public void onFail() {
                dialog.dismiss();
            }
        });
    }
    /**
     * 第三方照相模块
     *
     */
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String iconPath = result.getImage().getOriginalPath();
        if(iconPath.isEmpty()){
            Toast.makeText(this, "选取照片失败，请重新选取", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(iconPath);
        presenter.uploadImage(file);

    }
    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    };
    @Override
    public void takeCancel() {
        super.takeCancel();
    }
    private void initPermission() {
        // 申请权限。
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .send();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }
    //权限申请回调接口
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            if(requestCode == 100) {
                // TODO 相应代码。
                //do nothing
            }
        }
        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(AddBKActivity.this, deniedPermissions)) {

                // 用自定义的提示语
                AndPermission.defaultSettingDialog(AddBKActivity.this, 103)
                        .setTitle("权限申请失败")
                        .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
                        .setPositiveButton("好，去设置")
                        .show();
            }
        }
    };
    //获得照片的输出保存Uri
    private Uri getImageCropUri() {
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists())file.getParentFile().mkdirs();
        return Uri.fromFile(file);
    }

    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.titleColor);//通知栏所需颜色
    }
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
