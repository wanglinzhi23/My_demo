package com.intellif.eyecloud.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intellif.common.gson.GsonUtils;
import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseFragment;
import com.intellif.eyecloud.bean.UserBean;
import com.intellif.eyecloud.main.ManageActivity;
import com.intellif.eyecloud.main.SimilarActivity;
import com.intellif.eyecloud.main.ToggleActivity;
import com.intellif.eyecloud.setting.SettingActivity;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IPersonContact;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by intellif on 2017/9/7.
 */
public class PersonFragment extends BaseFragment implements IPersonContact.IPersonView{
    @BindView(R.id.rl_setting)
    RelativeLayout rl_setting;
    @BindView(R.id.rl_similar)
    RelativeLayout rl_similar;
    @BindView(R.id.rl_manager)
    RelativeLayout rl_manager;
    @BindView(R.id.person_login_name)
    TextView person_login_name;
    @BindView(R.id.ln_qie)
    LinearLayout ln_qie;
    @BindView(R.id.person_xiangsi)
    TextView person_xiangsi;
    @BindView(R.id.person_circle)
    TextView person_circle;
    @BindView(R.id.person_name)
    TextView person_name;
    private final int TOGGLE=100;
    private final int SIMILAR=101;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_person;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        setData();
    }
    @Override
    public void setPersonData(UserBean userBean) {

    }

    //跳转到设置界面
    @OnClick(R.id.rl_setting)
    void intentSetting(){
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.rl_similar)
    void intentSimilar(){
        Intent intent = new Intent(getActivity(), SimilarActivity.class);
        startActivityForResult(intent,SIMILAR);
    }
    @OnClick(R.id.rl_manager)
    void intentManager(){
        Intent intent = new Intent(getActivity(), ManageActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.ln_qie)
    void qieIntent(){
        Intent intent  = new Intent(getActivity(),ToggleActivity.class);
        startActivityForResult(intent,TOGGLE);
    }
    private void setData(){
        Log.e("person",SPContent.getAreaName(getActivity()));
        if(!SPContent.getAreaName(getActivity()).isEmpty()){
        person_circle.setText(SPContent.getAreaName(getActivity()).substring(0,1));
        person_name.setText(SPContent.getAreaName(getActivity()));
        }
        person_xiangsi.setText(SPContent.getSimilar(getActivity())+"%");
        UserBean.OauthAIKUserInfoBean bean= GsonUtils.GsonToBean(SPContent.getUser(getActivity()),UserBean.OauthAIKUserInfoBean.class);
        person_login_name.setText(bean.login);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TOGGLE){
            person_circle.setText(SPContent.getAreaName(getActivity()).substring(0,1));
            person_name.setText(SPContent.getAreaName(getActivity()));
        }
        if(requestCode==SIMILAR){
            person_xiangsi.setText(SPContent.getSimilar(getActivity())+"%");
        }
    }
}
