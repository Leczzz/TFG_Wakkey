package com.tema.wakkey

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tema.wakkey.despeinakkey.DespeinaConfig
import com.tema.wakkey.despeinakkey.DespeinaGenerador
import java.io.File
import java.io.IOException

class DespeinaActivity : AppCompatActivity() {

    private lateinit var recorder: MediaRecorder
    private lateinit var progressBar: ProgressBar
    private lateinit var porcentajeTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var imageView: ImageView

    private var progress = 0
    private var incrementoProgreso = 2
    private var progresoObjetivo = 100
    private var sensibilidad = 1.0f

    private var handler = Handler(Looper.getMainLooper())
    private var updateTask: Runnable? = null
    private var tiempoRestanteMs: Long = 5 * 60 * 1000 // 5 minutos
    private var timer: CountDownTimer? = null

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var outputFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.despeinakkey_main)

        progressBar = findViewById(R.id.progresoBarra)
        porcentajeTextView = findViewById(R.id.porcentajeProgreso)
        timerTextView = findViewById(R.id.tvTimer)
        imageView = findViewById(R.id.imgGatito)

        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        val sonidoNombre = intent.getStringExtra("sonido")

        // Configuración según dificultad
        val config: DespeinaConfig = DespeinaGenerador().obtenerConfiguracion(dificultad)
        incrementoProgreso = when (dificultad) {
            "F" -> 8
            "M" -> 4
            "D" -> 2
            else -> 8
        }
        progresoObjetivo = config.progresoObjetivo
        sensibilidad = config.sensibilidad
        progressBar.max = progresoObjetivo

        // Iniciar sonido
        val sonidoResId = when (sonidoNombre) {
            "Crystal Waters" -> R.raw.crystalwaters
            "Hawaii" -> R.raw.hawai
            "Lofi" -> R.raw.lofi
            "Morning" -> R.raw.morning
            "Piano" -> R.raw.piano
            else -> R.raw.morning
        }
        AlarmSoundPlayer.start(this, sonidoResId)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            iniciarJuego()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarJuego()
        } else {
            Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun iniciarJuego() {
        startRecording()
        startListening()
        iniciarTemporizador()
    }

    private fun startRecording() {
        outputFile = File.createTempFile("temp_record", ".3gp", cacheDir)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
        }

        try {
            recorder.prepare()
            recorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al preparar el micrófono", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al iniciar el micrófono", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun startListening() {
        updateTask = object : Runnable {
            override fun run() {
                try {
                    val maxAmplitude = recorder.maxAmplitude
                    if (maxAmplitude > 2000 * sensibilidad) {
                        imageView.setImageResource(R.drawable.sinfondoenfadado)
                        aumentarProgreso()
                    } else {
                        imageView.setImageResource(R.drawable.kkeysinfondo)
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                handler.postDelayed(this, 300)
            }
        }
        handler.post(updateTask!!)
    }

    private fun aumentarProgreso() {
        if (progress < progresoObjetivo) {
            progress += incrementoProgreso
            progressBar.progress = progress.coerceAtMost(progresoObjetivo)
            val porcentaje = (progress * 100 / progresoObjetivo).coerceAtMost(100)
            porcentajeTextView.text = "$porcentaje%"

            if (progress >= progresoObjetivo) {
                detenerJuego()
                Toast.makeText(this, "¡Despeinaste a Kkey!", Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@DespeinaActivity, "Tiempo agotado", Toast.LENGTH_SHORT).show()
                finalizarJuego()
            }
        }.start()
    }

    private fun detenerJuego() {
        try {
            updateTask?.let { handler.removeCallbacks(it) }
            if (::recorder.isInitialized) {
                recorder.stop()
                recorder.release()
            }
        } catch (_: Exception) { }
        timer?.cancel()
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
