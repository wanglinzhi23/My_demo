package com.intellif.eyecloud.setting;

import android.os.Bundle;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.utils.TitleLayout;
import com.intellif.eyecloud.utils.Tools;

import butterknife.BindView;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.tv_about_version)
    TextView tv_about_version;
    @Override
    public int getContentViewId() {
        return R.layout.activity_about;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        tv_about_version.setText(Tools.getVersionName(mActivity));
        initTitle();
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.guanyu));
    }
}
