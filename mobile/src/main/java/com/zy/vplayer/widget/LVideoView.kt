package com.zy.vplayer.widget

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.zy.vplayer.R
import com.zy.vplayer.contract.IPlayerContract
import com.zy.vplayer.entity.RecordEntity
import com.zy.vplayer.utils.DateUtil
import kotlinx.android.synthetic.main.layout_player_control.view.*
import java.lang.ref.WeakReference
import java.util.*

class LVideoView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs), IPlayerContract.IView {


    val PERCENT_HEIGHT = 2f / 5f
    val MIN_HEIGHT = 450
    private var mVideoUri: Uri? = null
    private var mSurfaceView: SurfaceView? = null
    private var controllerView: View? = null
    private var mTitleLayout: ViewGroup? = null//顶部根布局
    private var mBottomLayout: ViewGroup? = null//底部根布局
    private var mTitleTv: TextView? = null//标题text
    private var mTitleTimeTv: TextView? = null//顶部time
    private var mBackIv: ImageView? = null//顶部返回image
    private var mController: IPlayerContract.IPlayerController? = null//控制器
    private var mSurfaceHeight: Int = 0//画面高度
    private var mPlayIv: ImageView? = null//播放按钮
    private var mNextIv: ImageView? = null//下一媒体按钮
    private var mPreviousIv: ImageView? = null//上一媒体按钮
    private var mCurrentPosition: Long = 0//当前播放位置
    private var mTimeHandler: TimeHandler? = null//进度更新
    private val mDelayHandler: Handler = Handler(Looper.getMainLooper())//延时器
    private var mProgressSeekBar: AppCompatSeekBar? = null//进度条
    private var mGestureListener: GestureDetectorCompat? = null//手势监听
    private var mControllerViewIsShow: Boolean = false//界面是否显示
    private var mLineBar: ProgressBar? = null//细进度条
    private var mLockIv: ImageView? = null//锁
    private val mLockId: Int = View.NO_ID//
    private var mLocked: Boolean = false//是否锁住
    private var mPlayerListIv: ImageView? = null//显示播放列表
    private var mModel: IPlayerContract.IModel? = null
    private var mMediaEntity: RecordEntity? = null

    init {
        fitsSystemWindows = true
        setBackgroundColor(Color.BLACK)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onInterceptTouchEvent(ev)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w < h) {
            mSurfaceHeight = (h * PERCENT_HEIGHT).toInt()
            if (mTitleLayout != null) {
                mTitleLayout!!.setBackgroundColor(Color.TRANSPARENT)
            }
            if (mBottomLayout != null) {
                mBottomLayout!!.setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            mSurfaceHeight = h
            if (mTitleLayout != null) {
                mTitleLayout!!.setBackgroundColor(Color.BLACK)
                mTitleLayout!!.background.alpha = 120
            }
            if (mBottomLayout != null) {
                mBottomLayout!!.setBackgroundColor(Color.BLACK)
                mBottomLayout!!.background.alpha = 120
            }
        }

        if (mSurfaceHeight < MIN_HEIGHT) {
            mSurfaceHeight = MIN_HEIGHT
        }
        resetSurfaceHeight()
    }

    private fun resetSurfaceHeight() {
        val params = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSurfaceHeight)
        params.gravity = Gravity.CENTER_VERTICAL
        mSurfaceView!!.layoutParams = params
    }

    fun setController(playerController: IPlayerContract.IPlayerController) {
        this.mController = playerController
    }

    fun setModel(model: IPlayerContract.IModel) {
        this.mModel = model
    }

    override fun setPlayMedia(entity: RecordEntity?) {
        mMediaEntity = entity
        setMediaPath(mMediaEntity!!.path)
        if (mController != null) {
            mController!!.seekToPosition(mMediaEntity!!.lastPlayPosition)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            stopPlayer()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            stopPlayer()
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    fun setMediaPath(path: String) {
        setMediaUri(Uri.parse(path))
        getTitle(path)
    }

    private fun getTitle(path: String) {
        val name = path.substring(path.lastIndexOf('/') + 1)
        setTitle(name)
    }

    fun setMediaUri(uri: Uri) {
        this.mVideoUri = uri
        initView()
        mController!!.startOrPause()
    }

    fun setTitle(title: String) {
        if (mTitleTv != null) {
            mTitleTv!!.text = title
        }
    }

    private fun initView() {
        if (mSurfaceView == null) {
            createAndAddSurfaceView(SurfaceView(context))
            loadControllerView()
        }
        showViewInLayout()
        mController!!.setSurfaceHolder(mSurfaceView!!.holder)
        mController!!.setVideoUri(mVideoUri!!)
    }

    private fun createAndAddSurfaceView(surfaceView: SurfaceView) {
        this.mSurfaceView = surfaceView
        val params = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSurfaceHeight)
        params.gravity = Gravity.CENTER_VERTICAL
        addView(mSurfaceView, params)
    }

    private fun loadControllerView() {
        controllerView = View.inflate(context, R.layout.layout_player_control, null)
        mTitleLayout = controllerView!!.media_controller_top_layout
        mTitleTv = controllerView!!.media_controller_display_title_tv
        mBackIv = controllerView!!.media_controller_back_iv
        mBottomLayout = controllerView!!.media_controller_bottom_layout
        mPlayIv = controllerView!!.media_controller_play_iv
        mNextIv = controllerView!!.media_controller_skip_next_iv
        mPreviousIv = controllerView!!.media_controller_previous_iv
        mProgressSeekBar = controllerView!!.media_controller_progress_bar
        mTitleTimeTv = controllerView!!.media_controller_top_sys_time_tv
        mPlayerListIv = controllerView!!.media_controller_player_list_iv
        mPlayerListIv!!.setOnClickListener(mClickListener)
        mBackIv!!.setOnClickListener(mClickListener)
        mTitleTv!!.setOnClickListener(mClickListener)
        mPlayIv!!.setOnClickListener(mClickListener)
        mProgressSeekBar!!.setOnSeekBarChangeListener(onDragProgressListener)
        controllerView!!.setOnTouchListener { _, event ->
            if (mGestureListener == null) {
                mGestureListener = GestureDetectorCompat(context, mGestureDetectorListener)
            }
            mGestureListener!!.onTouchEvent(event)
            true
        }
        updateTopLayoutTime()
        addView(controllerView)
    }

    private val mGestureDetectorListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            //前提是未锁住之前
            if (mLocked.not()) {
                //左边控制亮度
                if (e1!!.x < controllerView!!.measuredWidth / 2) {
                    if (Math.abs(distanceY) > 5) {
                        mModel!!.windowBrightness = mModel!!.windowBrightness + distanceY * 12
                        return true
                    }
                } else {//右边控制音量
                    if (Math.abs(distanceY) > 10) {
                        if (distanceY > 0) {
                            mModel!!.changeVolume(1)
                        } else {
                            mModel!!.changeVolume(0)
                        }
                        return true
                    }
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            println("双击")
            mController!!.startOrPause()
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (mControllerViewIsShow) {
                clearViewInLayout()
            } else {
                showViewInLayout()
            }
            return true
        }
    }

    fun showViewInLayout() {
        if (mLocked) {
            showLockedTime()
        } else {
            showTopAndBottomView()
            hideLockedTime()
            hideBottomLineBar()
        }
        showLockIv()
        delayHiddenViewInLayout()
        mControllerViewIsShow = true
    }

    fun clearViewInLayout() {
        hiddenTopAndBottomView()
        hideLockIv()
        hideLockedTime()
        showBottomLineBar()
        mControllerViewIsShow = false
    }

    private fun delayHiddenViewInLayout() {
        mDelayHandler.removeCallbacksAndMessages(null)
        if (mController!!.playerState == LController.PlayerState.STATE_PLAYING) {
            mDelayHandler.postDelayed({
                if (mController!!.playerState == LController.PlayerState.STATE_PLAYING) {
                    //没有锁住view
                    clearViewInLayout()
                }
            }, 2000)
        }
    }

    private val mClickListener = View.OnClickListener {
        v ->
        when (v.id) {
            R.id.media_controller_back_iv -> {
                mController!!.stopPlayer()
                mModel!!.destroy()
            }
            R.id.media_controller_play_iv -> {
                mController!!.startOrPause()
                println("player changed play or pause ..... ")
            }
            R.id.media_controller_player_list_iv -> {
                mModel!!.showMediaList()
            }
            View.NO_ID -> {
                if (!mLocked) {//锁住
                    mLockIv!!.setImageResource(R.mipmap.ic_lock_close)
                    mLocked = true
                    clearViewInLayout()
                } else {//解锁
                    mLockIv!!.setImageResource(R.mipmap.ic_lock_open)
                    mLocked = false
                    showViewInLayout()
                }
            }
        }
    }

    private class TimeHandler(v: LVideoView) : Handler() {
        private var viewWeak: WeakReference<LVideoView>? = null

        init {
            viewWeak = WeakReference(v)
        }

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (viewWeak!!.get() != null) {
                viewWeak!!.get()!!.updateTime()
            }
            removeCallbacksAndMessages(null)
            sendEmptyMessageDelayed(0, 1000)
        }
    }

    fun updateTime() {
        if (mController!!.duration > 0) {
            mCurrentPosition = mController!!.currentPosition
            val progress = mCurrentPosition * 100L / mController!!.duration
            mProgressSeekBar!!.progress = progress.toInt()
            if (mLineBar != null) {
                mLineBar!!.progress = progress.toInt()
            }
        }
        val time = DateUtil.getInstance().getTime(mCurrentPosition)
        controllerView!!.media_controller_time_display_tv.text = time
    }

    override fun onPlayerStateChanged(state: Int) {
        println("change state = $state .... ... ... ")
        updateTime()
        when (state) {
            LController.PlayerState.STATE_PLAYING -> {
                mPlayIv!!.setImageResource(R.mipmap.ic_pause_media)
                if (mTimeHandler == null) {
                    mTimeHandler = TimeHandler(this)
                }
                mTimeHandler!!.sendEmptyMessageDelayed(0, 1000)
                delayHiddenViewInLayout()
            }
            LController.PlayerState.STATE_PAUSE -> {
                mTimeHandler!!.removeCallbacksAndMessages(null)
                mPlayIv!!.setImageResource(R.mipmap.ic_play_media)
            }
            LController.PlayerState.STATE_PREPARING -> {
                mPlayIv!!.setImageResource(R.mipmap.ic_play_media)
            }
            LController.PlayerState.STATE_PREPARED -> {
                val time = DateUtil.getInstance().getTime(mController!!.duration)
                controllerView!!.media_controller_duration_display_tv.text = time
            }
            LController.PlayerState.STATE_STOP -> {//播放完成停止
                stopPlayer()
            }
            else -> {
                println("stateChanged   other state=$state")
            }
        }
    }

    fun stopPlayer(){
        mMediaEntity!!.duration = mController!!.duration
        mMediaEntity!!.lastPlayPosition = mController!!.currentPosition
        mModel!!.setRecord(mModel!!.currentMediaPosition,mMediaEntity)
        mModel!!.destroy()
        mTimeHandler!!.removeCallbacksAndMessages(null)
        mPlayIv!!.setImageResource(R.mipmap.ic_play_media)
        mProgressSeekBar!!.progress = 0
        controllerView!!.media_controller_time_display_tv.text = resources.getString(R.string.empty_time)
    }

    fun hiddenTopAndBottomView() {
        if (controllerView!!.media_controller_top_layout.visibility == View.GONE)
            return
        mDelayHandler.removeCallbacksAndMessages(null)
        controllerView!!.media_controller_top_layout.visibility = View.GONE
        controllerView!!.media_controller_bottom_layout.visibility = View.GONE
        showBottomLineBar()
    }

    fun showTopAndBottomView() {
        if (controllerView!!.media_controller_top_layout.visibility == View.VISIBLE)
            return

        //更新界面中系统时间
        updateTopLayoutTime()
        hideBottomLineBar()
        controllerView!!.media_controller_top_layout.visibility = View.VISIBLE
        controllerView!!.media_controller_bottom_layout.visibility = View.VISIBLE
    }

    /**
     * 更新顶部title布局中的时间
     */
    fun updateTopLayoutTime() {
        if (mTitleTimeTv != null) {
            mTitleTimeTv!!.text = DateUtil.getInstance().formatTime(Date())
        }
    }

    /**
     * 显示底部linebar
     */
    fun showBottomLineBar() {
        if (mLineBar == null) {
            mLineBar = View.inflate(context, R.layout.layout_line_progress, null) as ProgressBar
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelOffset(R.dimen.dp1))
            params.gravity = Gravity.BOTTOM
            addView(mLineBar, params)
        }
        mLineBar!!.visibility = View.VISIBLE
    }

    /**
     * 隐藏底部linebar
     */
    fun hideBottomLineBar() {
        if (mLineBar != null) {
            mLineBar!!.visibility = View.GONE
        }
    }

    fun showLockIv(): Boolean {
        if (mLockIv == null) {
            mLockIv = AppCompatImageView(context)
            mLockIv!!.id = mLockId
            mLockIv!!.setBackgroundResource(R.drawable.lock_view_background)
            mLockIv!!.background.alpha = 100
            mLockIv!!.setImageResource(R.mipmap.ic_lock_open)
            mLockIv!!.setOnClickListener(mClickListener)
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            params.leftMargin = 20
            params.gravity = Gravity.CENTER_VERTICAL
            addView(mLockIv, params)
        }
        mLockIv!!.visibility = View.VISIBLE
        return mLocked
    }

    fun hideLockIv(): Boolean {
        if (mLockIv != null) {
            mLockIv!!.visibility = View.GONE
        }
        return mLocked
    }

    fun showLockedTime() {
        val mTimeTv = AppCompatTextView(context)
        mTimeTv.setTextColor(Color.WHITE)
        mTimeTv.textSize = 15f
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.END or Gravity.TOP
        params.topMargin = 20
        params.rightMargin = 20
        mTimeTv.tag = "time"
        mTimeTv.text = DateUtil.getInstance().formatTime(Date())
        addView(mTimeTv, params)
    }

    fun hideLockedTime() {
        removeViewByTag("time")
    }

    fun removeViewByTag(tag: String) {
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            if (child.tag != null) {
                if (child.tag is String && child.tag!!.toString() == tag) {
                    removeView(child)
                    break
                }
            }
        }
    }

    private val onDragProgressListener = object : SeekBar.OnSeekBarChangeListener {
        var percent = 0L
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            val time = DateUtil.getInstance().getTime(seekBar!!.progress * percent)
            controllerView!!.media_controller_time_display_tv.text = time
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            mDelayHandler.removeCallbacksAndMessages(null)
            mTimeHandler!!.removeCallbacksAndMessages(null)
            percent = mController!!.duration / 100L
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            mController!!.seekToPosition(seekBar!!.progress * percent)
            mTimeHandler!!.sendEmptyMessageDelayed(0, 1000)
            delayHiddenViewInLayout()
            println("onStop......" + mController!!.playerState)
        }
    }

}