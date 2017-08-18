package com.zy.vplayer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.zy.vplayer.adapter.AdapterItemTouchListener
import com.zy.vplayer.adapter.QueueAdapter
import com.zy.vplayer.entity.RecordEntity
import com.zy.vplayer.utils.LocalCache
import com.zy.vplayer.utils.ScanVideoFile
import kotlinx.android.synthetic.main.activity_queue_list.*
import kotlin.collections.ArrayList

class QueueListActivity : AppCompatActivity() {
    private var mRecycler: RecyclerView? = null
    private var mAdapter: QueueAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_list)
        mRecycler = recycler_queue_list
        mRecycler!!.layoutManager = GridLayoutManager(this, 2)
        if (LocalCache.getInstance().hasLocalCache(applicationContext)) {
            val obj = LocalCache.getInstance().getLocalCache(applicationContext)
            if (obj != null && obj is List<*>) {
                println("local....." + obj.toString())
                val list = obj as ArrayList<RecordEntity>
                list.forEach {
                    s ->
                    println(s.toString())
                }
                applyData(list)
            }
        } else {
            ScanVideoFile.getInstance().scanVideoFile(this).setOnScanComplete(object : ScanVideoFile.OnScanComplete() {
                override fun onComplete(paths: ArrayList<String>?) {
                    println("scan....." + paths?.toString())
                    val list = ArrayList<RecordEntity>()
                    paths!!.forEach {
                        s ->
                        list.add(RecordEntity(s, 0, 0))
                    }
                    LocalCache.getInstance().saveLocalCache(applicationContext, list)
                    applyData(list)
                }
            })
        }
    }

    fun applyData(list: ArrayList<RecordEntity>) {
        if (mAdapter == null) {
            mAdapter = QueueAdapter(list)
            mAdapter!!.setTouchListener(itemTouchListener)
            mRecycler!!.adapter = mAdapter
        } else {
            mAdapter!!.queueList = list
        }
    }

    val itemTouchListener = AdapterItemTouchListener { _, position ->
        println("list......"+mAdapter!!.queueList)
        skipToPlay(mAdapter!!.queueList,position)
    }

    fun skipToPlay(list: ArrayList<RecordEntity>, posi: Int) {
        val it = Intent(this, PlayerActivity::class.java)
        val extra = Bundle()
        extra.putParcelableArrayList("QUEUE_LIST",list)
        extra.putInt("PLAY_POSITION",posi)
        it.putExtras(extra)
        startActivity(it)
    }

}
