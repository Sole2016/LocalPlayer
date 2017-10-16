package com.zy.vplayer.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zy.vplayer.R;
import com.zy.vplayer.entity.RecordEntity;
import com.zy.vplayer.utils.ViewUtils;
import com.zy.vplayer.widget.ProgressDrawable;

import java.util.ArrayList;

public class QueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecordEntity> mQueueList;
    private Context context;
    private RequestOptions options = new RequestOptions();
    private AdapterItemTouchListener touchListener;
    private int mPlayingPosition;
    private ProgressDrawable mDrawable;

    public QueueAdapter(ArrayList<RecordEntity> mQueueList) {
        this.mQueueList = mQueueList;
        options.fitCenter();
        options.centerCrop();
        mDrawable = new ProgressDrawable(ProgressDrawable.VERTICAL);
        mDrawable.setAlpha(90);
    }

    public void setQueueList(ArrayList<RecordEntity> mQueueList) {
        this.mQueueList = mQueueList;
        notifyDataSetChanged();
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
        notifyDataSetChanged();
    }

    public void setTouchListener(AdapterItemTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public ArrayList<RecordEntity> getQueueList() {
        return mQueueList;
    }

    public RecordEntity getItem(int posi) {
        if (checkSafePosition(posi)) {
            return mQueueList.get(posi);
        } else {
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
        if (entity.getLastPlayPosition() != 0 && entity.getDuration() != 0) {
            float progress = entity.getLastPlayPosition() * 100L / entity.getDuration();
            ProgressDrawable mDrawable = new ProgressDrawable(ProgressDrawable.VERTICAL);
            mDrawable.setAlpha(100);
            mDrawable.setProgress((progress / 100f));
            ViewUtils.changeViewVisibility(queueHolder.mProgressView, View.VISIBLE);
            queueHolder.mProgressView.setBackgroundDrawable(mDrawable);
        } else {
            ViewUtils.changeViewVisibility(queueHolder.mProgressView, View.GONE);
        }
        Glide.with(queueHolder.articleIv).asBitmap()
                .load(entity.getPath()).apply(options).into(queueHolder.articleIv);
        queueHolder.titleTv.setText(String.valueOf(getName(entity.getPath())));
        queueHolder.timeTv.setText(String.valueOf(entity.getTime()));
        queueHolder.sizeTv.setText(String.valueOf(entity.getSize()));
    }

    private String getName(String path) {
        if (TextUtils.isEmpty(path)) {
            return "unknown";
        }
        return path.substring(path.lastIndexOf('/') + 1, path.length());
    }


    @Override
    public int getItemCount() {
        if (mQueueList == null)
            return 0;
        return mQueueList.size();
    }

    private static class QueueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView articleIv;//文件预览图
        private TextView titleTv;//文件名称
        //        private TextView pathTv;//文件路径
        private View mProgressView;//显示进度
        private TextView sizeTv;//显示文件大小
        private TextView timeTv;//创建时间
        private AdapterItemTouchListener l;

        private QueueHolder(View itemView, AdapterItemTouchListener l) {
            super(itemView);
            this.l = l;
            articleIv = (AppCompatImageView) itemView.findViewById(R.id.queue_item_article);
            titleTv = (TextView) itemView.findViewById(R.id.queue_item_name);
//            pathTv = (TextView) itemView.findViewById(R.id.queue_item_path);
            mProgressView = itemView.findViewById(R.id.queue_item_hint_progress);
            sizeTv = (TextView) itemView.findViewById(R.id.queue_item_size);
            timeTv = (TextView) itemView.findViewById(R.id.queue_item_time);
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
