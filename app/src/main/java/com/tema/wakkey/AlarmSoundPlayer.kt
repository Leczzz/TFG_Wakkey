package com.tema.wakkey

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    // Inicia el sonido, deteniendo cualquier sonido anterior si está en reproducción
    fun start(context: Context, recursoSonido: Int) {
        stop() // Detener cualquier sonido anterior antes de iniciar uno nuevo
        mediaPlayer = MediaPlayer.create(context, recursoSonido).apply {
            isLooping = true
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            start()
        }
    }

    // Detiene el sonido si está reproduciéndose
    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                release()
            }
        }
        mediaPlayer = null
    }


}
