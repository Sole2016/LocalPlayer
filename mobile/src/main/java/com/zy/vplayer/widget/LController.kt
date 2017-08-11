package com.zy.vplayer.widget

import android.content.Context
import android.net.Uri
import android.support.annotation.NonNull
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.zy.vplayer.contract.IPlayerContract
import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.lang.ref.WeakReference

class LController(c: Context, view: IPlayerContract.IView) :IPlayerContract.IController{
    interface PlayerState{
        companion object {
            val STATE_PLAYING: Int = 0x001
            val STATE_PREPARING: Int = 0x002
            val STATE_PREPARED: Int = 0x003
            val STATE_ERROR:Int = 0x004
            val STATE_PAUSE:Int = 0x005
            val STATE_STOP:Int = 0x006
            val STATE_EMPTY:Int = 0x007//未指定
        }
    }
    private var mTargetState:Int = PlayerState.STATE_EMPTY
    private var mCurrentState:Int = LController.PlayerState.STATE_EMPTY
    private var mContextWeak:WeakReference<Context>?=null
    private var mViewWeak:WeakReference<IPlayerContract.IView>?=null
    var mPlayer: IMediaPlayer? = null

    override fun setVideoUri(uri: Uri){
        mPlayer!!.setDataSource(mContextWeak!!.get(),uri)
        mPlayer!!.prepareAsync()
        mCurrentState = PlayerState.STATE_PREPARING
        notifyStateChange()
    }

    override fun startOrPause(){
        if(mCurrentState == PlayerState.STATE_PLAYING){
            mPlayer!!.pause()
            mCurrentState = PlayerState.STATE_PAUSE
            mTargetState = PlayerState.STATE_PAUSE
        }else{
            if(mCurrentState == PlayerState.STATE_PAUSE ||
                    mCurrentState == PlayerState.STATE_PREPARED){
                mPlayer!!.start()
                mCurrentState = PlayerState.STATE_PLAYING
            }
            mTargetState = PlayerState.STATE_PLAYING
        }
        notifyStateChange()
    }

    override fun stopPlayer() {
        if(mPlayer!!.isPlaying){
            mCurrentState = PlayerState.STATE_PAUSE
            mPlayer!!.pause()
            mPlayer!!.release()
        }
        notifyStateChange()
    }

    override fun getCurrentPosition(): Long {
        return mPlayer!!.currentPosition
    }

    override fun getDuration(): Long {
        return mPlayer!!.duration
    }

    override fun setSurfaceHolder(holder: SurfaceHolder?) {
        mPlayer!!.setDisplay(holder)
    }

    override fun getPlayerState(): Int {
        return mCurrentState
    }

    fun notifyStateChange(){
        if(mViewWeak!!.get() != null){
            mViewWeak!!.get()!!.onPlayerStateChanged(mCurrentState)
        }
    }

    val playerCompleteListener = IMediaPlayer.OnCompletionListener {
        mp->
        mp.stop()
        mp.release()
        mCurrentState = PlayerState.STATE_STOP
    }

    val playerErrorListener = IMediaPlayer.OnErrorListener { mp, what, extra ->





        true
    }

    val playerPreparedListener = IMediaPlayer.OnPreparedListener {
        mp->
        mCurrentState = PlayerState.STATE_PREPARED
        notifyStateChange()
        if(mTargetState == PlayerState.STATE_PLAYING && mPlayer!!.isPlaying.not()){
            mp.start()
            mCurrentState = mTargetState
            notifyStateChange()
        }
    }

    val mediaInfoListener = IMediaPlayer.OnInfoListener { mp, what, extra ->
        println("mediaInfo....."+what)
        true
    }

    init {
        mViewWeak = WeakReference(view)
        mContextWeak = WeakReference(c.applicationContext)
        mPlayer = IjkExoMediaPlayer(c)
        mPlayer!!.setOnErrorListener(playerErrorListener)
        mPlayer!!.setOnPreparedListener(playerPreparedListener)
        mPlayer!!.setOnInfoListener(mediaInfoListener)
        mPlayer!!.setOnCompletionListener(playerCompleteListener)
        mPlayer!!.setOnTimedTextListener { mp, text ->
            println("timedTextChanged........."+text)
        }
    }


}