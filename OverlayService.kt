package com.odinaev.gamebooster

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Choreographer
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var fpsView: TextView
    private var frameCount = 0
    private var lastTimeNs = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        fpsView = TextView(this).apply {
            text = "FPS: --"
            setTextColor(Color.parseColor("#FF8A00"))
            textSize = 14f
            setBackgroundColor(Color.parseColor("#AA000000"))
            setPadding(20, 10, 20, 10)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 100
        }

        windowManager.addView(fpsView, params)
        startFrameCounter()
    }

    private fun startFrameCounter() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (lastTimeNs == 0L) lastTimeNs = frameTimeNanos
                frameCount++
                val elapsedMs = (frameTimeNanos - lastTimeNs) / 1_000_000
                if (elapsedMs >= 1000) {
                    fpsView.text = "FPS: $frameCount"
                    frameCount = 0
                    lastTimeNs = frameTimeNanos
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(fpsView)
    }
}
