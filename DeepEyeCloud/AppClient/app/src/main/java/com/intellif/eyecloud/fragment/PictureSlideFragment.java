package com.intellif.eyecloud.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseFragment;

import butterknife.BindView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by intellif on 2017/9/18.
 */

public class PictureSlideFragment extends BaseFragment {
    private String url;
    private PhotoViewAttacher mAttacher;
    @BindView(R.id.iv_main_pic)
    ImageView imageView;
    @Override
    public int getContentViewId() {
        return R.layout.fragment_picture_slide;
    }

    public static PictureSlideFragment newInstance(String url) {
        PictureSlideFragment f = new PictureSlideFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);
        return f;//获得一个包含图片url的PictureSlideFragmen的实例
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString("url")
                : "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T122527_85134.jpg";
    }

    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {

        mAttacher = new PhotoViewAttacher(imageView);//使用PhotoViewAttacher为图片添加支持缩放、平移的属性

        Glide.with(getActivity()).load(url).crossFade().into(new GlideDrawableImageViewTarget(imageView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                mAttacher.update();//调用PhotoViewAttacher的update()方法，使图片重新适配布局
            }
        });
    }
}
