package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.resta.GeneradorResta
import com.tema.wakkey.resta.RestaGenerador

class RestaActivity : AppCompatActivity() {
    private lateinit var preguntas: List<RestaGenerador>
    private var preguntaActual = 0
    private var timer: CountDownTimer? = null
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )


        setContentView(R.layout.activity_resta)
        // Recuperar dificultad del intent
        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        preguntas = GeneradorResta().generarPreguntas(dificultad)

        if (preguntas.isEmpty()) {
            mostrarMensaje("No se pudieron generar preguntas. Intenta nuevamente.")
            finish()
            return
        }

        mostrarPreguntaActual()
        iniciarTemporizador()

        // Reproducir sonido al comenzar el juego
        val sonidoResId = R.raw.morning // Aquí puedes poner cualquier sonido que quieras usar
        AlarmSoundPlayer.start(this, sonidoResId)

        findViewById<Button>(R.id.btnVerificar).setOnClickListener {
            val respuestaTexto = findViewById<EditText>(R.id.tvRespuesta).text.toString()
            val respuesta = respuestaTexto.toIntOrNull()

            if (respuesta == preguntas[preguntaActual].resultado) {
                preguntaActual++
                if (preguntaActual < preguntas.size) {
                    mostrarPreguntaActual()
                } else {
                    mostrarMensaje("¡Lo has conseguido! Has respondido todas las preguntas.")
                    finalizarJuego()
                }
            } else {
                Toast.makeText(this, "Respuesta incorrecta. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarPreguntaActual() {
        if (preguntaActual < preguntas.size) {
            val pregunta = preguntas[preguntaActual]
            findViewById<TextView>(R.id.tvSuma).text = "${pregunta.enunciado} ="
            findViewById<EditText>(R.id.tvRespuesta).setText("")
        }
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(tiempoLimite, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutos = millisUntilFinished / 60000
                val segundos = (millisUntilFinished % 60000) / 1000
                findViewById<TextView>(R.id.tvTimer).text =
                    "${"%02d".format(minutos)}:${"%02d".format(segundos)}"
            }

            override fun onFinish() {
                mostrarMensaje("Tiempo agotado")
                finalizarJuego()
            }
        }.start()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        // Detener el sonido cuando la actividad sea destruida
        AlarmSoundPlayer.stop()
    }

    private fun finalizarJuego() {
        timer?.cancel()
        // Detener el sonido al finalizar el juego
        AlarmSoundPlayer.stop()

        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
