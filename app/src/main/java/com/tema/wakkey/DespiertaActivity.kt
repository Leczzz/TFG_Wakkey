package com.tema.wakkey

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.despiertakkey.DespiertaConfig
import com.tema.wakkey.despiertakkey.DespiertaKkeyGenerador
import kotlin.math.sqrt

class DespiertaActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var porcentajeTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var imageView: ImageView

    private var progreso = 0
    private var incrementoProgreso = 2
    private var progresoObjetivo = 100
    private var sensibilidad = 1.0f

    private var tiempoRestanteMs: Long = 5 * 60 * 1000 // 5 minutos
    private var timer: CountDownTimer? = null
    private var ultimoTiempoSacudida = 0L
    private var agitando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.despiertakkey_main)

        progressBar = findViewById(R.id.progresoBarra)
        porcentajeTextView = findViewById(R.id.porcentajeProgreso)
        timerTextView = findViewById(R.id.tvTimer)
        imageView = findViewById(R.id.imgGatito)

        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        val sonidoNombre = intent.getStringExtra("sonido")

        val config: DespiertaConfig = DespiertaKkeyGenerador().obtenerConfiguracion(dificultad)
        incrementoProgreso = when (dificultad) {
            "F" -> 8
            "M" -> 4
            "D" -> 2
            else -> 8
        }
        progresoObjetivo = config.progresoObjetivo
        sensibilidad = config.sensibilidad
        progressBar.max = progresoObjetivo

        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }
        AlarmSoundPlayer.start(this, sonidoResId)

        iniciarJuego()
    }

    private fun iniciarJuego() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        acelerometro?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        iniciarTemporizador()
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                val fuerza = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                if (fuerza > sensibilidad) {
                    val tiempoActual = System.currentTimeMillis()
                    if (tiempoActual - ultimoTiempoSacudida > 300) {
                        agitando = true
                        imageView.setImageResource(R.drawable.kkeysinfondo)
                        aumentarProgreso()
                        ultimoTiempoSacudida = tiempoActual
                    }
                } else {
                    if (agitando) {
                        agitando = false
                        imageView.setImageResource(R.drawable.kkeydormido)
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun aumentarProgreso() {
        if (progreso < progresoObjetivo) {
            progreso += incrementoProgreso
            progressBar.progress = progreso.coerceAtMost(progresoObjetivo)
            val porcentaje = (progreso * 100 / progresoObjetivo).coerceAtMost(100)
            porcentajeTextView.text = "$porcentaje%"

            if (progreso >= progresoObjetivo) {
                detenerJuego()
                Toast.makeText(this, "¡Despertaste a Kkey!", Toast.LENGTH_LONG).show()
                finalizarJuego()
            }
        }
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(tiempoRestanteMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutos = millisUntilFinished / 60000
                val segundos = (millisUntilFinished % 60000) / 1000
                timerTextView.text = String.format("%02d:%02d", minutos, segundos)
            }

            override fun onFinish() {
                detenerJuego()
                Toast.makeText(this@DespiertaActivity, "Tiempo agotado", Toast.LENGTH_SHORT).show()
                finalizarJuego()
            }
        }.start()
    }

    private fun detenerJuego() {
        timer?.cancel()
        sensorManager.unregisterListener(sensorListener)
        AlarmSoundPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerJuego()
    }

    private fun finalizarJuego() {
        val intent = Intent(this, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
