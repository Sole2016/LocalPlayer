package com.zy.vplayer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.zy.vplayer.adapter.AdapterItemTouchListener
import com.zy.vplayer.adapter.QueueAdapter
import com.zy.vplayer.entity.RecordEntity
import com.zy.vplayer.utils.LocalCache
import com.zy.vplayer.utils.ScanVideoFile
import kotlinx.android.synthetic.main.activity_queue_list.*

class QueueListActivity : AppCompatActivity() {
    private var mRecycler: RecyclerView? = null
    private var mAdapter: QueueAdapter? = null
    private var mLoadingDialog: AppCompatDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue_list)
        mRecycler = recycler_queue_list
        mRecycler!!.layoutManager = LinearLayoutManager(this)
        if (LocalCache.getInstance().hasLocalCache(applicationContext)) {
            val obj = LocalCache.getInstance().getLocalCache(applicationContext)
            if (obj != null && obj is ArrayList<*>) {
                val list = obj as ArrayList<RecordEntity>
                if(list.size == 0){
                    refreshLocal()
                    return
                }
//                list.forEach { s ->
//                    println(s.toString())
//                }
                applyData(list)
            }
        } else {
            refreshLocal()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_refresh,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.refresh_list){
            refreshLocal()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshLocal(){
        showLoading()
        ScanVideoFile.getInstance().scanVideoFile(this).setOnScanComplete(object : ScanVideoFile.OnScanComplete() {
            override fun onComplete(paths: ArrayList<RecordEntity>?) {
                hideLoading()
                LocalCache.getInstance().saveLocalCache(applicationContext, paths)
                applyData(paths!!)
            }
        })
    }

    private fun showLoading(){
        if(mLoadingDialog == null){
            mLoadingDialog = AppCompatDialog(this,R.style.TransDialog)
            mLoadingDialog!!.setContentView(R.layout.loading_layout)
            mLoadingDialog!!.setCanceledOnTouchOutside(false)
        }
        if(mLoadingDialog!!.isShowing){
            return
        }
        mLoadingDialog!!.show()
    }

    private fun hideLoading(){
        if(mLoadingDialog != null && mLoadingDialog!!.isShowing){
            mLoadingDialog!!.dismiss()
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

    private val itemTouchListener = AdapterItemTouchListener { _, position ->
        skipToPlay(mAdapter!!.queueList, position)
    }

    private fun skipToPlay(list: ArrayList<RecordEntity>, position: Int) {
        val it = Intent(this, PlayerActivity::class.java)
        val extra = Bundle()
        extra.putParcelableArrayList("QUEUE_LIST", list)
        extra.putInt("PLAY_POSITION", position)
        it.putExtras(extra)
        startActivityForResult(it, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val list = LocalCache.getInstance().getLocalCache(this)
        applyData(list as ArrayList<RecordEntity>)
    }

}
