package com.tema.wakkey

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresPermission
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "Wakkey:AlarmWakeLock"
        )
        wl.acquire(5000)

        val alarmaId = intent.getIntExtra("idAlarma", -1)

        // Si no se envió el ID, no hacer nada
        if (alarmaId == -1) {
            Log.e("AlarmReceiver", "ID de alarma no recibido. Cancelando ejecución.")
            return
        }

        val db = AppDatabase.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            val alarma = db.alarmDao().obtenerAlarmaPorId(alarmaId)

            // Si no existe o está desactivada, no continuar
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

            // Reproducir el sonido de forma global con el singleton
            AlarmSoundPlayer.start(context, recursoSonido)

            // Determinar la actividad de destino según el juego
            val actividadDestino = when (idJuego) {
                1 -> DespiertaActivity::class.java
                2 -> DespeinaActivity::class.java
                3 -> RestaActivity::class.java
                4 -> SumaActivity::class.java
                5 -> ScanActivity::class.java
                else -> DetenerAlarmaActivity::class.java
            }

            val fullScreenIntent = Intent(context, actividadDestino).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("dificultad", dificultad)
                putExtra("sonido", sonido)
            }

            context.startActivity(fullScreenIntent)
        }
    }
}
