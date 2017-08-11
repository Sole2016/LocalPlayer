package com.zy.vplayer.widget

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.zy.vplayer.R
import kotlinx.android.synthetic.main.layout_player_control.view.*

class LVideoView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    val PERCENT_HEIGHT = 1/3
    val MIN_HEIGHT = 300
    private var mVideoUri: Uri? = null
    private var mSurfaceView: SurfaceView? = null
    private var controllerView: View? = null
    private var mTitleLayout: ViewGroup? = null
    private var mBottomLayout: ViewGroup? = null
    private var mTitleTv: TextView?=null
    private var mBackIv: ImageView?= null
    private var mController:LController?=null
    private var mSurfaceHeight:Int = 0
    private var mPlayIv:ImageView?=null
    private var mNextIv:ImageView?=null
    private var mPreviousIv:ImageView?=null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mSurfaceHeight = h * PERCENT_HEIGHT
        if(h < MIN_HEIGHT){
            resetSurfaceHeight()
            return
        }
        if(mSurfaceHeight < MIN_HEIGHT){
            mSurfaceHeight = MIN_HEIGHT
        }
        resetSurfaceHeight()
    }

    private fun resetSurfaceHeight(){
        val params = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSurfaceHeight)
        params.gravity = Gravity.CENTER_VERTICAL
        mSurfaceView!!.layoutParams = params
    }

    fun setMediaPath(path:String){
        setMediaUri(Uri.parse(path))
        getTitle(path)
    }

    private fun getTitle(path: String){
        val name = path.substring(path.lastIndexOf('/') + 1)
        setTitle(name)
    }

    fun setMediaUri(uri:Uri){
        this.mVideoUri = uri
        initPlayer()
        mController!!.start()
    }

    fun setTitle(title:String){
        if(mTitleTv != null){
            mTitleTv!!.text = title
        }
    }

    private fun loadControllerView(){
        val viewBinding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(context),
                R.layout.layout_player_control,this,true)
        controllerView = viewBinding.root
        mTitleLayout = controllerView!!.media_controller_top_layout
        mTitleTv = controllerView!!.media_controller_display_title_tv
        mBackIv = controllerView!!.media_controller_back_iv
        mBottomLayout = controllerView!!.media_controller_bottom_layout
        mPlayIv = controllerView!!.media_controller_play_iv
        mNextIv = controllerView!!.media_controller_skip_next_iv
        mPreviousIv = controllerView!!.media_controller_previous_iv
    }

    private fun initPlayer() {
        loadControllerView()
        createAndAddSurfaceView(SurfaceView(context))
        mController = LController(context,this)
        mController!!.setSurface(mSurfaceView!!)
        mController!!.setVideoUri(mVideoUri!!)
    }

    private fun createAndAddSurfaceView(surfaceView: SurfaceView) {
        this.mSurfaceView = surfaceView
        val params = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSurfaceHeight)
        params.gravity = Gravity.CENTER_VERTICAL
        addView(mSurfaceView, params)
    }


}