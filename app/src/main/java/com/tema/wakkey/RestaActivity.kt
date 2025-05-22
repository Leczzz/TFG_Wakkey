package com.tema.wakkey

import android.app.KeyguardManager
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
// Esta clase se encarga de manejar la lógica y la interacción de la actividad de Resta.
class RestaActivity : AppCompatActivity() {
    private lateinit var preguntas: List<RestaGenerador> // Lista de preguntas
    private var preguntaActual = 0 // Índice de la pregunta actual
    private var timer: CountDownTimer? = null // Temporizador
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compatibilidad adicional para versiones antiguas
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        // Requiere API 27+
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Solicitar desbloqueo del Keyguard (bloqueo de pantalla)
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        setContentView(R.layout.activity_resta)

        // Recuperar dificultad del intent
        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        preguntas = GeneradorResta().generarPreguntas(dificultad)

        if (preguntas.isEmpty()) {
            mostrarMensaje("No se pudieron generar preguntas. Intenta nuevamente.")
            finish()
            return
        }

        mostrarPreguntaActual() // Mostrar la primera pregunta
        iniciarTemporizador() // Iniciar temporizador

        // Reproducir sonido recibido por intent
        val sonidoNombre = intent.getStringExtra("sonido") // Nombre del sonido
        val sonidoResId = when (sonidoNombre) { // ID del sonido
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }
        AlarmSoundPlayer.start(this, sonidoResId) // Reproducir sonido

        findViewById<Button>(R.id.btnVerificar).setOnClickListener { // Verificar respuesta
            val respuestaTexto = findViewById<EditText>(R.id.tvRespuesta).text.toString() // Respuesta del usuario
            val respuesta = respuestaTexto.toIntOrNull() // Convertir a entero

            if (respuesta == preguntas[preguntaActual].resultado) { // Si la respuesta es correcta
                preguntaActual++ // Incrementar la pregunta actual
                if (preguntaActual < preguntas.size) { // Si hay más preguntas
                    mostrarPreguntaActual() // Mostrar la siguiente pregunta
                } else {
                    mostrarMensaje("¡Lo has conseguido! Has respondido todas las preguntas.")
                    finalizarJuego()
                }
            } else {
                Toast.makeText(this, "Respuesta incorrecta. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarPreguntaActual() { // Mostrar la pregunta actual
        if (preguntaActual < preguntas.size) { // Si hay más preguntas
            val pregunta = preguntas[preguntaActual] // Pregunta actual
            findViewById<TextView>(R.id.tvSuma).text = "${pregunta.enunciado} =" // Mostrar la pregunta
            findViewById<EditText>(R.id.tvRespuesta).setText("") // Limpiar la respuesta
        }
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(tiempoLimite, 1000) { // Temporizador
            override fun onTick(millisUntilFinished: Long) { // Actualizar el temporizador
                val minutos = millisUntilFinished / 60000 // Minutos y segundos
                val segundos = (millisUntilFinished % 60000) / 1000 // Minutos y segundos
                findViewById<TextView>(R.id.tvTimer).text = // Actualizar el TextView
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
        AlarmSoundPlayer.stop()
    }

    private fun finalizarJuego() {
        timer?.cancel()
        AlarmSoundPlayer.stop()
        finish()
    }
}
