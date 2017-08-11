package com.zy.vplayer.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Canvas
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.zy.vplayer.R
import com.zy.vplayer.contract.IPlayerContract
import com.zy.vplayer.utils.DateUtil
import kotlinx.android.synthetic.main.layout_player_control.view.*
import java.lang.ref.WeakReference

class LVideoView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    val PERCENT_HEIGHT = 2f / 5f
    val MIN_HEIGHT = 450
    private var mVideoUri: Uri? = null
    private var mSurfaceView: SurfaceView? = null
    private var controllerView: View? = null
    private var mTitleLayout: ViewGroup? = null
    private var mBottomLayout: ViewGroup? = null
    private var mTitleTv: TextView? = null
    private var mBackIv: ImageView? = null
    private var mController: IPlayerContract.IController? = null
    private var mSurfaceHeight: Int = 0
    private var mPlayIv: ImageView? = null
    private var mNextIv: ImageView? = null
    private var mPreviousIv: ImageView? = null
    private var mCurrentPosition: Long = 0
    private var mTimeHandler: TimeHandler? = null
    private val mDelayHandler:Handler = Handler(Looper.getMainLooper())
    private var mProgressSeekBar: AppCompatSeekBar? = null
    private var mGestureListener:GestureDetectorCompat?=null
    private var mControllerViewIsShow:Boolean = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        println("onSizeChanged....$h......$oldh")
        mSurfaceHeight = (h * PERCENT_HEIGHT).toInt()
        println("onSizeChanged........$mSurfaceHeight")
        if (mSurfaceHeight < MIN_HEIGHT) {
            mSurfaceHeight = MIN_HEIGHT
        }
        resetSurfaceHeight()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        println("onConfigurationChanged...." + newConfig!!.toString())
    }

    private fun resetSurfaceHeight() {
        val params = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSurfaceHeight)
        params.gravity = Gravity.CENTER_VERTICAL
        mSurfaceView!!.layoutParams = params
    }

    fun setController(controller:IPlayerContract.IController){
        this.mController = controller
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
        loadControllerView()
        createAndAddSurfaceView(SurfaceView(context))
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
        val viewBinding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(context),
                R.layout.layout_player_control, this, true)
        controllerView = viewBinding.root
        mTitleLayout = controllerView!!.media_controller_top_layout
        mTitleTv = controllerView!!.media_controller_display_title_tv
        mBackIv = controllerView!!.media_controller_back_iv
        mBottomLayout = controllerView!!.media_controller_bottom_layout
        mPlayIv = controllerView!!.media_controller_play_iv
        mNextIv = controllerView!!.media_controller_skip_next_iv
        mPreviousIv = controllerView!!.media_controller_previous_iv
        mProgressSeekBar = controllerView!!.media_controller_progress_bar
        mBackIv!!.setOnClickListener(mClickListener)
        mTitleTv!!.setOnClickListener(mClickListener)
        mPlayIv!!.setOnClickListener(mClickListener)
        controllerView!!.setOnTouchListener { _, event ->
            if(mGestureListener == null){
                mGestureListener = GestureDetectorCompat(context,mGestureDetectorListener)
            }
            mGestureListener!!.onTouchEvent(event)
            true
        }
    }

    private val mGestureDetectorListener = object: GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            println("双击")
            return super.onDoubleTap(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            println("单击")
            if(mControllerViewIsShow)
                hideControllerView()
            else
                showControllerView()
            return super.onSingleTapConfirmed(e)
        }
    }

    private val mClickListener = View.OnClickListener {
        v ->
        when (v.id) {
            R.id.media_controller_back_iv -> {
                mController!!.stopPlayer()
            }
            R.id.media_controller_play_iv -> {
                mController!!.startOrPause()
            }
            R.id.media_controller_previous_iv -> {

            }
            R.id.media_controller_skip_next_iv -> {

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
        if(mController!!.duration > 0){
            mCurrentPosition = mController!!.currentPosition
            val progress = mCurrentPosition * 100L / mController!!.duration
            mProgressSeekBar!!.progress = progress.toInt()
        }

        val time = DateUtil.getInstance().getTime(mCurrentPosition)
        controllerView!!.media_controller_time_display_tv.text = time
    }


    fun playerStateChanged(state: Int) {
        when (state) {
            LController.PlayerState.STATE_PLAYING -> {
                mPlayIv!!.setImageResource(R.mipmap.ic_pause_media)
                if (mTimeHandler == null) {
                    mTimeHandler = TimeHandler(this)
                }
                mTimeHandler!!.sendEmptyMessageDelayed(0, 1000)
                hideControllerView()
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
            LController.PlayerState.STATE_STOP->{//播放完成停止
                mTimeHandler!!.removeCallbacksAndMessages(null)
                mPlayIv!!.setImageResource(R.mipmap.ic_play_media)
                mProgressSeekBar!!.progress = 0
                controllerView!!.media_controller_time_display_tv.text = resources.getString(R.string.empty_time)
            }
            else -> {
                println("stateChanged   other state=$state")
            }
        }
    }

    fun hideControllerView(){
        val topHideAnim = ObjectAnimator.ofInt(controllerView!!.media_controller_top_layout,
                "translationY",0,-controllerView!!.media_controller_top_layout.measuredHeight)
        topHideAnim.addUpdateListener {anim->
            val y = ""+anim.animatedValue
            controllerView!!.media_controller_top_layout.translationY = y.toFloat()
        }
        topHideAnim.duration = 500
        topHideAnim.start()

        val anim = ObjectAnimator.ofInt(controllerView!!.media_controller_bottom_layout,
                "translationY",0,controllerView!!.media_controller_bottom_layout.measuredHeight)
        anim.addUpdateListener({ animation ->
            val y = animation.animatedValue
            val s = "" + y
            println("translateY...." + y)
            controllerView!!.media_controller_bottom_layout.translationY = s.toFloat()
        })
        anim.addListener(object:Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mControllerViewIsShow = false
            }
        })
        anim.duration = 500
        anim.start()
    }

    fun showControllerView(){
        val topHideAnim = ObjectAnimator.ofInt(controllerView!!.media_controller_top_layout,
                "translationY",-controllerView!!.media_controller_top_layout.measuredHeight,0)
        topHideAnim.addUpdateListener {anim->
            val y = ""+anim.animatedValue
            controllerView!!.media_controller_top_layout.translationY = y.toFloat()
        }
        topHideAnim.duration = 500
        topHideAnim.start()

        val anim = ObjectAnimator.ofInt(controllerView!!.media_controller_bottom_layout,
                "translationY",controllerView!!.media_controller_bottom_layout.measuredHeight,0)
        anim.addUpdateListener({ animation ->
            val y = ""+animation.animatedValue
            controllerView!!.media_controller_bottom_layout.translationY = y.toFloat()
        })
        anim.addListener(object:Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                mControllerViewIsShow = true
                if(mController!!.playerState == LController.PlayerState.STATE_PLAYING){
                    mDelayHandler.postDelayed({
                        hideControllerView()
                    },1000)
            }
            }
        })
        anim.duration = 500
        anim.start()
    }




}