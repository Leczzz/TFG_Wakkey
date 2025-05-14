package com.tema.wakkey

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class DespeinaActivity : AppCompatActivity() {

    private lateinit var recorder: MediaRecorder
    private lateinit var progressBar: ProgressBar
    private lateinit var porcentajeTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var imageView: ImageView

    private var progress = 0
    private var handler = Handler(Looper.getMainLooper())
    private var updateTask: Runnable? = null
    private var tiempoRestanteMs: Long = 5 * 60 * 1000 // 5 minutos
    private var timer: CountDownTimer? = null
    private var sonidoDetectado = false

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var outputFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.despeinakkey_main)

        progressBar = findViewById(R.id.progresoBarra)
        porcentajeTextView = findViewById(R.id.porcentajeProgreso)
        timerTextView = findViewById(R.id.tvTimer)
        imageView = findViewById(R.id.imgGatito)

        // Verificar permisos para grabar audio
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
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

        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(outputFile.absolutePath)

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
                    if (maxAmplitude > 2000) {
                        sonidoDetectado = true
                        imageView.setImageResource(R.drawable.sinfondoenfadado)
                        aumentarProgreso()
                    } else {
                        sonidoDetectado = false
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
        if (progress < 100) {
            progress += 2
            progressBar.progress = progress
            porcentajeTextView.text = "$progress%"

            if (progress >= 100) {
                detenerJuego()
                Toast.makeText(this, "¡Despeinaste a Kkey!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun iniciarTemporizador() {
        timer = object : CountDownTimer(tiempoRestanteMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutos = millisUntilFinished / 60000
                val segundos = (millisUntilFinished % 60000) / 1000
                timerTextView.text = String.format("%d:%02d", minutos, segundos)
            }

            override fun onFinish() {
                detenerJuego()
                Toast.makeText(this@DespeinaActivity, "Tiempo agotado", Toast.LENGTH_SHORT).show()
                finish()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        detenerJuego()
    }
}
