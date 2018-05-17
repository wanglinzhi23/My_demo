package com.intellif.common.imageload;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.intellif.common.R;

import java.io.File;
/**
 * Created by Administrator on 2017-03-07.
 */

public class GlideImgManager {
    public static void loadImage(Context context, String url, int erroImg, int emptyImg, ImageView iv) {
        //原生 API
        Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).into(iv);
    } 
    public static void loadImage(Context context, String url, ImageView iv) {
        //原生 API
        Glide.with(context).load(url).crossFade().placeholder(R.drawable.ic_launcher_round).error(R.drawable.ic_launcher_round).into(iv);
    }

    public static void loadGifImage(Context context, String url, ImageView iv) {
        Glide.with(context).load(url).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(null).error(null).into(iv);
    }

    public static void loadImage(Context context, final File file, final ImageView imageView) {
        Glide.with(context)
                .load(file)
                .into(imageView);
    }
    public static void loadImage(Context context, final int resourceId, final ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .into(imageView);
    }
}
