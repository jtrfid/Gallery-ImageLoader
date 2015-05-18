package com.yetwish.horizatalscrollviewdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by yetwish on 2015-05-16
 */

public class HorizontalScrollViewAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private List<Integer> mData;

    public HorizontalScrollViewAdapter(Context context, List<Integer> mData)
    {
        mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public int getCount()
    {
        return mData.size();
    }

    public Object getItem(int position)
    {
        return mData.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.item_gallery, parent, false);
            viewHolder.mImg = (ImageView) convertView
                    .findViewById(R.id.item_gallery_iv);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mImg.setImageResource(mData.get(position));

        return convertView;
    }

    private class ViewHolder
    {
        ImageView mImg;
    }

}
