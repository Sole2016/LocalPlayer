package com.zy.vplayer.widget

import android.content.Context
import android.net.Uri
import android.view.SurfaceHolder
import com.zy.vplayer.contract.IPlayerContract
import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.lang.ref.WeakReference

class LController(c: Context, view: IPlayerContract.IView) : IPlayerContract.IPlayerController {
    interface PlayerState {
        companion object {
            val STATE_PLAYING: Int = 0x001
            val STATE_PREPARING: Int = 0x002
            val STATE_PREPARED: Int = 0x003
            val STATE_ERROR: Int = 0x004
            val STATE_PAUSE: Int = 0x005
            val STATE_STOP: Int = 0x006
            val STATE_EMPTY: Int = 0x007//未指定
            val STATE_SEEK_TO:Int = 0x008
        }
    }

    private var mTargetState: Int = PlayerState.STATE_EMPTY
    private var mCurrentState: Int = LController.PlayerState.STATE_EMPTY
    private var mContextWeak: WeakReference<Context>? = null
    private var mViewWeak: WeakReference<IPlayerContract.IView>? = null
    private var mSurfaceHolder:SurfaceHolder?=null
    var mPlayer: IMediaPlayer? = null
    private var mSeekPosition = 0L

    override fun setVideoUri(uri: Uri) {
        if(mCurrentState == PlayerState.STATE_PLAYING || mCurrentState == PlayerState.STATE_PAUSE){
            mPlayer!!.stop()
            mPlayer!!.reset()
            createPlayer()
        }
        mPlayer!!.setDataSource(mContextWeak!!.get(), uri)
        mPlayer!!.prepareAsync()
        mCurrentState = PlayerState.STATE_PREPARING
        notifyStateChange()
    }

    override fun startOrPause() {
        if (mCurrentState == PlayerState.STATE_PLAYING) {
            mPlayer!!.pause()
            mCurrentState = PlayerState.STATE_PAUSE
            mTargetState = PlayerState.STATE_PAUSE
        } else {
            if (mCurrentState == PlayerState.STATE_PAUSE ||
                    mCurrentState == PlayerState.STATE_PREPARED) {
                mPlayer!!.start()
                mCurrentState = PlayerState.STATE_PLAYING
            }
            mTargetState = PlayerState.STATE_PLAYING
        }
        notifyStateChange()
    }

    override fun stopPlayer() {
        if (mPlayer!!.isPlaying) {
            mCurrentState = PlayerState.STATE_PAUSE
            mPlayer!!.pause()
            mPlayer!!.release()
            mPlayer!!.reset()
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
        mSurfaceHolder = holder
        mPlayer!!.setDisplay(holder)
    }

    override fun getPlayerState(): Int {
        return mCurrentState
    }

    fun notifyStateChange() {
        if (mViewWeak!!.get() != null) {
            mViewWeak!!.get()!!.onPlayerStateChanged(mCurrentState)
        }
    }

    override fun reStart() {
        seekToPosition(0)
    }

    override fun seekToPosition(position: Long) {
        if(mCurrentState == PlayerState.STATE_PLAYING || mCurrentState == PlayerState.STATE_PAUSE){
            mPlayer!!.seekTo(position)
            mCurrentState = PlayerState.STATE_PREPARING
            mTargetState = PlayerState.STATE_PLAYING
            notifyStateChange()
            mCurrentState = mTargetState
            notifyStateChange()
        }else{
            if(mCurrentState == PlayerState.STATE_PREPARING){
                mSeekPosition = position
                mTargetState = PlayerState.STATE_SEEK_TO
            }
        }
    }

    val playerCompleteListener = IMediaPlayer.OnCompletionListener {
        mp ->
        mCurrentState = PlayerState.STATE_STOP
        notifyStateChange()
        mp.release()
        mp.stop()
    }

    val playerErrorListener = IMediaPlayer.OnErrorListener { mp, what, extra ->
        true
    }

    val playerPreparedListener = IMediaPlayer.OnPreparedListener {
        mp ->
        println("缓冲完成")
        mCurrentState = PlayerState.STATE_PREPARED
        notifyStateChange()
        if (mTargetState == PlayerState.STATE_PLAYING && mPlayer!!.isPlaying.not()) {
            mp.start()
            mCurrentState = mTargetState
            notifyStateChange()
        }

        if(mTargetState == PlayerState.STATE_SEEK_TO){
            mp.seekTo(mSeekPosition)
            mSeekPosition = 0L
            mp.start()
            mCurrentState = PlayerState.STATE_PLAYING
            mTargetState = PlayerState.STATE_PLAYING
            notifyStateChange()
        }
    }

    val mediaInfoListener = IMediaPlayer.OnInfoListener { _, what, _ ->
        println("mediaInfo....." + what)
        true
    }

    val seekToListener = IMediaPlayer.OnSeekCompleteListener {
        mp ->
        if (mTargetState == PlayerState.STATE_PLAYING) {
            mCurrentState = mTargetState
            mp.start()
            notifyStateChange()
        }
    }

    init {
        mViewWeak = WeakReference(view)
        mContextWeak = WeakReference(c.applicationContext)
        createPlayer()
    }

    fun createPlayer(){
        mPlayer = IjkExoMediaPlayer(mContextWeak!!.get())
        mPlayer!!.setOnErrorListener(playerErrorListener)
        mPlayer!!.setOnPreparedListener(playerPreparedListener)
        mPlayer!!.setOnInfoListener(mediaInfoListener)
        mPlayer!!.setOnCompletionListener(playerCompleteListener)
        mPlayer!!.setOnSeekCompleteListener(seekToListener)
        mPlayer!!.setScreenOnWhilePlaying(true)
        if(mSurfaceHolder != null){
            mPlayer!!.setSurface(mSurfaceHolder!!.surface)
        }
    }


}