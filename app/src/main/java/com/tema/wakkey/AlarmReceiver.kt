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

//Esta clase es un BroadcastReceiver que se
// activa cuando suena una alarma programada.
// Su función principal es despertar al usuario mostrando una notificación
// y lanzando una actividad con el juego para desactivar la alarma.
class AlarmReceiver : BroadcastReceiver() {


    companion object {
        const val CHANNEL_ID = "wakkey_alarm_channel" // Canal de notificación
        const val NOTIF_ID_BASE = 1000 // ID base para las notificaciones de alarma
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS) // Necesita permiso de notificación
    override fun onReceive(context: Context, intent: Intent) { // Maneja la recepción del intent
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager // Servicio de PowerManager

        // WakeLock para mantener la pantalla encendida y despertar dispositivo
        val wl = pm.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or // Bloquea la pantalla
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or // Despierta el dispositivo
                    PowerManager.ON_AFTER_RELEASE, // Libera el WakeLock después de despertar
            "Wakkey:AlarmWakeLock" // Etiqueta para el WakeLock
        )
        wl.acquire(10 * 60 * 1000L) // 10 minutos

        try { // Intenta ejecutar el código
            val alarmaId = intent.getIntExtra("idAlarma", -1) // Obtiene el ID de la alarma del intent
            if (alarmaId == -1) { // Si el ID no está presente, cancela la ejecución
                Log.e("AlarmReceiver", "ID de alarma no recibido. Cancelando ejecución.")
                wl.release()
                return
            }

            val db = AppDatabase.getInstance(context) // Obtiene la instancia de la base de datos
            CoroutineScope(Dispatchers.IO).launch { // Ejecuta en un hilo de fondo
                val alarma = db.alarmDao().obtenerAlarmaPorId(alarmaId) // Obtiene la alarma de la base de datos

                if (alarma == null || !alarma.esActivo) { // Si la alarma no está activa, cancela la ejecución
                    Log.d("AlarmReceiver", "Alarma no activa o no encontrada. No se reproduce.")
                    withContext(Dispatchers.Main) {
                        if (wl.isHeld) wl.release()
                    }
                    return@launch
                }

                val sonido = intent.getStringExtra("sonido") ?: "Crystal Waters" // Obtiene el sonido de la alarma del intent
                val idJuego = alarma.idJuego // Obtiene el ID del juego de la alarma
                val dificultad = alarma.dificultad // Obtiene la dificultad de la alarma

                Log.d("AlarmReceiver", "Activando alarma: idJuego = $idJuego, dificultad = $dificultad, sonido = $sonido")

                val recursoSonido = when (sonido) { // Obtiene el recurso de sonido correspondiente
                    "Crystal Waters" -> R.raw.crystalwaters
                    "Hawaii" -> R.raw.hawai
                    "Lofi" -> R.raw.lofi
                    "Morning" -> R.raw.morning
                    "Piano" -> R.raw.piano
                    else -> R.raw.morning
                }

                withContext(Dispatchers.Main) { // Ejecuta en el hilo principal
                    AlarmSoundPlayer.start(context, recursoSonido) // Reproduce el sonido de la alarma
                }

                val actividadDestino = when (idJuego) { // Obtiene la actividad correspondiente al juego
                    1 -> DespiertaActivity::class.java
                    2 -> DespeinaActivity::class.java
                    3 -> RestaActivity::class.java
                    4 -> SumaActivity::class.java
                    5 -> ScanActivity::class.java
                    else -> DetenerAlarmaActivity::class.java
                }

                val fullScreenIntent = Intent(context, actividadDestino).apply { // Crea el intent para la actividad
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or // Crea una nueva tarea
                            Intent.FLAG_ACTIVITY_CLEAR_TOP // Limpia la actividad actual
                    putExtra("idAlarma", alarmaId) // Pasa el ID de la alarma
                    putExtra("dificultad", dificultad) // Pasa la dificultad de la alarma
                    putExtra("sonido", sonido) // Pasa el sonido de la alarma
                }

                val fullScreenPendingIntent = PendingIntent.getActivity( // Crea el PendingIntent para la actividad
                    context,
                    alarmaId,
                    fullScreenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Actualiza la actividad existente
                )

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager // Servicio de notificación
                val channel = NotificationChannel( // Crea el canal de notificación
                    CHANNEL_ID, // ID del canal
                    "Alarmas Wakkey", // Nombre del canal
                    NotificationManager.IMPORTANCE_HIGH // Importancia
                ).apply {
                    description = "Canal para alarmas Wakkey"
                    setSound(null, null)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC // Visibilidad en pantalla completa
                }
                notificationManager.createNotificationChannel(channel) // Crea el canal de notificación

                val notification = NotificationCompat.Builder(context, CHANNEL_ID) // Crea la notificación
                    .setSmallIcon(R.drawable.img)
                    .setContentTitle("Alarma Wakkey")
                    .setContentText("¡Es hora de despertar!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setFullScreenIntent(fullScreenPendingIntent, true)
                    .setAutoCancel(true)
                    .build()

                withContext(Dispatchers.Main) { // Ejecuta en el hilo principal
                    notificationManager.notify(NOTIF_ID_BASE + alarmaId, notification) // Muestra la notificación

                    // Liberamos WakeLock después de notificar y lanzar la actividad
                    if (wl.isHeld) wl.release()
                }
            }
        } catch (e: Exception) { // Maneja cualquier error que pueda ocurrir
            Log.e("AlarmReceiver", "Error en AlarmReceiver: ${e.message}", e)
            if (wl.isHeld) wl.release()
        }
    }
}
