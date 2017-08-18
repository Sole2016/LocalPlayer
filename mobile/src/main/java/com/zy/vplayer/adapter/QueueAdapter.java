package com.zy.vplayer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zy.vplayer.R;
import com.zy.vplayer.entity.RecordEntity;

import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecordEntity> mQueueList;
    private Context context;
    private RequestOptions options = new RequestOptions();
    private AdapterItemTouchListener touchListener;
    private int mPlayingPosition;

    public QueueAdapter(ArrayList<RecordEntity> mQueueList) {
        this.mQueueList = mQueueList;
        options.fitCenter();
        options.centerCrop();
    }

    public void setQueueList(ArrayList<RecordEntity> mQueueList) {
        this.mQueueList = mQueueList;
        notifyDataSetChanged();
    }

    public void setPlayingPosition(int position){
        this.mPlayingPosition = position;
        notifyDataSetChanged();
    }

    public void setTouchListener(AdapterItemTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public ArrayList<RecordEntity> getQueueList(){
        return mQueueList;
    }

    public RecordEntity getItem(int posi){
        if(checkSafePosition(posi)){
            return mQueueList.get(posi);
        }else{
            return null;
        }
    }

    private boolean checkSafePosition(int pos) {
        return pos >= 0 && pos < mQueueList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new QueueHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_queue,
                parent, false), touchListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        QueueHolder queueHolder = (QueueHolder) holder;
        RecordEntity entity = mQueueList.get(position);
        if(mPlayingPosition == position){
            queueHolder.itemView.setBackgroundColor(Color.YELLOW);
        }else{
            queueHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        Glide.with(queueHolder.articleIv).asBitmap()
                .load(entity.getPath()).apply(options).into(queueHolder.articleIv);
        queueHolder.titleTv.setText(entity.getPath());

        if (entity.getDuration() > 0) {
            int progress = (int) (entity.getLastPlayPosition() * 100 / entity.getDuration());
            queueHolder.recordTv.setProgress(progress);
        } else {
            queueHolder.recordTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mQueueList == null)
            return 0;
        return mQueueList.size();
    }

    private static class QueueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView articleIv;
        private TextView titleTv;
        private ProgressBar recordTv;
        private AdapterItemTouchListener l;

        private QueueHolder(View itemView, AdapterItemTouchListener l) {
            super(itemView);
            this.l = l;
            articleIv = (ImageView) itemView.findViewById(R.id.queue_item_article);
            titleTv = (TextView) itemView.findViewById(R.id.queue_item_path);
            recordTv = (ProgressBar) itemView.findViewById(R.id.queue_item_last_position_bar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (l != null) {
                l.onItemTouch(this, getAdapterPosition());
            }
        }
    }
}
