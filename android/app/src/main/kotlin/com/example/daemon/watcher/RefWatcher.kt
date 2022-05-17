package com.example.daemon.watcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.daemon.ui.KeepLiveActivity
import java.lang.ref.WeakReference

object RefWatcher {

    private var mReference: WeakReference<Activity>? = null

    fun setKeepLiveActivity(activity: Activity) {
        mReference = WeakReference(activity)
    }

    fun startKeepLiveActivity(context: Context) {
        val intent = Intent(context, KeepLiveActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun finishKeepLiveActivity() {
        val activity: Activity? = mReference?.get()
        activity?.finish()
    }
}