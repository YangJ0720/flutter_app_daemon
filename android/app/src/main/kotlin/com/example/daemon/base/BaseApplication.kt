package com.example.daemon.base

import android.app.Application
import android.util.Log
import com.example.daemon.dev.DaemonEnv
import com.example.daemon.service.AlarmService

class BaseApplication : Application() {
    companion object {
        private const val TAG = "BaseApplication"
    }

    override fun onCreate() {
        super.onCreate()
        val cls = AlarmService::class.java
        DaemonEnv.initialize(this, cls, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL)
        DaemonEnv.startServiceMayBind(cls)
        Log.i(TAG, "---------------------------------------------------------------")
        Log.i(TAG, "BaseApplication")
        Log.i(TAG, "---------------------------------------------------------------")
    }
}