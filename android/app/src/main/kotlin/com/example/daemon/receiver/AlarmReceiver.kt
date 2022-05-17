package com.example.daemon.receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.daemon.ui.MainActivity
import com.example.daemon.R
import com.example.daemon.service.AlarmService
import com.example.daemon.watcher.RefWatcher
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"
        private const val CHANNEL_ID = "id"
        private const val RECEIVER_ACTION_NOTIFY = "receiver_action_notify"
    }

    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive -> action = ${intent.action}")
        when (intent.action) {
            RECEIVER_ACTION_NOTIFY -> {
                sendNotification(context)
            }
            Intent.ACTION_SCREEN_ON -> {
                startForegroundService(context, false)
                // 屏幕开启，关闭透明Activity
                RefWatcher.finishKeepLiveActivity()
                Log.i(TAG, "屏幕点亮")
            }
            Intent.ACTION_SCREEN_OFF -> {
                startForegroundService(context, true)
                // 屏幕关闭，打开透明Activity
                RefWatcher.startKeepLiveActivity(context)
                Log.i(TAG, "屏幕熄灭")
            }
            else -> {}
        }
    }

    private fun sendNotification(context: Context) {
        // 获取通知管理器
        val name = Context.NOTIFICATION_SERVICE
        val manager = context.getSystemService(name) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, TAG, importance)
            manager.createNotificationChannel(channel)
        }
        //
        val intent = Intent(context, MainActivity::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }
        val pending = PendingIntent.getActivity(context, 0, intent, flags)
        // 创建通知
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(getContentText())
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()
        manager.notify(100, notification)
    }

    private fun getContentText(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    private fun startForegroundService(context: Context, value: Boolean) {
        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(AlarmService.EXTRA_NAME, value)
        ContextCompat.startForegroundService(context, intent)
    }
}