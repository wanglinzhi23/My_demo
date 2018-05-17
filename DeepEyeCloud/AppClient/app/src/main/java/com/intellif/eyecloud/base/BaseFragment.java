package com.intellif.eyecloud.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by intellif on 2017/9/7.
 */

public abstract class BaseFragment extends Fragment {
    public abstract int getContentViewId();
    protected Context context;
    protected View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView =inflater.inflate(getContentViewId(),container,false);
        ButterKnife.bind(this,mRootView);//绑定framgent
        this.context = getActivity();
        initAllMembersView(savedInstanceState);
        return mRootView;
    }
    protected abstract void initAllMembersView(Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}