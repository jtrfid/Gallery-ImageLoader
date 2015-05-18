package com.yetwish.horizatalscrollviewdemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yetwish.horizatalscrollviewdemo.HorizontalScrollViewAdapter;
import com.yetwish.horizatalscrollviewdemo.R;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yetwish on 2015-05-16
 */

public class MyHorizontalScrollView extends HorizontalScrollView implements
        View.OnClickListener {

    /**
     * 屏幕宽度
     */
    private int mScreenWidth;

    /**
     * 图片的宽度
     */
    private int mChildWidth;

    /**
     * horizontalScrollView 下的 linearLayout
     */
    private LinearLayout mContainer;

    /**
     * 每屏最多显示的View的个数
     */
    private int mCountOneScreen;

    /**
     * adapter
     */
    private HorizontalScrollViewAdapter mAdapter;

    /**
     * 保存View与位置的键值对
     */
    private Map<View, Integer> mViewPos = new HashMap<>();

    /**
     * 当前屏幕显示的最后一张图片的下标
     */
    private int mLastIndex;

    /**
     * 当前屏幕显示的第一张图片的下标
     */
    private int mFirstIndex;

    /**
     * 当前图片切换 回调接口
     */
    private CurrentImageChangedListener mItemChangedListener;

    /**
     * 点击图片 回调接口
     */
    private OnItemClickListener mItemClickListener;

    /**
     * 可额外添加的空白图片的个数
     */
    private int mAdditionalCount;

    /**
     * 标识是否已加载完所有空白图片
     */
    private boolean isLoaded = false;

    /**
     * 当前点击选中的图片
     */
    private int mCurrentClickedItem;

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 获取屏幕宽度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    /**
     * 初始化数据和adapter
     */
    public void initData(HorizontalScrollViewAdapter adapter) {
        mAdapter = adapter;
        mContainer = (LinearLayout) getChildAt(0);
        // 获取适配器的第一个view
        View view = mAdapter.getView(0, null, mContainer);
        mContainer.addView(view);
        // 计算当前view的宽高
        if (mChildWidth == 0) {
            mChildWidth = (int) getResources().getDimension(
                    R.dimen.gallery_width) + 1;
            // 计算每次加载多少个view
            mCountOneScreen = mScreenWidth / mChildWidth + 2;
            mAdditionalCount = mCountOneScreen-1;
            // 如果adapter中view 的总数比能一屏能加载的少，则把最多能加载数置为view总数
            if (mCountOneScreen > mAdapter.getCount())
                mCountOneScreen = mAdapter.getCount();
        }
        // 初始化第一屏幕
        initFirstScreenChild(mCountOneScreen);

    }

    /**
     * 加载第一屏的view
     */
    public void initFirstScreenChild(int mCountOneScreen) {
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();
        mViewPos.clear();
        for (int i = 0; i < mCountOneScreen; i++) {
            View view = mAdapter.getView(i, null, mContainer);
            view.setOnClickListener(this);
            //初始化时 默认选中第一个
            if (i == 0) view.setAlpha(1f);
            else view.setAlpha(0.5f);
            mContainer.addView(view);
            mViewPos.put(view, i);
            mLastIndex = i;
        }
        if (mItemChangedListener != null) {
            notifyCurrentItemChanged();
        }

    }

    private void notifyCurrentItemChanged() {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            mContainer.getChildAt(i).setAlpha(0.5f);
        }
        mItemChangedListener.onCurrentImgChanged(mFirstIndex, mContainer.getChildAt(0));
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int scrollX = getScrollX();
                // 如果当前scrollX 为view的宽度，加载下一张，移除第一张
                if (scrollX >= mChildWidth) {
                    loadNextImage();
                }
                // 如果scrollX = 0 ,加载上一张 移除最后一张
                if (scrollX == 0) {
                    loadPreImage();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 加载下一张image 并移除第一张
     */
    private void loadNextImage() {
        Log.w("TAG", "load Next image");
        View view;
        if (isLoaded) {
            return;
        }
        // 后面没有
        if (mFirstIndex >= mAdapter.getCount() - mCountOneScreen
                && mAdditionalCount > 0) {
            view = new ImageView(getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    (int) getContext().getResources().getDimension(R.dimen.gallery_width),
                    (int) getContext().getResources().getDimension(R.dimen.gallery_height)));
            mAdditionalCount--;
            if (mAdditionalCount == 0)
                isLoaded = true;
        } else {
            // 获取下一张图片
            view = mAdapter.getView(++mLastIndex, null, mContainer);
            view.setOnClickListener(this);
        }
        // 移除第一张图片，并将水平滚动位置置0
        scrollTo(0, 0);
        mViewPos.remove(mContainer.getChildAt(0));
        mContainer.removeViewAt(0);
        mContainer.addView(view);
        mViewPos.put(view, mLastIndex);
        //如果生成的next view并不是当前选择的view,则将其透明度设为50％,否则设为1
        if (mLastIndex != mCurrentClickedItem)
            view.setAlpha(0.5f);
        else view.setAlpha(1f);
        // 当后面还有图片时，更新第一张图片的下标
        if (mFirstIndex < mAdapter.getCount() - 1)
            mFirstIndex++;
        //通知item changed 回调
        if (mItemChangedListener != null) {
            notifyCurrentItemChanged();
        }
    }

    /**
     * 加载上一张，并移除最后一张
     */
    private void loadPreImage() {
        // 前面没有了
        if (mFirstIndex == 0) {
            return;
        }
        // 销毁
        else if (mAdditionalCount < mCountOneScreen-1) {
            mAdditionalCount++;
            isLoaded = false;
        }
        // 获取当前应该显示为第一张图片的下标
        int index = mFirstIndex - 1;
        if (index >= 0) {
            // 移除最后一张
            int oldViewPos = mContainer.getChildCount() - 1;
            mViewPos.remove(mContainer.getChildAt(oldViewPos));
            mContainer.removeViewAt(oldViewPos);
            View view = mAdapter.getView(index, null, mContainer);
            if (index != mCurrentClickedItem)
                view.setAlpha(0.5f);
            else view.setAlpha(1f);
            view.setOnClickListener(this);
            mContainer.addView(view, 0);
            mViewPos.put(view, index);
            // 水平滚动位置向左移动view的宽度个像素
            scrollTo(mChildWidth, 0);
            // 当滑动到最后一屏之前，每次load pre 则mCurrentPos --
            if (mFirstIndex <= mAdapter.getCount() - mCountOneScreen)
                mLastIndex--;
            mFirstIndex--;
            //通知item changed 回调
            if (mItemChangedListener != null) {
                notifyCurrentItemChanged();
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            for (int i = 0; i < mContainer.getChildCount(); i++) {
                mContainer.getChildAt(i).setAlpha(0.5f);
            }
            view.setAlpha(1f);
            mCurrentClickedItem = mViewPos.get(view);
            // 点击项与当前显示项之间 图片个数
            int itemCount = mViewPos.get(view) - mFirstIndex;
            // 点击时，由于每次只加载了显示在屏幕上的几个图片，所以应先根据itemCount加载出后面的图片，才能scroll，否则会无法滑动
            for (int i = 0; i < itemCount; i++) {
                loadNextImage();
            }
            smoothScrollBy(calculateScrollWidth(itemCount), 0);

            mItemClickListener.onItemClick(view, mViewPos.get(view));
        }
    }

    /**
     * 计算点击时 将点击项滑动到屏幕最左边，需要滑动的距离
     *
     * @param itemCount , 前后两项 间隔的图片个数
     * @return 需滑动的距离
     */
    private int calculateScrollWidth(int itemCount) {
        int scrollWidth;
        if (itemCount > 2)
            scrollWidth = mChildWidth * (itemCount - 3);
        else if (itemCount > 1) {
            scrollWidth = mChildWidth * (itemCount - 2);
        } else {
            scrollWidth = mChildWidth * (itemCount - 1);
        }
        return scrollWidth;
    }

    public interface CurrentImageChangedListener {
        void onCurrentImgChanged(int position, View viewIndicator);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setCurrentImageChangedListener(
            CurrentImageChangedListener listener) {
        mItemChangedListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }



//    /**
//     * TODO pause时是否需要进行回收？如果pause进行回收，则resume时要重新加载
//     * 回收bitmap
//     */
//    public void onDestroy() {
//        if (!mViewPos.isEmpty()) {
//            for (View view : mViewPos.keySet()) {
//                ImageView iv = (ImageView) view;
//                //获取iv中的bitmap
//                iv.setDrawingCacheEnabled(true);
//                iv.getDrawingCache().recycle();
//                iv.setDrawingCacheEnabled(false);
//            }
//        }
//    }

}
