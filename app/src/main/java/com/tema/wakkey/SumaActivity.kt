package com.tema.wakkey

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
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
    private lateinit var mediaPlayer: MediaPlayer
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suma)

        // Obtener dificultad y generar preguntas
        val dificultad = intent.getStringExtra("dificultad") ?: "fácil"
        preguntas = GeneradorSuma().generarPreguntas(dificultad)

        // Verificar si se generaron preguntas
        if (preguntas.isEmpty()) {
            mostrarMensaje("No se pudieron generar preguntas. Intenta nuevamente.")
            finish()
            return
        }

        // Mostrar la primera pregunta
        mostrarPreguntaActual()

        // Iniciar el temporizador
        iniciarTemporizador()

        // Reproducir sonido según selección
        val sonidoNombre = intent.getStringExtra("sonido")
        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }

        mediaPlayer = MediaPlayer.create(this, sonidoResId)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // Botón para verificar respuesta
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
            findViewById<EditText>(R.id.tvRespuesta).setText("") // Limpiar respuesta anterior
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
                // Si el tiempo se agota, finalizar el juego
                mostrarMensaje("Tiempo agotado")
                finalizarJuego()
            }
        }.start()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun finalizarJuego() {
        // Detener el temporizador
        timer?.cancel()

        // Detener y liberar el MediaPlayer si está en reproducción
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        // Finalizar la actividad, regresando a la pantalla anterior (alarma)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        // Asegurarse de cancelar el temporizador y liberar recursos
        timer?.cancel()
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}
