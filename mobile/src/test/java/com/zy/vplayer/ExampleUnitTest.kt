package com.zy.vplayer

import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
        val simple = File("C:\\Users\\ZhiTouPC\\Downloads\\Alone.mp4")
        if(simple.exists()){
            println("size="+simple.length())
        }
    }
}
