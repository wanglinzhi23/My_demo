package com.intellif.eyecloud.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.RecordBean;
import com.intellif.eyecloud.utils.Tools;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by intellif on 2017/9/8.
 */
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> implements View.OnClickListener {
    public List<RecordBean> datas = null;
    public Context context;
    private OnItemClickListener mOnItemClickListener = null;
    private void  setmOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    public RecordAdapter(List<RecordBean> datas,Context context) {
        this.datas = datas;
        this.context = context;
    }
    //define interface
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record,viewGroup,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.mTextView.setText(datas.get(position).bName);
        viewHolder.item_record_des.setText(datas.get(position).description);
        Picasso.with(context).load(datas.get(position).blackSmallurl).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.item_record_img);
        viewHolder.item_record_time.setText(Tools.dateToStamp(datas.get(position).processTime+""));
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
        public TextView item_record_time;
        public TextView item_record_des;
        public ImageView item_record_img;
        public ViewHolder(View view){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_reocrd_name);
            item_record_time = (TextView) view.findViewById(R.id.item_record_time);
            item_record_des = (TextView) view.findViewById(R.id.item_record_des);
            item_record_img = (ImageView) view.findViewById(R.id.item_record_img);
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