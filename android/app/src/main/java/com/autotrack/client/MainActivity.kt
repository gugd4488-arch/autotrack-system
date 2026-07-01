package com.autotrack.client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val textView = TextView(this).apply {
            text = "AutoTrack Client\n\n自动化脚本 + GPS追踪\n\n后台服务已启动"
            textSize = 18f
            setPadding(50, 50, 50, 50)
        }
        
        setContentView(textView)
        
        // Start services
        AutoTrackService.start(this)
    }
}
