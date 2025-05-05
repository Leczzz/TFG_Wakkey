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
            // Llamada a la base de datos para obtener la alarma
            lifecycleScope.launch {
                val alarma = withContext(Dispatchers.IO) {
                    // Obtener la instancia de la base de datos correctamente
                    val db = AppDatabase.getInstance(applicationContext)
                    db.alarmDao().obtenerAlarmaPorId(idAlarma)
                }

                // Asegurarse de que la alarma no sea nula
                alarma?.let {
                    // Asignar el nombre del sonido de acuerdo con el valor de la alarma
                    val nombreSonido = when (it.sonido) {
                        "Crystal Waters" -> "crystalwaters"
                        "Hawaii" -> "hawaii"
                        "Lofi" -> "lofi"
                        "Morning" -> "morning"
                        "Piano" -> "piano"
                        else -> "crystalwaters"  // Default
                    }

                    // Buscar el recurso de sonido
                    val recursoSonido = resources.getIdentifier(nombreSonido, "raw", packageName)

                    // Crear y reproducir el MediaPlayer
                    mediaPlayer = MediaPlayer.create(this@DetenerAlarmaActivity, recursoSonido)
                    mediaPlayer?.start()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar el MediaPlayer al destruir la actividad
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
