package com.tema.wakkey

import android.app.KeyguardManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Button

class DetenerAlarmaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mostrar encima del bloqueo y encender pantalla
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        // Desbloquear el keyguard si es posible
        val keyguardManager = getSystemService(KeyguardManager::class.java)
        keyguardManager?.requestDismissKeyguard(this, null)

        setContentView(R.layout.deteneralarma_activity)

        val idAlarma = intent.getIntExtra("idAlarma", -1)

        if (idAlarma != -1) {
            lifecycleScope.launch {
                val alarma = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    db.alarmDao().obtenerAlarmaPorId(idAlarma)
                }

                if (alarma != null) {
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

        findViewById<Button>(R.id.btnDetenerAlarma).setOnClickListener {
            detenerSonidoYSalir()
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
