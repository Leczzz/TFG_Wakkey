package com.tema.wakkey

import android.Manifest
import android.app.KeyguardManager
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
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

//Esta clase se encarga de manejar la lógica y la interacción de la actividad de Despeina.
class DespeinaActivity : AppCompatActivity() {
    private var recorder: MediaRecorder? = null
    private var isRecorderStarted = false
    private var timer: CountDownTimer? = null
    private var sonidoTimer: CountDownTimer? = null
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos
    private var progreso = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var config: DespeinaConfig
    private lateinit var kkeyImage: ImageView

    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compatibilidad para versiones antiguas
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or // Mantener la pantalla encendida
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or // Desactivar el bloqueo de pantalla
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or // Mostrar la actividad cuando la pantalla está desactivada
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON // Activar la pantalla cuando la actividad se muestra
        )

        // Requiere API 27+
        setShowWhenLocked(true) // Mostrar la actividad cuando la pantalla está desactivada
        setTurnScreenOn(true) // Activar la pantalla cuando la actividad se muestra

        // Solicitar desbloqueo del Keyguard (bloqueo de pantalla)
        val keyguardManager = getSystemService(KeyguardManager::class.java) // Obtener el servicio de Keyguard
        keyguardManager?.requestDismissKeyguard(this, null) // Solicitar el desbloqueo del Keyguard

        setContentView(R.layout.despeinakkey_main)

        progressBar = findViewById(R.id.progresoBarra) // Barra de progreso
        kkeyImage = findViewById(R.id.imgGatito) // Imagen de Kkey

        // Obtener dificultad desde la Intent, valor por defecto "F"
        val dificultad = intent.getStringExtra("dificultad") ?: "F"
        val generador = DespeinaGenerador() // Generador de configuraciones
        config = generador.obtenerConfiguracion(dificultad) // Obtener configuración según dificultad

        // Configurar barra de progreso con el progreso objetivo según dificultad
        progressBar.max = config.progresoObjetivo

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

        // Pedir permiso para usar micrófono si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            iniciarTemporizador()
            iniciarMicrofono()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarTemporizador()
                iniciarMicrofono()
            } else {
                mostrarMensaje("Permiso de micrófono denegado. No se puede iniciar el juego.")
                finish()
            }
        }
    }

    // Iniciar temporizador
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

    // Iniciar micrófono
    private fun iniciarMicrofono() {
        recorder = MediaRecorder().apply { // Configuración del grabador de audio
            try { // Intenta iniciar el grabador
                setAudioSource(MediaRecorder.AudioSource.MIC) // Fuente del audio
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Formato de salida
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // Codificador de audio
                val outputFile = filesDir.absolutePath + "/temp_audio.3gp" // Ruta del archivo de salida
                setOutputFile(outputFile) // Establece la ruta del archivo de salida
                prepare() // Prepara el grabador
                start() // Inicia la grabación
                isRecorderStarted = true // Marca el grabador como iniciado
            } catch (e: Exception) {
                e.printStackTrace()
                mostrarMensaje("No se pudo iniciar el micrófono: ${e.message}")
                finish()
            }
        }

        sonidoTimer = object : CountDownTimer(tiempoLimite, 200) { // Temporizador para el sonido
            override fun onTick(millisUntilFinished: Long) {
                if (isRecorderStarted) { // Si el grabador está iniciado
                    try {
                        val amplitud = ((recorder?.maxAmplitude ?: 0) * config.sensibilidad).toInt() // Calcula la amplitud
                        if (amplitud > 1000) { // Si la amplitud es mayor a un umbral
                            progreso += 5
                            if (progreso > config.progresoObjetivo) {
                                progreso = config.progresoObjetivo // Si el progreso supera el objetivo, establecerlo
                            }
                            progressBar.progress = progreso // Actualiza la barra de progreso
                            kkeyImage.setImageResource(R.drawable.sinfondoenfadado) // Imagen al detectar sonido
                            if (progreso >= config.progresoObjetivo) { // Si el progreso llega al objetivo
                                mostrarMensaje("¡Despeinaste a Kkey!") // Mensaje de despeinado exitoso
                                finalizarJuego()
                            }
                        } else {
                            kkeyImage.setImageResource(R.drawable.kkeysinfondo) // Imagen cuando no detecta sonido
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFinish() {}
        }.start()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun finalizarJuego() { // Finaliza el juego
        timer?.cancel() // Cancela el temporizador
        sonidoTimer?.cancel() // Cancela el temporizador del sonido
        if (isRecorderStarted) { // Si el grabador está iniciado
            try { // Intenta detener y liberar el grabador
                recorder?.stop() // Detiene la grabación
            } catch (e: Exception) {
                e.printStackTrace()
            }
            recorder?.release() // Libera el grabador
            isRecorderStarted = false // Marca el grabador como detenido
        }
        AlarmSoundPlayer.stop() // Detiene la reproducción del sonido
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        finalizarJuego()
    }
}
