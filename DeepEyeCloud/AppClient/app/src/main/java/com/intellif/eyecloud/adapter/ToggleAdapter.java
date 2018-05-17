package com.intellif.eyecloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.ToggleBean;
import com.intellif.eyecloud.utils.SPContent;
import java.util.List;
/**
 * Created by intellif on 2017/9/8.
 */
public class ToggleAdapter extends RecyclerView.Adapter<ToggleAdapter.ViewHolder> implements View.OnClickListener {
    public List<ToggleBean> datas = null;
    public Context context;
    private OnItemClickListener mOnItemClickListener = null;
    private void  setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    public ToggleAdapter(List<ToggleBean> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }
    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_manager,viewGroup,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String id = SPContent.getArea(context);
        Log.e("ToggleAdapter——save",id+"==============");
        Log.e("ToggleAdapter22--dianji",datas.get(position).id);
        if(datas.get(position).id.equals(id)){
            Log.e("ToggleAdapter","pressed");
        viewHolder.ln_toggle.setBackgroundResource(R.drawable.shap_dianpu_pressed);
        viewHolder.mTextView.setBackgroundColor(0xff5F6B85);
        }else{
            viewHolder.ln_toggle.setBackgroundResource(R.drawable.shap_dianpu);
            viewHolder.mTextView.setBackgroundColor(0xff444B5B);
        }
        viewHolder.mTextView.setText(datas.get(position).areaName);
        //将position保存在itemView的Tag中，以便点击时进行获取
        viewHolder.itemView.setTag(position);
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public LinearLayout ln_toggle;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_manager_name);
            ln_toggle = (LinearLayout) view.findViewById(R.id.ln_toggle);
        }
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
}