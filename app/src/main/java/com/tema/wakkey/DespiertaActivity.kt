package com.tema.wakkey

import android.app.KeyguardManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.despiertakkey.DespiertaKkeyGenerador
import kotlin.math.sqrt

// Esta clase se encarga de manejar la lógica y la interacción de la actividad de Despierta.
class DespiertaActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager // Manejador de sensores
    private var acelerometro: Sensor? = null // Sensor de aceleración
    private var progreso = 0 // Progreso de la barra de progreso
    private lateinit var progressBar: ProgressBar // Barra de progreso
    private lateinit var wakkeyImage: ImageView // Imagen de Kkey
    private lateinit var porcentajeProgreso: TextView // Texto de porcentaje
    private var timer: CountDownTimer? = null // Temporizador
    private val tiempoLimite = 5 * 60 * 1000L // 5 minutos

    // Variables para configuración dinámica
    private var progresoObjetivo = 100
    private var sensibilidad = 15.0f

    // Handler para cambiar la imagen de vuelta a dormido después de un tiempo
    private val handler = Handler(Looper.getMainLooper())
    private var resetImageRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compatibilidad adicional para versiones antiguas
        window.addFlags( // Configuración de la ventana
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or // Mantener la pantalla encendida
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        setShowWhenLocked(true) // Mostrar la actividad cuando la pantalla está desactivada
        setTurnScreenOn(true) // Activar la pantalla cuando la actividad se muestra

        val keyguardManager = getSystemService(KeyguardManager::class.java) // Solicitar desbloqueo del Keyguard
        keyguardManager?.requestDismissKeyguard(this, null) // Solicitar el desbloqueo del Keyguard

        setContentView(R.layout.despiertakkey_main)

        progressBar = findViewById(R.id.progresoBarra) // Barra de progreso
        wakkeyImage = findViewById(R.id.imgGatito) // Imagen de Kkey
        porcentajeProgreso = findViewById(R.id.porcentajeProgreso) // Texto porcentaje

        // Obtener dificultad del Intent, default a "F"
        val dificultad = intent.getStringExtra("dificultad") ?: "F"

        // Obtener configuración desde el generador
        val generador = DespiertaKkeyGenerador()
        val config = generador.obtenerConfiguracion(dificultad)

        progresoObjetivo = config.progresoObjetivo // Configurar el progreso objetivo
        sensibilidad = config.sensibilidad // Configurar la sensibilidad

        // Configurar la barra con el progreso objetivo
        progressBar.max = progresoObjetivo

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

        iniciarTemporizador() // Iniciar temporizador
        iniciarSensor() // Iniciar el sensor de aceleración
    }

    private fun iniciarTemporizador() { // Temporizador
        timer = object : CountDownTimer(tiempoLimite, 1000) { // Temporizador de 5 minutos
            override fun onTick(millisUntilFinished: Long) { // Actualizar el temporizador
                val minutos = millisUntilFinished / 60000 // Minutos y segundos
                val segundos = (millisUntilFinished % 60000) / 1000 // Minutos y segundos
                findViewById<TextView>(R.id.tvTimer).text = // Actualizar el TextView
                    "${"%02d".format(minutos)}:${"%02d".format(segundos)}" // Formato de tiempo
            }

            override fun onFinish() {
                mostrarMensaje("Tiempo agotado")
                finalizarJuego() // Finalizar el juego
            }
        }.start()
    }

    private fun iniciarSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager // Manejador de sensores
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) // Sensor de aceleración
        acelerometro?.let { // Si el sensor de aceleración existe
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) // Registrar el listener
        }
    }

    override fun onSensorChanged(event: SensorEvent?) { // Manejar cambios en el sensor
        event?.let { // Si el evento no es nulo
            val aceleracion = sqrt( // Calcular la aceleración
                it.values[0] * it.values[0] + // Aceleración en el eje X
                        it.values[1] * it.values[1] + // Aceleración en el eje Y
                        it.values[2] * it.values[2] // Aceleración en el eje Z
            )
            // Usar sensibilidad dinámica para la comparación
            if (aceleracion > sensibilidad) { // Si la aceleración es mayor a la sensibilidad
                progreso++ // Incrementar el progreso
                progressBar.progress = progreso // Actualizar la barra de progreso

                // Actualizar texto del porcentaje de progreso
                val porcentaje = (progreso * 100 / progresoObjetivo).coerceAtMost(100)
                porcentajeProgreso.text = "$porcentaje%"

                wakkeyImage.setImageResource(R.drawable.kkeysinfondo) // Cambiar la imagen

                // Cancelar cualquier runnable previo
                resetImageRunnable?.let { handler.removeCallbacks(it) }

                // Definir nuevo runnable para volver a la imagen dormida tras 500ms
                resetImageRunnable = Runnable {
                    wakkeyImage.setImageResource(R.drawable.kkeydormido)
                }
                handler.postDelayed(resetImageRunnable!!, 500)

                if (progreso >= progresoObjetivo) { // Si el progreso llega al objetivo
                    mostrarMensaje("¡Despertaste a Kkey!") // Mensaje de despeinado exitoso
                    finalizarJuego() // Finalizar el juego
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show() // Mostrar mensaje
    }

    private fun finalizarJuego() { // Finalizar el juego
        timer?.cancel() // Cancelar el temporizador
        sensorManager.unregisterListener(this) // Desregistrar el listener
        AlarmSoundPlayer.stop() // Detener la reproducción del sonido
        finish() // Finalizar la actividad
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(resetImageRunnable ?: Runnable { }) // Eliminar callbacks pendientes
        finalizarJuego()
    }
}
