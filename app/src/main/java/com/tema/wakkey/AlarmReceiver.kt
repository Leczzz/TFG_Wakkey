package com.tema.wakkey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sonido = intent.getStringExtra("sonido") ?: "Crystal Waters" // Obtener el sonido
        val idJuego = intent.getIntExtra("idJuego", 0)
        val dificultad = intent.getStringExtra("dificultad")

        Log.d("AlarmReceiver", "Recibido: idJuego = $idJuego, dificultad = $dificultad, sonido = $sonido")

        // Crear el intent adecuado para el juego
        val juegoIntent = when (idJuego) {
            3 -> Intent(context, RestaActivity::class.java) // Actividad para el juego "Suma"

            4 -> Intent(context, SumaActivity::class.java) // Actividad para el juego "Suma"

            else -> Intent(context, DetenerAlarmaActivity::class.java) // Actividad por defecto
        }

        // Pasar los extras de dificultad y sonido al intent
        juegoIntent.putExtra("dificultad", dificultad)
        juegoIntent.putExtra("sonido", sonido)
        juegoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Necesario para iniciar la actividad desde un BroadcastReceiver

        // Iniciar la actividad
        context.startActivity(juegoIntent)
    }
}
