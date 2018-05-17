package com.intellif.eyecloud.setting;

import android.os.Bundle;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.utils.TitleLayout;

public class QuestionActivity extends BaseActivity {
    @Override
    public int getContentViewId() {
        return R.layout.activity_question;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        initTitle();
    }
    private void initTitle() {
        TitleLayout titlelayout=(TitleLayout)findViewById(R.id.title_layout);
        //显示活动自定义标题
        titlelayout.setTitle(getString(R.string.setting_question));
    }
}
