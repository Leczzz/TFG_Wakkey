package com.tema.wakkey

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission

class AlarmReceiver : BroadcastReceiver() {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val sonido = intent.getStringExtra("sonido") ?: "Crystal Waters"
        val idJuego = intent.getIntExtra("idJuego", 0)
        val dificultad = intent.getStringExtra("dificultad")

        // Log para depuración
        Log.d("AlarmReceiver", "Recibido: idJuego = $idJuego, dificultad = $dificultad, sonido = $sonido")

        // Determinar el recurso de sonido
        val recursoSonido = when (sonido) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning  // Valor por defecto si el sonido no está en la lista
        }

        // Reproducir el sonido de forma global con el singleton
        AlarmSoundPlayer.start(context, recursoSonido)

        // Determinar la actividad de destino según el juego
        val actividadDestino = when (idJuego) {
            1 -> DespiertaActivity::class.java
            2 -> DespeinaActivity::class.java
            3 -> RestaActivity::class.java
            4 -> SumaActivity::class.java
            else -> DetenerAlarmaActivity::class.java

        }

        // Lanzar la actividad en pantalla completa
        val fullScreenIntent = Intent(context, actividadDestino).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("dificultad", dificultad)
            putExtra("sonido", sonido)
        }

        context.startActivity(fullScreenIntent)
    }
}
