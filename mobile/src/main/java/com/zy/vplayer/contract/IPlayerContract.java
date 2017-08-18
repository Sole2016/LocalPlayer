package com.zy.vplayer.contract;

import android.net.Uri;
import android.view.SurfaceHolder;

import com.zy.vplayer.entity.RecordEntity;

public interface IPlayerContract {
    interface IView {
        void onPlayerStateChanged(int state);

        void setPlayMedia(RecordEntity entity);
    }

    interface IPlayerController {
        void startOrPause();

        void stopPlayer();

        long getCurrentPosition();

        long getDuration();

        int getPlayerState();

        void setSurfaceHolder(SurfaceHolder holder);

        void setVideoUri(Uri uri);

        void seekToPosition(long position);

        void reStart();
    }

    interface IModel {
        void destroy();

        void setWindowBrightness(float brightness);//修改亮度

        float getWindowBrightness();//获取当前亮度

        void showMediaList();//显示播放列表

        void keepScreenOn(boolean flag);

        void changeVolume(int flag);//改变音量大小

        void hideMediaList();//隐藏播放列表

        RecordEntity getNextMedia();

        RecordEntity getPreviousMedia();

        void setRecord(int position, RecordEntity recordEntity);//更新列表中的数据

        void saveRecord();//保存本地记录

        int getCurrentMediaPosition();//获取当前媒体的位置
    }

}
