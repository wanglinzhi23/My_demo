package com.intellif.eyecloud.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseFragment;
import com.intellif.eyecloud.main.AddBKActivity;
import com.intellif.eyecloud.main.PeopleActivity;
import com.intellif.eyecloud.presenter.FunctionPresenter;
import com.intellif.eyecloud.view.IFunctionContact;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * Created by intellif on 2017/9/7.
 */

public class FunctionFragment extends BaseFragment implements IFunctionContact.IFunctionView{
    @BindView(R.id.ln_function_jichu)
    LinearLayout ln_function_jichu;
    @BindView(R.id.ln_function_kurongliang)
    LinearLayout ln_function_kurongliang;
    @BindView(R.id.ln_function_fujia)
    LinearLayout ln_function_fujia;
    @BindView(R.id.ln_function_bukong)
    LinearLayout ln_function_bukong;
    @BindView(R.id.ln_function_renyuan)
    LinearLayout ln_function_renyuan;
    @BindView(R.id.ln_function_renliu)
    LinearLayout ln_function_renliu;
    @BindView(R.id.ln_function_importent)
    LinearLayout ln_function_importent;
    @BindView(R.id.ln_function_bukongrenyuan)
    LinearLayout ln_function_bukongrenyuan;
    private FunctionPresenter functionPresenter;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_function;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        functionPresenter = new FunctionPresenter(this);
    }
    /**
     * 跳转到基础服务
     */
    @OnClick(R.id.ln_function_jichu)
    void  intentJiChu(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }
    //跳转到增加库容量
    @OnClick(R.id.ln_function_kurongliang)
    void  intentKurongliang(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }
    /**
     * 开通附加功能
     */
    @OnClick(R.id.ln_function_fujia)
    void  intentFuJia(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }
    /**
     * 增加布控
     */
    @OnClick(R.id.ln_function_bukong)
    void  intentBuKong(){
        Intent intent = new Intent(getActivity(), AddBKActivity.class);
        startActivity(intent);
    }
    /**
     * 人员库
     */
    @OnClick(R.id.ln_function_renyuan)
    void  intentRenYuan(){
        Intent intent = new Intent(getActivity(), PeopleActivity.class);
        startActivity(intent);
    }
    /**
     * 人流量统计
     */
    @OnClick(R.id.ln_function_renliu)
    void  intentRenLiu(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }
    /**
     * 重要客户统计
     */
    @OnClick(R.id.ln_function_importent)
    void  intentImportent(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }
    /**
     * 布控人员统计
     */
    @OnClick(R.id.ln_function_bukongrenyuan)
    void  intentBuKongRenYuan(){
        functionPresenter.showDialog(getActivity(),"温馨提示","该功能暂未开放");
    }


}