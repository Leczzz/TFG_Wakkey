package com.tema.wakkey

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var cronometro: CronometroActivity

    private lateinit var txtTiempo: TextView
    private lateinit var btnIniciar: Button
    private lateinit var btnPausar: Button
    private lateinit var btnReiniciar: Button
    private lateinit var btnReanudar: Button
    private lateinit var btnDetener: Button

    private lateinit var tablaVueltas: TableLayout

    private var vueltas = mutableListOf<Vuelta>()
    private var tiempoUltimaVuelta: Long = 0L

    data class Vuelta(val numero: Int, val tiempoVuelta: String, val tiempoTotal: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cronometro_main)

        txtTiempo = findViewById(R.id.txtTiempo)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausar = findViewById(R.id.btnPausar)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnDetener = findViewById(R.id.btnDetener)
        tablaVueltas = findViewById(R.id.tablaVueltas)

        cronometro = CronometroActivity { tiempo ->
            runOnUiThread {
                txtTiempo.text = tiempo
            }
        }

        btnIniciar.setOnClickListener {
            cronometro.start()
            btnIniciar.visibility = View.GONE
            btnPausar.visibility = View.VISIBLE
            btnReiniciar.visibility = View.VISIBLE
            tiempoUltimaVuelta = 0L
            limpiarTablaVueltas()
        }

        btnPausar.setOnClickListener {
            registrarVuelta()
        }

        btnReiniciar.setOnClickListener {
            cronometro.pause()
            btnPausar.visibility = View.GONE
            btnReiniciar.visibility = View.GONE
            btnReanudar.visibility = View.VISIBLE
            btnDetener.visibility = View.VISIBLE
        }

        btnReanudar.setOnClickListener {
            cronometro.resume()
            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnPausar.visibility = View.VISIBLE
            btnReiniciar.visibility = View.VISIBLE
        }

        btnDetener.setOnClickListener {
            cronometro.reset()
            vueltas.clear()
            limpiarTablaVueltas()

            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnIniciar.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cronometro.reset() // Detener handler
    }

    private fun registrarVuelta() {
        val totalMs = cronometro.getElapsedTime()
        val tiempoVueltaMs = totalMs - tiempoUltimaVuelta
        tiempoUltimaVuelta = totalMs

        val tiempoTotal = formatTime(totalMs)
        val tiempoVuelta = formatTime(tiempoVueltaMs)
        val num = vueltas.size + 1

        val vuelta = Vuelta(num, tiempoVuelta, tiempoTotal)
        vueltas.add(vuelta)
        agregarFilaTabla(vuelta)
    }

    private fun agregarFilaTabla(vuelta: Vuelta) {
        val fila = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )

        }

        // Configuración común para las celdas
        fun createTableCell(text: String): TextView {
            return TextView(this).apply {
                this.text = text
                textSize = 16f // Tamaño aumentado
                setTextColor(Color.BLACK) // Texto en negro
                gravity = Gravity.CENTER
                setPadding(12, 12, 12, 12) // Padding aumentado
            }
        }

        fila.addView(createTableCell("Vuelta ${vuelta.numero}"))
        fila.addView(createTableCell(vuelta.tiempoVuelta))
        fila.addView(createTableCell(vuelta.tiempoTotal))

        tablaVueltas.addView(fila)
    }


    private fun limpiarTablaVueltas() {
        val childCount = tablaVueltas.childCount
        if (childCount > 1) { // Si hay más de una fila (la primera son los encabezados)
            tablaVueltas.removeViews(1, childCount - 1)
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
