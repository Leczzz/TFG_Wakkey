package com.tema.wakkey

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class CrearAlarmaActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_alarma)

        val spinnerSonido = findViewById<Spinner>(R.id.spinnerSonido)

        val sonidosMap = mapOf(
            "Beep Beep " to R.raw.beepbepp,
            "Crystal Waters" to R.raw.crystalwaters,
            "Hawaii" to R.raw.hawai,
            "Lofi" to R.raw.lofi,
            "Morning" to R.raw.morning,
            "Piano" to R.raw.piano
        )

        spinnerSonido.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val sonidoSeleccionado = parent.getItemAtPosition(position).toString()
                val sonidoResId = sonidosMap[sonidoSeleccionado] ?: return

                // Libera si ya hay uno reproduciéndose
                if (::mediaPlayer.isInitialized) {
                    mediaPlayer.release()
                }

                mediaPlayer = MediaPlayer.create(this@CrearAlarmaActivity, sonidoResId)
                mediaPlayer.start()

                // Detener después de 5 segundos
                handler.postDelayed({
                    if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                    }
                }, 5000)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
