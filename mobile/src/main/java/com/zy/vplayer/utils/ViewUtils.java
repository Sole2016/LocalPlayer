package com.zy.vplayer.utils;

import android.view.View;

public class ViewUtils {
    public static void changeViewVisibility(View v,int state){
        if (v == null) {
            return;
        }
        if(state == v.getVisibility()){
            return;
        }
        v.setVisibility(state);
    }
}
