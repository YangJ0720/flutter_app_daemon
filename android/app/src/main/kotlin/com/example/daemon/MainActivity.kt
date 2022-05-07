package com.example.daemon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.example.daemon.service.AlarmService
import io.flutter.embedding.android.FlutterActivity

class MainActivity: FlutterActivity() {

    private var mBinder: AlarmService.MainBinder? = null
    private val mConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mBinder = binder as AlarmService.MainBinder?
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initService()
    }

    override fun onDestroy() {
        unbindService(mConn)
        super.onDestroy()
    }

    private fun initData() {
        ignoreBatteryOptimizations()
    }

    private fun initService() {
        val intent = Intent(this, AlarmService::class.java)
        bindService(intent, mConn, Context.BIND_AUTO_CREATE)
    }

    private fun ignoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
}
