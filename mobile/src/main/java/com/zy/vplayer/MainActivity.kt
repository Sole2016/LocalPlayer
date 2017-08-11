package com.zy.vplayer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zy.vplayer.widget.LVideoView

class MainActivity : AppCompatActivity() {
    val path = "/storage/emulated/0/Download/Alone.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videoView = findViewById<LVideoView>(R.id.simple_video_view)
        videoView.setMediaPath(path)
    }
}
