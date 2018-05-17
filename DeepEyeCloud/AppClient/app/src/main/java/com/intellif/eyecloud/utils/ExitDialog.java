package com.intellif.eyecloud.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.intellif.eyecloud.R;

/**
 *
 * Date: 2015-06-26
 * Time: 15:34
 * FIXME
 */

public class ExitDialog extends Dialog {
    private Context mContext;
    private Button mConfirm;
    private Button mCancel;
    private TextView tv_title;
    private TextView tv_content;
    private TextView line;
    private String title;
    private String content;
    private DialogCallBack callBack = null;
    public ExitDialog(Context context, String title, String content) {
        super(context, R.style.ExitDialog);
        mContext=context;
        this.content = content;
        this.title = title;
    }

    public ExitDialog(Context context, int theme) {
        super(context, theme);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        //设置为我们的布局
        this.setCanceledOnTouchOutside(false);
        //设置为点击对话框之外的区域对话框不消失
        mConfirm= (Button) findViewById(R.id.dialog_confirm);
        mCancel= (Button) findViewById(R.id.dialog_cancel);
        line= (TextView) findViewById(R.id.line);
        tv_title= (TextView) findViewById(R.id.title);
        tv_content= (TextView) findViewById(R.id.content);
        tv_title.setText(title);
        tv_content.setText(content);
        //设置事件
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callBack.onSure();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onFail();
            }
        });

    }
    public void  setRingMiss(){
        mCancel.setVisibility(View.GONE);
        mConfirm.setBackgroundResource(R.drawable.dialog_right_btn_selector_single);
        line.setVisibility(View.GONE);
    }
    public void setCallBack(DialogCallBack callBack1){
        callBack=callBack1;
    }
    public interface DialogCallBack{
        void onSure();
        void onFail();
    }
}
