package com.intellif.eyecloud.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.intellif.eyecloud.R;
import com.intellif.eyecloud.base.BaseActivity;
import com.intellif.eyecloud.fragment.PictureSlideFragment;

import java.util.ArrayList;

public class PicViewerActivity extends BaseActivity {
    private ViewPager viewPager;
    private TextView tv_indicator;
    private ArrayList<String> urlList;
    private int postion=0;
    @Override
    public int getContentViewId() {
        return R.layout.activity_pic_viewer;
    }
    @Override
    protected void initAllMembersView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        ArrayList<String> list = intent.getStringArrayListExtra("img");
        postion = intent.getIntExtra("postion",0);
//        String [] urls={"http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T152245_1226.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170811/20170811T142648_7282.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170811/20170811T143411_7372.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T123824_85212.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T123211_85174.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T122901_85150.jpg",
//                "http://192.168.2.12/store1_0/ImgWareHouse/src_0_2/20170810/20170810T122527_85134.jpg",};//需要加载的网络图片
        urlList = new ArrayList<>();
        urlList.addAll(list);
//        Collections.addAll(urlList, urls);
        findViewById(R.id.ln_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);

        viewPager.setAdapter(new PictureSlidePagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(postion);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tv_indicator.setText(String.valueOf(position+1)+"/"+urlList.size());//<span style="white-space: pre;">在当前页面滑动至其他页面后，获取position值</span>
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }
    private  class PictureSlidePagerAdapter extends FragmentStatePagerAdapter {

        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PictureSlideFragment.newInstance(urlList.get(position));//<span style="white-space: pre;">返回展示不同网络图片的PictureSlideFragment</span>
        }
        @Override
        public int getCount() {
            return urlList.size();//<span style="white-space: pre;">指定ViewPager的总页数</span>
        }
    }
}
