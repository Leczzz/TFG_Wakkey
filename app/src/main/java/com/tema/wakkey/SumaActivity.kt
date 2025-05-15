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
import com.tema.wakkey.suma.GeneradorSuma
import com.tema.wakkey.suma.SumaGenerador

class SumaActivity : AppCompatActivity() {
    private lateinit var preguntas: List<SumaGenerador>
    private var preguntaActual = 0
    private var timer: CountDownTimer? = null
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suma)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )


        // Recuperamos la dificultad desde el Intent
        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        preguntas = GeneradorSuma().generarPreguntas(dificultad)

        // Verificar si la lista de preguntas está vacía
        if (preguntas.isEmpty()) {
            mostrarMensaje("No se pudieron generar preguntas. Intenta nuevamente.")
            finish()
            return
        }

        // Mostrar la primera pregunta
        mostrarPreguntaActual()

        // Iniciar el temporizador
        iniciarTemporizador()

        // Reproducir sonido usando AlarmSoundPlayer
        val sonidoNombre = intent.getStringExtra("sonido")
        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }

        AlarmSoundPlayer.start(this, sonidoResId)

        // Acción para verificar la respuesta
        findViewById<Button>(R.id.btnVerificar).setOnClickListener {
            val respuestaTexto = findViewById<EditText>(R.id.tvRespuesta).text.toString()
            val respuesta = respuestaTexto.toIntOrNull()

            // Comprobar si la respuesta es correcta
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
        // Mostrar la pregunta actual en la vista
        if (preguntaActual < preguntas.size) {
            val pregunta = preguntas[preguntaActual]
            findViewById<TextView>(R.id.tvSuma).text = "${pregunta.enunciado} ="
            findViewById<EditText>(R.id.tvRespuesta).setText("") // Limpiar el campo de respuesta
        }
    }

    private fun iniciarTemporizador() {
        // Iniciar un temporizador de cuenta regresiva de 5 minutos
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
        // Detener el temporizador
        timer?.cancel()

        // Detener el sonido al finalizar el juego
        AlarmSoundPlayer.stop()

        // Iniciar la actividad de la alarma
        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
