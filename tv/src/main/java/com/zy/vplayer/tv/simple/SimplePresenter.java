package com.zy.vplayer.tv.simple;

import android.graphics.Color;
import android.support.v17.leanback.widget.Presenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author ZhiTouPC
 */
public class SimplePresenter extends Presenter{
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(200,200));
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setBackgroundColor(Color.RED);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((TextView)viewHolder.view).setText("item +"+item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
