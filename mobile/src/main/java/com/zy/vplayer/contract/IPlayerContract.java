package com.zy.vplayer.contract;

import android.net.Uri;
import android.os.Handler;
import android.view.SurfaceHolder;

public interface IPlayerContract {
    interface IView{
        void onPlayerStateChanged(int state);
    }

    interface IController{
        void startOrPause();
        void stopPlayer();
        long getCurrentPosition();
        long getDuration();
        int getPlayerState();

        void setSurfaceHolder(SurfaceHolder holder);
        void setVideoUri(Uri uri);
    }



}
