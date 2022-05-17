package com.example.daemon.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.*
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.daemon.R
import com.example.daemon.dev.AbsWorkService
import com.example.daemon.receiver.AlarmReceiver
import com.example.daemon.ui.MainActivity
import com.example.daemon.utils.FileUtils
import java.io.IOException

class AlarmService : AbsWorkService() {

    companion object {
        const val EXTRA_NAME = "startForegroundService"
        //
        private const val TAG = "AlarmService"
        private const val CHANNEL_ID = "channel_id"
//        private const val PHONE_VIVO = "vivo"
        private const val WHAT_NOTIFICATION = 1
        private const val DELAY_NOTIFICATION = 30000L
        private var sIsRunning = false
    }

    private var mPlayer: MediaPlayer? = null
    private val mBinder: MainBinder = MainBinder()
    private lateinit var mHandler: AlarmHandler
    private lateinit var mReceiver: AlarmReceiver

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        // val fileName = "di.ogg"
        val fileName = "media.m4a"
        FileUtils.writeFileToSDCard(this, assets.open(fileName))
        // handler
        this.mHandler = AlarmHandler(Looper.getMainLooper())
        // receiver
        val receiver = AlarmReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, filter)
        this.mReceiver = receiver
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val value = intent.getBooleanExtra("startForegroundService", true)
        if (value) {
            startPlayer()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val manager = getSystemService(Context.POWER_SERVICE) as PowerManager
            Log.i(TAG, "manager.isInteractive = ${manager.isInteractive}")
            if (manager.isInteractive) {
                sendNotification(this)
                stopForeground(true)
                //
                stopPlayer()
            } else {
                startPlayer()
            }
        }
        Log.i(TAG, "onStartCommand")
        return super.onStartCommand(intent, START_FLAG_RETRY, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
        unregisterReceiver(mReceiver)
    }

    override fun onBind(intent: Intent?, alwaysNull: Void?): IBinder {
        return mBinder
    }

    override fun shouldStopService(intent: Intent?, flags: Int, startId: Int): Boolean {
        Log.i(TAG, "shouldStopService")
        // return sIsRunning
        return false
    }

    override fun startWork(intent: Intent?, flags: Int, startId: Int) {
        Log.i(TAG, "startWork")
        sIsRunning = true
    }

    override fun stopWork(intent: Intent?, flags: Int, startId: Int) {
        Log.i(TAG, "stopWork")
        sIsRunning = false
        // cancelJobAlarmSub()
    }

    override fun isWorkRunning(intent: Intent?, flags: Int, startId: Int): Boolean {
        Log.i(TAG, "isWorkRunning")
        return sIsRunning
    }

    override fun onServiceKilled(rootIntent: Intent?) {
        Log.i(TAG, "onServiceKilled")
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
            .setContentTitle(getString(R.string.app_name))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()
        startForeground(1, notification)
    }

    private fun startPlayer() {
        // 播放音乐
        if (mPlayer == null) {
            mPlayer = MediaPlayer()
        }
        mPlayer?.let {
            it.reset()
//            val brand = Build.BRAND
//            if (TextUtils.equals(PHONE_VIVO, brand)) {
//                it.isLooping = true
//            }
            it.setDataSource(FileUtils.getPath(this))
            it.setOnPreparedListener { player ->
                player.start()
            }
            try {
                if (it.isPlaying) {
                    it.stop()
                }
                it.prepareAsync()
            } catch (e: IllegalStateException) {
                Log.e(TAG, "e = ${e.message}")
            } catch (e: IOException) {
                Log.e(TAG, "e = ${e.message}")
            }
        }
    }

    private fun stopPlayer() {
        mPlayer?.stop()
    }

    inner class MainBinder : Binder() {
        fun refresh() {}
    }

    inner class AlarmHandler(looper: Looper) : Handler(looper) {

        init {
            sendHandler(delayMillis = 0)
        }

        private fun sendHandler(delayMillis: Long = DELAY_NOTIFICATION) {
            val msg = Message.obtain()
            msg.what = WHAT_NOTIFICATION
            sendMessageDelayed(msg, delayMillis)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.i(TAG, "handleMessage")
            startPlayer()
            //
            sendHandler()
        }
    }
}