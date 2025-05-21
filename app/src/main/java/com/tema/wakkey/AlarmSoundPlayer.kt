package com.tema.wakkey

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context, recursoSonido: Int) {
        stop()
        mediaPlayer = MediaPlayer.create(context, recursoSonido)?.apply {
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            start()
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}
