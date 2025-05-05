package com.tema.wakkey

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetenerAlarmaActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deteneralarma_activity)

        val idAlarma = intent.getIntExtra("idAlarma", -1)

        if (idAlarma != -1) {
            lifecycleScope.launch {
                val alarma = withContext(Dispatchers.IO) {
                    val db = AppDatabase.getInstance(applicationContext)
                    db.alarmDao().obtenerAlarmaPorId(idAlarma)
                }

                val nombreSonido = when (alarma?.sonido) {
                    "Crystal Waters" -> "crystalwaters"
                    "Hawaii" -> "hawai"
                    "Lofi" -> "lofi"
                    "Morning" -> "morning"
                    "Piano" -> "piano"
                    else -> "crystalwaters"
                }

                val recursoSonido = resources.getIdentifier(nombreSonido, "raw", packageName)

                mediaPlayer = MediaPlayer.create(this@DetenerAlarmaActivity, recursoSonido)
                mediaPlayer?.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
