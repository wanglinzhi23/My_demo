package com.intellif.eyecloud.setting;

import android.os.Bundle;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.utils.TitleLayout;
import com.suke.widget.SwitchButton;

import butterknife.BindView;
public class AlertsActivity extends BaseActivity {
    @BindView(R.id.alert_button)
    SwitchButton alert_button;
    @Override
    public int getContentViewId() {
        return R.layout.activity_alerts;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        initTitle();
        setData();
        alert_button.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    SPContent.saveRing(mActivity,true);
                    Toast.makeText(mActivity, "消息接收已开启", Toast.LENGTH_SHORT).show();
                }else{
                    SPContent.saveRing(mActivity,false);
                    Toast.makeText(mActivity, "消息接收已关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.setting_message));
    }
    /**
     * 设置当前是否接收消息
     */
    private void setData() {
        boolean result = SPContent.getRing(mActivity);
        if(result){
            alert_button.setChecked(true);
        }else{
            alert_button.setChecked(false);
        }
    }
}