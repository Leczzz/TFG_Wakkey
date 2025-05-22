package com.tema.wakkey

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
// Gestiona la reproducción en bucle del sonido de alarma usando MediaPlayer.
//Permite iniciar y detener el sonido fácilmente, asegurando que no haya solapamientos.

object AlarmSoundPlayer {
    private var mediaPlayer: MediaPlayer? = null // MediaPlayer para reproducir el sonido

    fun start(context: Context, recursoSonido: Int) { // Inicia la reproducción del sonido
        stop() // Para cualquier sonido que pueda estar actualmente reproduciendo
        mediaPlayer = MediaPlayer.create(context, recursoSonido)?.apply { // Crea el MediaPlayer
            isLooping = true // Reproduce el sonido en bucle
            setAudioAttributes( // Configura los atributos de audio
                AudioAttributes.Builder() // Construye los atributos de audio
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) // Tipo de contenido: Música
                    .setUsage(AudioAttributes.USAGE_ALARM) // Uso: Alarma
                    .build() // Construye los atributos de audio
            )
            start() // Inicia la reproducción del sonido
        }
    }

    fun stop() { // Para la reproducción del sonido
        mediaPlayer?.apply { // Si hay un MediaPlayer existente
            if (isPlaying) { // Si está reproduciendo
                stop() // Detén la reproducción
            }
            release() // Libera los recursos
        }
        mediaPlayer = null // El MediaPlayer se establece en null
    }
}
