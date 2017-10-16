package com.zy.vplayer

import android.content.Context
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.zy.vplayer.adapter.AdapterItemTouchListener
import com.zy.vplayer.adapter.QueueAdapter
import com.zy.vplayer.contract.IPlayerContract
import com.zy.vplayer.entity.RecordEntity
import com.zy.vplayer.utils.LocalCache
import com.zy.vplayer.widget.LController
import com.zy.vplayer.widget.LVideoView
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity(), IPlayerContract.IModel {
    private val ACTION_LIST = "QUEUE_LIST"
    private val ACTION_POSITION = "PLAY_POSITION"

    private var mVideoView: LVideoView? = null
    private var mVideoController: IPlayerContract.IPlayerController? = null
    private var mAudioManager: AudioManager? = null
    private var entityList: ArrayList<RecordEntity>? = null
    private var mRecyclerList: RecyclerView? = null
    private var mQueueAdapter: QueueAdapter? = null
    private var mMediaPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_player)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        entityList = intent.extras.getParcelableArrayList<RecordEntity>(ACTION_LIST)
        if (entityList == null) {
            finish()
            return
        }
        mMediaPosition = intent.getIntExtra(ACTION_POSITION, 0)

        if (mMediaPosition >= entityList!!.size) {
            println("playPosition is " + mMediaPosition + ",but list size is " + entityList!!.size)
            finish()
            return
        }
        mRecyclerList = queue_list_recycler
        mRecyclerList!!.layoutManager = LinearLayoutManager(this)

        mVideoView = simple_video_view
        mVideoController = LController(applicationContext, mVideoView!!)
        mVideoView!!.setController(mVideoController!!)
        mVideoView!!.setModel(this)
        mVideoView!!.setPlayMedia(entityList!![mMediaPosition])
    }

    override fun setWindowBrightness(brightness: Float) {
        val lp = window.attributes
        lp.screenBrightness = Math.max(0f, Math.min(brightness, 255f)) / 255.0f
        lp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = lp
    }

    override fun getWindowBrightness(): Float = window.attributes.screenBrightness

    override fun keepScreenOn(flag: Boolean) {
        val lp = window.attributes
        if (flag) {
            lp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        } else {
            lp.flags = 0
        }
        window.attributes = lp
    }

    override fun changeVolume(flag: Int) {
        if (mAudioManager == null) {
            mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        }
        when (flag) {
            0 -> {//降低
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                        AudioManager.FX_FOCUS_NAVIGATION_UP)
            }
            1 -> {//增加
                mAudioManager!!.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mRecyclerList?.visibility == View.VISIBLE) {
                    val area = Rect()
                    mRecyclerList?.getGlobalVisibleRect(area)
                    if (!area.contains(ev.x.toInt(), ev.y.toInt())) {
                        hideMediaList()
                        return super.dispatchTouchEvent(ev)
                    }
                } else {
                    return super.dispatchTouchEvent(ev)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun showMediaList() {
        if (mQueueAdapter == null) {
            mQueueAdapter = QueueAdapter(entityList)
            mQueueAdapter!!.setTouchListener(itemTouchListener)
            mRecyclerList!!.adapter = mQueueAdapter
        }
        mQueueAdapter!!.setPlayingPosition(mMediaPosition)
        mRecyclerList!!.visibility = View.VISIBLE
    }

    override fun hideMediaList() {
        mRecyclerList!!.visibility = View.GONE
    }

    override fun setRecord(position: Int, recordEntity: RecordEntity?) {
        entityList!![position] = recordEntity!!
        saveRecord()
    }

    override fun saveRecord() {
        LocalCache.getInstance().saveLocalCache(this, entityList)
    }

    override fun getCurrentMediaPosition(): Int = mMediaPosition

    override fun getNextMedia(): RecordEntity {
        if (mMediaPosition + 1 == entityList!!.size) {
            mMediaPosition = 0
        } else {
            mMediaPosition++
        }
        return entityList!![mMediaPosition]
    }

    override fun getPreviousMedia(): RecordEntity {
        if (mMediaPosition - 1 < 0) {
            mMediaPosition = entityList!!.size - 1
        } else {
            mMediaPosition--
        }
        return entityList!![mMediaPosition]
    }

    private val itemTouchListener = AdapterItemTouchListener { _, position ->
        mMediaPosition = position
        mVideoView!!.setMediaPath(mQueueAdapter!!.getItem(position).path)
        mQueueAdapter!!.setPlayingPosition(mMediaPosition)
        hideMediaList()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean =
            mVideoView!!.onKeyDown(keyCode, event)

    override fun destroy() {
        setRecord(currentMediaPosition, mVideoView!!.getEntity())
        setResult(0)
        finish()
    }

}
