package com.zy.vplayer.tv.simple;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.PlaybackFragment;
import android.support.v17.leanback.app.PlaybackOverlayFragment;
import android.support.v17.leanback.app.PlaybackSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.VideoSurfaceView;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zy.vplayer.tv.DetailsDescriptionPresenter;
import com.zy.vplayer.tv.R;

import java.io.File;
import java.io.IOException;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @author ZhiTouPC
 */
public class SimpleVideoFragment extends Fragment implements AudioManager.OnAudioFocusChangeListener {
    private String path = "/storage/sdcard/Download/Alone.mp4";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            showMsg("写卡权限有了");
        }

        if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            showMsg("读卡权限有了");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frament_video, container, false);
        VideoSurfaceView surfaceView = rootView.findViewById(R.id.video_surface);
        Context context = getActivity().getApplicationContext();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null){
            audioManager.requestAudioFocus(this,AudioManager.STREAM_DTMF, AudioManager.AUDIOFOCUS_GAIN);
        }
        initPlayer(surfaceView);
        return rootView;
    }

    private IMediaPlayer player;

    private void initPlayer(SurfaceView surfaceView) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"Alone.mp4");
        Uri uriForFile = FileProvider.getUriForFile(getActivity(), "com.zy.vplayer.fileprovider", file);
        System.out.println("downloadUrl:"+uriForFile.toString());
        player = new IjkExoMediaPlayer(getActivity().getApplicationContext());
        player.setOnPreparedListener(preparedListener);
        try {
            player.reset();
            player.setSurface(surfaceView.getHolder().getSurface());
            player.setDisplay(surfaceView.getHolder());
            player.setDataSource(getActivity().getApplicationContext(), uriForFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
    }

    private IMediaPlayer.OnPreparedListener preparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            showMsg("准备好了，开始播放");
            if (!iMediaPlayer.isPlaying()) {
                iMediaPlayer.start();
            }
            if (!player.isPlaying()) {
                player.start();
            }
        }
    };

    private void showMsg(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}
