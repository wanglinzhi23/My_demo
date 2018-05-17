package com.intellif.eyecloud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.MessageBean;
import com.intellif.eyecloud.utils.Tools;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by intellif on 2017/9/8.
 */

public class InformAdapter extends RecyclerView.Adapter<InformAdapter.ViewHolder> implements View.OnClickListener {
    public List<MessageBean> datas = null;
    public Context context;
    public OnItemClickListener mOnItemClickListener = null;
    public OnItemRightClickListener mOnItemRightClickListener = null;
    public OnItemLeftClickListener mOnItemLeftClickListener = null;
    public InformAdapter(List<MessageBean> datas,Context context) {
        this.datas = datas;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public void setOnItemLeftClickListener(OnItemLeftClickListener listener) {
        mOnItemLeftClickListener = listener;
    }
    public void setOnItemRightClickListener(OnItemRightClickListener listener) {
        mOnItemRightClickListener = listener;
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.itenm_info_show){
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取position
                mOnItemClickListener.onItemClick(v,(int)v.getTag());
            }
        }
        if(v.getId()==R.id.item_inform_imgright){
            if (mOnItemRightClickListener != null) {
                //注意这里使用getTag方法获取position
                mOnItemRightClickListener.onItemRightClick(v,(int)v.getTag());
            }
        }
        if(v.getId()==R.id.item_inform_imgleft){
            if (mOnItemLeftClickListener != null) {
                //注意这里使用getTag方法获取position
                mOnItemLeftClickListener.onItemLeftClick(v,(int)v.getTag());
            }
        }

    }
    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }public static interface OnItemLeftClickListener {
        void onItemLeftClick(View view , int position);
    }public static interface OnItemRightClickListener {
        void onItemRightClick(View view , int position);
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inform,viewGroup,false);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(datas.get(position).events!=null){
        viewHolder.item_inform_name.setText(datas.get(position).realName);
        viewHolder.mTextView.setText(Tools.dateToStampTime4(""+datas.get(position).events.get(0).time));
        Picasso.with(context).load(datas.get(position).photoData).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.item_info_img);
        Picasso.with(context).load(datas.get(position).photoData).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.item_info_imgleft);
        Picasso.with(context).load(datas.get(position).events.get(0).imageData).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.item_info_imgright);
        viewHolder.item_info_area.setText(datas.get(position).events.get(0).address);
            Log.e("item_info_area",datas.get(position).events.get(0).address);
        viewHolder.item_info_similar.setText(("相似度")+(datas.get(position).events.get(0).confidence+"").substring(2,4)+"%");
        if(!datas.get(position).description.isEmpty()){
        viewHolder.item_inform_des.setText(datas.get(position).description);
        }
        }
        viewHolder.item_info_show.setOnClickListener(this);
        viewHolder.item_info_show.setTag(position);
        viewHolder.item_info_imgleft.setOnClickListener(this);
        viewHolder.item_info_imgleft.setTag(position);
        viewHolder.item_info_imgright.setOnClickListener(this);
        viewHolder.item_info_imgright.setTag(position);
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView item_info_area;
        public TextView item_info_similar;
        public TextView item_inform_name;
        public TextView item_inform_des;
        public LinearLayout item_info_show;
        public ImageView item_info_imgleft;
        public ImageView item_info_imgright;
        public ImageView item_info_img;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_inform_time);
            item_inform_name = (TextView) view.findViewById(R.id.item_inform_name);
            item_info_area = (TextView) view.findViewById(R.id.item_inform_area);
            item_inform_des = (TextView) view.findViewById(R.id.item_inform_des);
            item_info_similar = (TextView) view.findViewById(R.id.item_inform_similar);
            item_info_show = view.findViewById(R.id.itenm_info_show);
            item_info_imgright = view.findViewById(R.id.item_inform_imgright);
            item_info_imgleft = view.findViewById(R.id.item_inform_imgleft);
            item_info_img = view.findViewById(R.id.item_inform_img);
        }
    }
}