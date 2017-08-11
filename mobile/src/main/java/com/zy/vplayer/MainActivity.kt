package com.zy.vplayer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zy.vplayer.contract.IPlayerContract
import com.zy.vplayer.widget.LController
import com.zy.vplayer.widget.LVideoView

class MainActivity : AppCompatActivity(),IPlayerContract.IView {
    val path = "/storage/emulated/0/Download/Alone.mp4"
    private var mVideoView:LVideoView?=null
    private var mVideoController:IPlayerContract.IController?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        mVideoController = LController(applicationContext,this)
        mVideoView = findViewById(R.id.simple_video_view) as LVideoView
        mVideoView!!.setController(mVideoController!!)
        mVideoView!!.setMediaPath(path)
    }

    override fun onPlayerStateChanged(state: Int) {
        mVideoView!!.playerStateChanged(state)
    }
}
