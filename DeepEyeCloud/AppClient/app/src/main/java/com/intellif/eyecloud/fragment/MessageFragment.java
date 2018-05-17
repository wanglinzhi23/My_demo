package com.intellif.eyecloud.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.adapter.InformAdapter;
import com.intellif.eyecloud.base.BaseFragment;
import com.intellif.eyecloud.bean.MessageBean;
import com.intellif.eyecloud.bean.post.ListenerBean;
import com.intellif.eyecloud.main.PicViewerActivity;
import com.intellif.eyecloud.presenter.MessagePresenter;
import com.intellif.eyecloud.utils.CustomProgress;
import com.intellif.eyecloud.utils.IListener;
import com.intellif.eyecloud.utils.ListenerManager;
import com.intellif.eyecloud.utils.SPContent;
import com.intellif.eyecloud.view.IMessageContact;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by intellif on 2017/9/7.
 */
public class MessageFragment extends BaseFragment implements IMessageContact.IMessageView, XRecyclerView.LoadingListener, IListener {
    @BindView(R.id.lv_message)
    XRecyclerView lv_message;
    private List<MessageBean> mlist = new ArrayList<>();
    private InformAdapter adapter;
    @BindView(R.id.ln_ring)
    LinearLayout ln_ring;
    @BindView(R.id.ln_warm)
    LinearLayout ln_warm;
    @BindView(R.id.tv_warm)
    TextView tv_warm;
    @BindView(R.id.ring_img)
    ImageView ring_img;
    private int page=1;
    private int pageSize=20;
    private MessagePresenter presenter;
    CustomProgress progress;
    private int del_id=0;
    private boolean isReferch=false;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_message;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        progress = new CustomProgress(getActivity(),"");
        presenter = new MessagePresenter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lv_message.setLayoutManager(layoutManager);
        lv_message.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_message.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        lv_message.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lv_message.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        lv_message.setArrowImageView(R.drawable.iconfont_downgrey);
        lv_message.setLoadingListener(this);
//        lv_message.addItemDecoration(new DividerItemDecoration(
//                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new InformAdapter(mlist,getActivity());
        lv_message.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new InformAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showPopueWindow(position);
            }
        });
        adapter.setOnItemLeftClickListener(new InformAdapter.OnItemLeftClickListener() {
            @Override
            public void onItemLeftClick(View view, int position) {
                ArrayList<String> string = new ArrayList<String>();
                string.add(mlist.get(position).photoData);
                Intent intent = new Intent(getActivity(), PicViewerActivity.class);
                intent.putExtra("postion",0);
                intent.putStringArrayListExtra("img",string);
                getActivity().startActivity(intent);
            }
        });
        adapter.setOnItemRightClickListener(new InformAdapter.OnItemRightClickListener() {
            @Override
            public void onItemRightClick(View view, int position) {
                ArrayList<String> string = new ArrayList<String>();
                string.add(mlist.get(position).events.get(0).scene);
                Log.e("image========",mlist.get(position).events.get(0).scene);
                Intent intent = new Intent(getActivity(), PicViewerActivity.class);
                intent.putExtra("postion",0);
                intent.putStringArrayListExtra("img",string);
                getActivity().startActivity(intent);
            }
        });
        presenter.getDataReferch();
        setData();
    }
    @OnClick(R.id.ln_ring)
    void Ring(){
        boolean result = SPContent.getRing(getActivity());
        if(result){
            ln_warm.setVisibility(View.VISIBLE);
            ring_img.setImageResource(R.mipmap.ring_close);
            SPContent.saveRing(getActivity(),false);
            tv_warm.setText("消息提醒已关闭");
        }else{
            ln_warm.setVisibility(View.VISIBLE);
            ring_img.setImageResource(R.mipmap.ring);
            SPContent.saveRing(getActivity(),true);
            tv_warm.setText("消息提醒已开启");
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ln_warm.setVisibility(View.GONE);
        }
    };
    @Override
    public void showProgress() {
        progress.show();
    }
    @Override
    public void hideProgress() {
        if(progress.isShowing()){
            progress.dismiss();
        }
    }
    /**
     * 判断是否开启消息通知
     */
    @Override
    public void inform() {
        progress.show();
    }
    //列表数据
    @Override
    public void setMessageList(List<MessageBean> list) {
        mlist.addAll(list);
        Log.e("messageFragment",mlist.size()+"===============================");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setMessageListReferch(List<MessageBean> list) {
        Log.e("ssss","刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新刷新");
        mlist.clear();
        mlist.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showInfo(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getSimilar() {
        return SPContent.getSimilar(getActivity());
    }

    @Override
    public String getArea() {
        return SPContent.getArea(getActivity());
    }

    @Override
    public void doneComplete(MessageBean.EventsBean bean) {
        for (int i = 0; i <mlist.size(); i++) {
            if(mlist.get(i).events.get(0).id==del_id){
                mlist.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
        ListenerBean listenerBean = new ListenerBean();
        listenerBean.code=2;
        ListenerManager.getInstance().sendBroadCast(listenerBean);
    }

    @Override
    public void onRefresh() {
        page=1;
        presenter.getDataReferch();
        lv_message.refreshComplete();
        isReferch=false;
    }

    @Override
    public void onLoadMore() {
        page = page+1;
        presenter.getData();
        lv_message.loadMoreComplete();
    }

    private void showPopueWindow(final int postion){
        View view = View.inflate(getActivity(),R.layout.pop_message,null);
        TextView done = (TextView) view.findViewById(R.id.pop_done);
        TextView error = (TextView) view.findViewById(R.id.pop_error);
        TextView cancle = (TextView) view.findViewById(R.id.pop_cancle);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels*1/5;

        final PopupWindow popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                presenter.setDone(mlist.get(postion).events.get(0).id+"",1);
                del_id=mlist.get(postion).events.get(0).id;

            }
        });
        error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                presenter.setError(mlist.get(postion).events.get(0).id+"",2);
                del_id=mlist.get(postion).events.get(0).id;

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    /**
     * 设置当前是否接收消息
     */
    private void setData() {
        boolean result = SPContent.getRing(getActivity());
        if(result){
            ring_img.setImageResource(R.mipmap.ring);
        }else{
            ring_img.setImageResource(R.mipmap.ring_close);
        }
    }
    @Override
    public void notifyAllActivity(ListenerBean bean) {
        if(bean.code==1){
            if(isReferch==false){
                new updateTask().execute();
            }

        }
        else if(bean.code==3){
            if(isReferch==false){
                new updateTask().execute();
            }
        }else{
            return;
        }
    }
    class updateTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
                presenter.getDataReferch();
        }
    }
}
