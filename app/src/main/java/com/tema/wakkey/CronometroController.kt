package com.tema.wakkey

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView

class CronometroController(private val onTick: (String) -> Unit) {

    private var startTime = 0L
    private var accumulatedTime = 0L
    var isRunning = false
    var isPaused = false // Variable para saber si está pausado
    private var tiempoUltimaVuelta: Long = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val time = System.currentTimeMillis() - startTime + accumulatedTime
                onTick(formatTime(time))
                handler.postDelayed(this, 1000)
            }
        }
    }

    val vueltas = mutableListOf<Vuelta>()

    data class Vuelta(val numero: Int, val tiempoVuelta: String, val tiempoTotal: String)

    fun start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis()
            isRunning = true
            isPaused = false // Al iniciar, no está pausado
            handler.post(updateRunnable)
        }
    }

    fun pause() {
        if (isRunning) {
            accumulatedTime += System.currentTimeMillis() - startTime
            isRunning = false
            isPaused = true // Al pausar, se marca como pausado
            handler.removeCallbacks(updateRunnable)
        }
    }

    fun resume() {
        if (isPaused) {
            startTime = System.currentTimeMillis()
            isRunning = true
            isPaused = false // Al reanudar, ya no está pausado
            handler.post(updateRunnable)
        }
    }

    fun reset() {
        isRunning = false
        isPaused = false // Al resetear, ya no está ni corriendo ni pausado
        startTime = 0
        accumulatedTime = 0
        handler.removeCallbacks(updateRunnable)
        onTick("00:00:00")
        vueltas.clear()
    }

    fun getElapsedTime(): Long {
        return if (isRunning)
            System.currentTimeMillis() - startTime + accumulatedTime
        else
            accumulatedTime
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun getFormattedElapsedTime(): String {
        return formatTime(getElapsedTime())
    }

    // Maneja las vueltas
    fun registrarVuelta(tablaVueltas: TableLayout) {
        val totalMs = getElapsedTime()
        val tiempoVueltaMs = totalMs - tiempoUltimaVuelta
        tiempoUltimaVuelta = totalMs

        val vuelta = Vuelta(
            numero = vueltas.size + 1,
            tiempoVuelta = formatTime(tiempoVueltaMs),
            tiempoTotal = getFormattedElapsedTime()
        )

        vueltas.add(vuelta)
        agregarFilaTabla(vuelta, tablaVueltas)
    }

    // Agregar fila a la tabla de vueltas
    private fun agregarFilaTabla(vuelta: Vuelta, tablaVueltas: TableLayout) {
        val fila = TableRow(tablaVueltas.context).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Crear celdas para la tabla
        fun createTableCell(text: String): TextView {
            return TextView(tablaVueltas.context).apply {
                this.text = text
                textSize = 16f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                setPadding(12, 12, 12, 12)
            }
        }

        fila.addView(createTableCell("Vuelta ${vuelta.numero}"))
        fila.addView(createTableCell(vuelta.tiempoVuelta))
        fila.addView(createTableCell(vuelta.tiempoTotal))

        tablaVueltas.addView(fila) // Agregar la fila a la tabla
    }
}
