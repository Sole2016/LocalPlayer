package com.zy.vplayer.tv.simple;

import android.graphics.Color;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zy.vplayer.tv.R;

/**
 * @author ZhiTouPC
 */
public class CardPresenter extends Presenter{

    private static class SimpleViewHolder extends ViewHolder implements View.OnClickListener{

        public SimpleViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(),"Click,",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView cardView = new ImageCardView(parent.getContext());
        cardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ACTIVATED);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(Color.BLUE);
        return new SimpleViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        //设置卡片右下角小图标
//        ((ImageCardView)viewHolder.view).setBadgeImage(ContextCompat.getDrawable(viewHolder.view.getContext(),
//                R.drawable.app_banner));
        ((ImageCardView)viewHolder.view).setContentText("这是content");
        ((ImageCardView)viewHolder.view).setTitleText("这是Title");
        ((ImageCardView)viewHolder.view).setInfoAreaBackgroundColor(Color.RED);
        ((ImageCardView)viewHolder.view).setMainImage(ContextCompat.getDrawable(viewHolder.view.getContext(),
                R.drawable.app_banner),true);

        ((ImageCardView)viewHolder.view).setMainImageDimensions(200,200);

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
