package com.tema.wakkey

import android.app.KeyguardManager
import android.os.Bundle
import android.os.Handler
import android.os.Build
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetenerAlarmaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) { // API 27+
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(KeyguardManager::class.java)
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
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
                    // Aquí usamos el recurso de sonido con AlarmSoundPlayer.start()
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
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        AlarmSoundPlayer.stop()
    }
}
