package com.intellif.eyecloud.main;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.fragment.FunctionFragment;
import com.intellif.eyecloud.fragment.MessageFragment;
import com.intellif.eyecloud.fragment.PersonFragment;
import com.intellif.eyecloud.fragment.RecordFragment;
import com.intellif.eyecloud.utils.ExitDialog;

import butterknife.BindView;
import butterknife.OnClick;
public class MainActivity extends BaseActivity {
    @BindView(R.id.bottom_img_message)
    ImageView bottom_img_message;
    @BindView(R.id.bottom_img_record)
    ImageView bottom_img_record;
    @BindView(R.id.bottom_img_function)
    ImageView bottom_img_function;
    @BindView(R.id.bottom_img_person)
    ImageView bottom_img_person;
    @BindView(R.id.bottom_tv_message)
    TextView bottom_tv_message;
    @BindView(R.id.bottom_tv_record)
    TextView bottom_tv_record;
    @BindView(R.id.bottom_tv_function)
    TextView bottom_tv_function;
    @BindView(R.id.bottom_tv_person)
    TextView bottom_tv_persons;
    Fragment messageFragment;
    Fragment recordFragment;
    Fragment functionFragment;
    Fragment personFragment;
    private int EXIT=0;
    private int DIANPU=101;
    private long time=0;

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        setSelect(1);
        setSelect(0);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 底部bottom的点击事件
     */
    @OnClick(R.id.bottom_message)
    void perssedMessage(){
        setSelect(0);
    }
    @OnClick(R.id.bottom_record)
    void perssedRecord(){
        setSelect(1);
    }
    @OnClick(R.id.bottom_function)
    void perssedFunction(){
        setSelect(2);
    }
    @OnClick(R.id.bottom_person)
    void perssedPerson(){
        setSelect(3);
    }
    /**
     * 重置全部的文字以及图片
     */
    private void setNormal(){
        bottom_img_message.setImageResource(R.mipmap.message_normal);
        bottom_img_record.setImageResource(R.mipmap.record_normal);
        bottom_img_function.setImageResource(R.mipmap.function_normal);
        bottom_img_person.setImageResource(R.mipmap.person_normal);
        bottom_tv_message.setTextColor(getResources().getColor(R.color.bottom));
        bottom_tv_record.setTextColor(getResources().getColor(R.color.bottom));
        bottom_tv_function.setTextColor(getResources().getColor(R.color.bottom));
        bottom_tv_persons.setTextColor(getResources().getColor(R.color.bottom));
    }
    private void setSelect(int i) {
        setNormal();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);

        switch (i)
        {
            case 0:
                if (messageFragment == null)
                {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.container, messageFragment);
                } else
                {
                    transaction.show(messageFragment);
                }
                bottom_img_message.setImageResource(R.mipmap.message_pressed);
                bottom_tv_message.setTextColor(getResources().getColor(R.color.button_color));
                break;
            case 1:
                if (recordFragment == null)
                {
                    recordFragment = new RecordFragment();transaction.add(R.id.container, recordFragment);
                } else
                {
                    transaction.show(recordFragment);

                }
                bottom_img_record.setImageResource(R.mipmap.record_pressed);
                bottom_tv_record.setTextColor(getResources().getColor(R.color.button_color));
                break;
            case 2:
                if (functionFragment == null)
                {
                    functionFragment = new FunctionFragment();
                    transaction.add(R.id.container, functionFragment);
                } else
                {
                    transaction.show(functionFragment);
                }
                bottom_img_function.setImageResource(R.mipmap.function_pressed);
                bottom_tv_function.setTextColor(getResources().getColor(R.color.button_color));

                break;
            case 3:
                if (personFragment == null)
                {
                    personFragment = new PersonFragment();
                    transaction.add(R.id.container, personFragment);
                } else
                {
                    transaction.show(personFragment);
                }
                bottom_img_person.setImageResource(R.mipmap.person_pressed);
                bottom_tv_persons.setTextColor(getResources().getColor(R.color.button_color));

                break;

            default:
                break;
        }

        transaction.commit();
    }
    private void hideFragment(FragmentTransaction transaction)
    {
        if (messageFragment != null)
        {
            transaction.hide(messageFragment);
        }
        if (recordFragment != null)
        {
            transaction.hide(recordFragment);
        }
        if (functionFragment != null)
        {
            transaction.hide(functionFragment);
        }
        if (personFragment != null)
        {
            transaction.hide(personFragment);
        }
    }
    //返回退出程序
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {//点击的是返回键
            final ExitDialog dialog = new ExitDialog(MainActivity.this, "提示", "确定退出慧眼云？");
            if (EXIT == 0) {
                EXIT = 1;
                dialog.show();
                dialog.setCallBack(new ExitDialog.DialogCallBack() {
                    @Override
                    public void onSure() {
                        dialog.dismiss();
//                        ActivityControl.finishAll();
                        System.exit(0);
                    }

                    @Override
                    public void onFail() {
                        dialog.dismiss();
                        EXIT = 0;
                    }
                });

            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                EXIT = 0;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
