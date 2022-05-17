package com.example.daemon.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import com.example.daemon.watcher.RefWatcher

class KeepLiveActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "KeepLiveActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "KeepLiveActivity -> onCreate")
        //
        RefWatcher.setKeepLiveActivity(this)
        // 左下角显示
        val window = window
        window.setGravity(Gravity.LEFT or Gravity.BOTTOM)
        // 设置为1像素大小
        val params = window.attributes
        params.x = 0
        params.y = 0
        params.width = 1
        params.height = 1
        window.attributes = params
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "KeepLiveActivity -> onDestroy")
    }
}