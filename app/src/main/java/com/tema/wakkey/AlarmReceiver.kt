package com.tema.wakkey

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "wakkey_alarm_channel"
        const val NOTIF_ID_BASE = 1000
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "Wakkey:AlarmWakeLock"
        )
        wl.acquire(10 * 60 * 1000L) // 10 minutos

        try {
            val alarmaId = intent.getIntExtra("idAlarma", -1)
            if (alarmaId == -1) {
                Log.e("AlarmReceiver", "ID de alarma no recibido. Cancelando ejecución.")
                return
            }

            val db = AppDatabase.getInstance(context)
            CoroutineScope(Dispatchers.IO).launch {
                val alarma = db.alarmDao().obtenerAlarmaPorId(alarmaId)

                if (alarma == null || !alarma.esActivo) {
                    Log.d("AlarmReceiver", "Alarma no activa o no encontrada. No se reproduce.")
                    return@launch
                }

                val sonido = intent.getStringExtra("sonido") ?: "Crystal Waters"
                val idJuego = alarma.idJuego
                val dificultad = alarma.dificultad

                Log.d("AlarmReceiver", "Activando alarma: idJuego = $idJuego, dificultad = $dificultad, sonido = $sonido")

                val recursoSonido = when (sonido) {
                    "Crystal Waters" -> R.raw.crystalwaters
                    "Hawaii" -> R.raw.hawai
                    "Lofi" -> R.raw.lofi
                    "Morning" -> R.raw.morning
                    "Piano" -> R.raw.piano
                    else -> R.raw.morning
                }

                withContext(Dispatchers.Main) {
                    AlarmSoundPlayer.start(context, recursoSonido)
                }

                val actividadDestino = when (idJuego) {
                    1 -> DespiertaActivity::class.java
                    2 -> DespeinaActivity::class.java
                    3 -> RestaActivity::class.java
                    4 -> SumaActivity::class.java
                    5 -> ScanActivity::class.java
                    else -> DetenerAlarmaActivity::class.java
                }

                val fullScreenIntent = Intent(context, actividadDestino).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("idAlarma", alarmaId)
                    putExtra("dificultad", dificultad)
                    putExtra("sonido", sonido)
                }

                val fullScreenPendingIntent = PendingIntent.getActivity(
                    context,
                    alarmaId,
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Alarmas Wakkey",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal para alarmas Wakkey"
                    setSound(null, null)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)

                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.img)
                    .setContentTitle("Alarma Wakkey")
                    .setContentText("¡Es hora de despertar!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setAutoCancel(true)
                    .build()

                withContext(Dispatchers.Main) {
                    notificationManager.notify(NOTIF_ID_BASE + alarmaId, notification)
                }
            }
        } finally {
            if (wl.isHeld) wl.release()
        }
    }
}
