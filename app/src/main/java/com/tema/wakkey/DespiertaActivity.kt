package com.tema.wakkey

import android.app.KeyguardManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class DespiertaActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var progreso = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var wakkeyImage: ImageView
    private var timer: CountDownTimer? = null
    private val tiempoLimite = 5 * 60 * 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar encima del bloqueo
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        setContentView(R.layout.despiertakkey_main)

        progressBar = findViewById(R.id.progresoBarra)
        wakkeyImage = findViewById(R.id.imgGatito)

        // Sonido
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

        iniciarTemporizador()
        iniciarSensor()
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

    private fun iniciarSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        acelerometro?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val aceleracion = sqrt(it.values[0] * it.values[0] +
                    it.values[1] * it.values[1] +
                    it.values[2] * it.values[2])
            if (aceleracion > 15) {
                progreso++
                progressBar.progress = progreso
                wakkeyImage.setImageResource(R.drawable.sinfondoenfadado)
                if (progreso >= 100) {
                    mostrarMensaje("¡Despertaste a Kkey!")
                    finalizarJuego()
                }
            } else {
                wakkeyImage.setImageResource(R.drawable.kkeydormido)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun finalizarJuego() {
        timer?.cancel()
        sensorManager.unregisterListener(this)
        AlarmSoundPlayer.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        finalizarJuego()
    }
}
