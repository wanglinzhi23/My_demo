package com.intellif.eyecloud.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.intellif.eyecloud.R;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by intellif on 2017/9/8.
 */

public class BKAdapter extends RecyclerView.Adapter<BKAdapter.ViewHolder> implements View.OnClickListener {
    public List<String> datas = null;
    public Context context;
    private OnItemClickListener mOnItemClickListener = null;
    public BKAdapter(List<String> datas, Context context) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bk,viewGroup,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //viewHolder.mTextView.setText(datas.get(position));
        //将position保存在itemView的Tag中，以便点击时进行获取
        if (position == datas.size()) {
            viewHolder.bk_img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.add));
            if (position == 4) {
                viewHolder.bk_img.setVisibility(View.GONE);
            }
        } else {
            //GlideImgManager.loadImage(context,datas.get(position),viewHolder.bk_img);
            Picasso.with(context).load(datas.get(position)).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.bk_img);
        }
        viewHolder.itemView.setTag(position);
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        if (datas.size() == 3) {
            return 3;
        }
        return (datas.size() + 1);
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView bk_img;
        public ViewHolder(View view){
            super(view);
            bk_img = (ImageView) view.findViewById(R.id.item_bk_img);
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