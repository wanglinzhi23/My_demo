package com.intellif.eyecloud.adapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.bean.post.FaceBean;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by intellif on 2017/9/8.
 */

public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder> implements View.OnClickListener {
    public List<FaceBean> datas = null;
    public Context context;
    private OnItemClickListener mOnItemClickListener = null;
    public FaceAdapter(List<FaceBean> datas, Context context) {
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_person,viewGroup,false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
//        GlideImgManager.loadImage(context,datas.get(position).imageData,viewHolder.imageView);
        Picasso.with(context).load(datas.get(position).imageData).placeholder(R.mipmap.running).error(R.mipmap.running).into(viewHolder.imageView);
        viewHolder.itemView.setTag(position);
    }
    //获取数据的数量
    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ViewHolder(View view){
            super(view);
            imageView = (ImageView) view.findViewById(R.id.item_person_img);
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
