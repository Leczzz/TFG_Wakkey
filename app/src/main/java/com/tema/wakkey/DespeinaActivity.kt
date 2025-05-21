package com.tema.wakkey

import android.app.KeyguardManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class DespeinaActivity : AppCompatActivity() {
    private lateinit var recorder: MediaRecorder
    private var timer: CountDownTimer? = null
    private var sonidoTimer: CountDownTimer? = null
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos
    private var progreso = 0
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pantalla encendida y desbloqueo
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        setContentView(R.layout.despeinakkey_main)

        progressBar = findViewById(R.id.progresoBarra)

        // Reproducir sonido de alarma
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
        iniciarMicrofono()
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

    private fun iniciarMicrofono() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile("/dev/null")
            prepare()
            start()
        }

        sonidoTimer = object : CountDownTimer(tiempoLimite, 200) {
            override fun onTick(millisUntilFinished: Long) {
                val amplitud = recorder.maxAmplitude
                if (amplitud > 2000) {
                    progreso += 1
                    progressBar.progress = progreso
                    if (progreso >= 100) {
                        mostrarMensaje("¡Despeinaste a Kkey!")
                        finalizarJuego()
                    }
                }
            }

            override fun onFinish() {}
        }.start()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun finalizarJuego() {
        timer?.cancel()
        sonidoTimer?.cancel()
        recorder.release()
        AlarmSoundPlayer.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        finalizarJuego()
    }
}
