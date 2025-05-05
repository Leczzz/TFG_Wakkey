package com.tema.wakkey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sonido = intent.getStringExtra("sonido") ?: "Crystal Waters"
        val idJuego = intent.getIntExtra("idJuego", 0)
        val dificultad = intent.getCharExtra("dificultad", 'F')

        Log.d("AlarmReceiver", "Recibido: idJuego = $idJuego, dificultad = $dificultad")

        val juegoIntent = when (idJuego) {
            4 -> Intent(context, SumaActivity::class.java) // "¡Suma!" juego
            else -> Intent(context, DetenerAlarmaActivity::class.java) // Default activity
        }

        juegoIntent.putExtra("dificultad", dificultad)
        juegoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // Aquí agregamos el flag necesario

        context.startActivity(juegoIntent)
    }
}
