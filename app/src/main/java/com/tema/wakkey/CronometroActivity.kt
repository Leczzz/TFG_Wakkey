package com.tema.wakkey

import android.os.Handler
import android.os.Looper

class CronometroActivity ( private val onTick: (String) -> Unit) {

        private var startTime = 0L
        private var accumulatedTime = 0L
        private var isRunning = false

        private val handler = Handler(Looper.getMainLooper())
        private val updateRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    val time = System.currentTimeMillis() - startTime + accumulatedTime
                    onTick(formatTime(time))
                    handler.postDelayed(this, 1000)
                }
            }
        }

        fun start() {
            if (!isRunning) {
                startTime = System.currentTimeMillis()
                isRunning = true
                handler.post(updateRunnable)
            }
        }

        fun pause() {
            if (isRunning) {
                accumulatedTime += System.currentTimeMillis() - startTime
                isRunning = false
                handler.removeCallbacks(updateRunnable)
            }
        }

        fun resume() {
            if (!isRunning) {
                startTime = System.currentTimeMillis()
                isRunning = true
                handler.post(updateRunnable)
            }
        }

        fun reset() {
            isRunning = false
            startTime = 0
            accumulatedTime = 0
            handler.removeCallbacks(updateRunnable)
            onTick("00:00:00")
        }

        fun getElapsedTime(): Long {
            return if (isRunning)
                System.currentTimeMillis() - startTime + accumulatedTime
            else
                accumulatedTime
        }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60


        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    }

