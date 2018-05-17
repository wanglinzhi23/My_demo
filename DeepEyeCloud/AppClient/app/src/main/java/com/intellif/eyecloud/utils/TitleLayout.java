package com.intellif.eyecloud.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intellif.eyecloud.R;

/**
 * Created by intellif on 2017/9/14.
 */

public class TitleLayout extends LinearLayout {

    private TextView tv_title;

    public TitleLayout(Context context) {
        super(context, null);
    }

    public TitleLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);

        //引入布局
        LayoutInflater.from(context).inflate(R.layout.common_title, this);
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.ln_back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);

    }

    //显示活的的标题
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }
}
