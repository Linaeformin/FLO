package com.example.flo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.flo.Constant.CHANNEL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.thread

class ForegroundService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null // 사용하지 않음을 의미한다.
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
            showNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 버전 확인
            createNotificationChannel()
        }

        return START_STICKY
    }

    private fun showNotification() {
        // 알림을 클릭했을 때 이동할 화면을 지정한다.
        val notificationIntent = Intent(this, SongActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat
            .Builder(this, Constant.CHANNEL_ID)
            .setContentText("현재 음악이 재생 중입니다.")
            .setSmallIcon(R.drawable.img_album_exp2)
            .setContentIntent(pendingIntent)
//            .setProgress(10, progress, false)
//            .build()
//        startForeground(Constant.MUSIC_NOTIFICATION_ID, notification)

        var i = 0
        CoroutineScope(Dispatchers.Main).launch {
            repeat(1000) { i ->
                notification.setProgress(100, i, false)
                startForeground(Constant.MUSIC_NOTIFICATION_ID, notification.build())
                delay(1000)
            }
        }
    }
    }