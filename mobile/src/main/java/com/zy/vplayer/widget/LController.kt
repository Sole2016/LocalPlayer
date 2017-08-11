package com.zy.vplayer.widget

import android.content.Context
import android.net.Uri
import android.support.annotation.NonNull
import android.view.SurfaceView
import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.lang.ref.WeakReference
class LController {
    interface PlayerState{
        companion object {
            val STATE_PLAYING: Int = 0x001
            val STATE_PREPARING: Int = 0x002
            val STATE_PREPARED: Int = 0x003
            val STATE_ERROR:Int = 0x004
            val STATE_PAUSE:Int = 0x005
            val STATE_EMPTY:Int = 0x006//未指定
        }
    }
    private var mTargetState:Int = PlayerState.STATE_EMPTY
    private var mCurrentState:Int = LController.PlayerState.STATE_EMPTY
    private var mContextWeak:WeakReference<Context>?=null
    private var mViewWeak:WeakReference<LVideoView>?=null
    var mPlayer: IMediaPlayer? = null

    constructor(c:Context,view: LVideoView) {
        mViewWeak = WeakReference(view)
        mContextWeak = WeakReference(c.applicationContext)
        mPlayer = IjkExoMediaPlayer(c)
        mPlayer!!.setOnErrorListener(playerErrorListener)
        mPlayer!!.setOnPreparedListener(playerPreparedListener)
        mPlayer!!.setOnInfoListener(mediaInfoListener)
    }

    fun setSurface(@NonNull surfaceView: SurfaceView){
        mPlayer!!.setDisplay(surfaceView.holder)
    }

    fun setVideoUri(uri: Uri){
        mPlayer!!.setDataSource(mContextWeak!!.get(),uri)
        mPlayer!!.prepareAsync()
        mCurrentState = PlayerState.STATE_PREPARING
    }

    fun start(){
        if(mCurrentState == PlayerState.STATE_PREPARED){
            mPlayer!!.start()
            mCurrentState = PlayerState.STATE_PLAYING
        }
        mTargetState = PlayerState.STATE_PLAYING
        mPlayer!!.setOnTimedTextListener { mp, text ->
            println("timedTextChanged........."+text)
        }
    }

    val playerErrorListener = IMediaPlayer.OnErrorListener { mp, what, extra ->
        true
    }

    val playerPreparedListener = IMediaPlayer.OnPreparedListener {
        mp->
        if(mTargetState == PlayerState.STATE_PLAYING && mPlayer!!.isPlaying.not()){
            mp.start()
        }
    }

    val mediaInfoListener = IMediaPlayer.OnInfoListener { mp, what, extra ->
        println("mediaInfo....."+what)
        true
    }
}