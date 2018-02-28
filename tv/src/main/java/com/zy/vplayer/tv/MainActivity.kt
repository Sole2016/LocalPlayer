/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.zy.vplayer.tv

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import com.zy.vplayer.tv.simple.FocusBorderView
import com.zy.vplayer.tv.simple.SimpleMainFragment
import com.zy.vplayer.tv.struct.BaseFunctionWithParamAndResult
import com.zy.vplayer.tv.struct.FunctionManager

/**
 * Loads [MainFragment].
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunctionManager.getInstance().addFunction(object : BaseFunctionWithParamAndResult
        <Int, Int>(SimpleMainFragment.INTERFACE) {
            override fun function(param: Int?): Int {
                Toast.makeText(this@MainActivity, "param=" + param, Toast.LENGTH_SHORT).show()
                return 1
            }
        })
        setContentView(R.layout.activity_main)
//        val manager = fragmentManager
//        val beginTransaction = manager.beginTransaction()
//        beginTransaction.replace(R.id.main_browse_fragment,SimpleMainFragment())
//        beginTransaction.commitAllowingStateLoss()


        if (actionBar != null) {
            actionBar.hide()
        }

//        window.decorView.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
//            addBorderView(window.decorView as ViewGroup, oldFocus,newFocus)
//            println("oldFocus=" + oldFocus.javaClass.simpleName + ",tag=" + oldFocus.tag)
//            println("newFocus=" + newFocus.javaClass.simpleName + ",tag=" + newFocus.tag)
//        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun addBorderView(main: ViewGroup,old:View, newFocus: View) {
        main.postDelayed({
            if (view == null) {
                view = FocusBorderView(this)
                view!!.setInit(newFocus)
                main.addView(view)
            } else {
                view!!.updateView(old, newFocus)
            }
        },20)
    }

    var view: FocusBorderView? = null

}
