package com.yetwish.horizatalscrollviewdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.yetwish.horizatalscrollviewdemo.widget.MyHorizontalScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    //实现滑动监听的HorizontalScrollView
    private MyHorizontalScrollView mSlideGallery;
    //实现点击监听的HorizontalScrollView
    private MyHorizontalScrollView mClickGallery;
    //用于展示当前选中的图片
    private ImageView mImg;

    //图片资源文件数组
    private List<Integer> mData = new ArrayList<Integer>(Arrays.asList(
            R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d,
            R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
            R.drawable.i, R.drawable.j, R.drawable.k));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImg = (ImageView) findViewById(R.id.id_content);
        mSlideGallery = (MyHorizontalScrollView) findViewById(R.id.slide_gallery);
        mClickGallery = (MyHorizontalScrollView) findViewById(R.id.click_gallery);
        //添加滚动回调
        mSlideGallery.setCurrentImageChangedListener(
                new MyHorizontalScrollView.CurrentImageChangedListener() {
                    @Override
                    public void onCurrentImgChanged(int position, View viewIndicator) {
                        mImg.setImageResource(mData.get(position));
                        viewIndicator.setAlpha(1f);
                    }
                });
        //初始化，配置adapter
        mSlideGallery.initData(new HorizontalScrollViewAdapter(this, mData));
        //添加点击回调
        mClickGallery.setOnItemClickListener(
                new MyHorizontalScrollView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mImg.setImageResource(mData.get(position));
            }
        });

        //初始化，配置adapter
        mClickGallery.initData(new HorizontalScrollViewAdapter(this, mData));

    }

}
