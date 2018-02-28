package com.zy.vplayer.tv.simple;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.zy.vplayer.tv.R;
import com.zy.vplayer.tv.struct.FunctionManager;

import java.util.Locale;

/**
 * @author ZhiTouPC
 */
public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "SimpleAdapter";
    public static final String INTERFACE = TAG+"ItemClick";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SimpleHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_simple,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SimpleHolder){
            SimpleHolder simpleHolder = (SimpleHolder) holder;
            simpleHolder.textView.setText(String.format(Locale.CHINA,"Simple Item + %s",position));
            simpleHolder.textView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return 30;
    }

    private class SimpleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;

        private SimpleHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.simple_tv_item);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FunctionManager.getInstance().invokeParamOnly(INTERFACE,getAdapterPosition());
        }

    }

}
