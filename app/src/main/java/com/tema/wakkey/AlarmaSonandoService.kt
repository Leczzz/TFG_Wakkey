package com.tema.wakkey

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AlarmaSonandoService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "alarm_channel"
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarma")
            .setContentText("¡Despierta!")
            .setSmallIcon(R.drawable.icono)
            .build()

        startForeground(1, notification)

        val sonidoNombre = intent?.getStringExtra("sonido") ?: "Morning" // valor por defecto

        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }

        val mediaPlayer = MediaPlayer.create(this, sonidoResId)
        mediaPlayer.start()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

