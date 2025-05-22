package com.tema.wakkey

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Esta clase se encarga de manejar la lógica y la interacción de la actividad de Detener Alarma.
class DetenerAlarmaActivity : AppCompatActivity() {

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

        setContentView(R.layout.deteneralarma_activity)

        val idAlarma = intent.getIntExtra("idAlarma", -1)

        if (idAlarma != -1) { // Si se pasó el ID de la alarma
            lifecycleScope.launch { // Iniciar una corrutina
                val alarma = withContext(Dispatchers.IO) { // Ejecutar en un hilo de E/S
                    val db = AppDatabase.getInstance(applicationContext) //  Obtener la instancia de la base de datos
                    db.alarmDao().obtenerAlarmaPorId(idAlarma) // Obtener alarma por ID
                }

                if (alarma != null) { // Si la alarma existe
                    val sonidoNombre = intent.getStringExtra("sonido") ?: "Morning"
                    val sonidoResId = when (sonidoNombre) {
                        "Crystal Waters" -> R.raw.crystalwaters
                        "Hawaii" -> R.raw.hawai
                        "Lofi" -> R.raw.lofi
                        "Morning" -> R.raw.morning
                        "Piano" -> R.raw.piano
                        else -> R.raw.morning
                    }
                    AlarmSoundPlayer.start(this@DetenerAlarmaActivity, sonidoResId)
                }
            }
        }

        findViewById<Button>(R.id.btnDetenerAlarma).setOnClickListener { // Botón para detener la alarma
            detenerSonidoYSalir() // Detener el sonido y salir
        }
    }

    private fun detenerSonidoYSalir() {
        AlarmSoundPlayer.stop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundPlayer.stop()
    }
}
