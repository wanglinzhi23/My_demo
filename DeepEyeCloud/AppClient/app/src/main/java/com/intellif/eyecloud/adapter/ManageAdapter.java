package com.intellif.eyecloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.ManageBean;

import java.util.List;

/**
 * Created by intellif on 2017/9/8.
 */
public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ViewHolder> implements View.OnClickListener {
    public List<ManageBean> datas = null;
    public Context context;
    private OnItemClickListener mOnItemClickListener = null;
    private void  setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    public ManageAdapter(List<ManageBean> datas, Context context) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_manage,viewGroup,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(datas.get(position).name);
        //将position保存在itemView的Tag中，以便点击时进行获取
        if(datas.get(position).status==1){
            viewHolder.item_manage_status.setText("正常");
            viewHolder.item_manage_status.setTextColor(0xff660000);
        }else{
            viewHolder.item_manage_status.setText("异常");
            viewHolder.item_manage_status.setTextColor(0xffff0000);
        }
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
        public TextView item_manage_status;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_manage_num);
            item_manage_status = (TextView) view.findViewById(R.id.item_manage_status);
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

