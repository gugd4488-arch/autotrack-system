package com.autotrack.client

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.work.*
import java.util.concurrent.TimeUnit

class AutoTrackService : Service() {
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setupWorkers()
        return START_STICKY
    }
    
    private fun setupWorkers() {
        // GPS位置上报 - 每30秒
        val gpsWork = PeriodicWorkRequestBuilder<GpsWorker>(30, TimeUnit.SECONDS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "gps_upload", ExistingPeriodicWorkPolicy.KEEP, gpsWork
        )
        
        // 脚本检查 - 每1分钟
        val scriptWork = PeriodicWorkRequestBuilder<ScriptWorker>(1, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "script_check", ExistingPeriodicWorkPolicy.KEEP, scriptWork
        )
    }
    
    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, AutoTrackService::class.java))
        }
    }
}
