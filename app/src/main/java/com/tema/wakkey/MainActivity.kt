package com.tema.wakkey

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.Database.JuegoDao
import com.tema.wakkey.Database.JuegoEntity

class MainActivity : AppCompatActivity() {

    private lateinit var cronometro: CronometroActivity //Cronómetro
    private lateinit var txtTiempo: TextView //Cronómetro
    private lateinit var btnIniciar: Button //Cronómetro
    private lateinit var btnPausar: Button //Cronómetro
    private lateinit var btnReiniciar: Button //Cronómetro
    private lateinit var btnReanudar: Button //Cronómetro
    private lateinit var btnDetener: Button //Cronómetro

    private lateinit var tablaVueltas: TableLayout //Cronómetro

    private var vueltas = mutableListOf<Vuelta>() //Cronómetro
    private var tiempoUltimaVuelta: Long = 0L //Cronómetro

    data class Vuelta(val numero: Int, val tiempoVuelta: String, val tiempoTotal: String) //Cronómetro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cronometro_main)


        //Para clase Cronómetro
        txtTiempo = findViewById(R.id.txtTiempo)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausar = findViewById(R.id.btnPausar)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnDetener = findViewById(R.id.btnDetener)
        tablaVueltas = findViewById(R.id.tablaVueltas)

        //Inicializa el cronómetro
        cronometro = CronometroActivity { tiempo ->
            runOnUiThread {
                txtTiempo.text = tiempo
            }
        }

        //Accion cuando se pulsa boton iniciar
        btnIniciar.setOnClickListener {
            cronometro.start()
            btnIniciar.visibility = View.GONE
            btnPausar.visibility = View.VISIBLE
            btnReiniciar.visibility = View.VISIBLE
            tiempoUltimaVuelta = 0L
            limpiarTablaVueltas()
        }

        //Accion cuando se pulsa boton pausar
        btnPausar.setOnClickListener {
            registrarVuelta()
        }

        //Accion cuando se pulsa boton reiniciar
        btnReiniciar.setOnClickListener {
            cronometro.pause()
            btnPausar.visibility = View.GONE
            btnReiniciar.visibility = View.GONE
            btnReanudar.visibility = View.VISIBLE
            btnDetener.visibility = View.VISIBLE
        }

        //Accion cuando se pulsa boton reanudar
        btnReanudar.setOnClickListener {
            cronometro.resume()
            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnPausar.visibility = View.VISIBLE
            btnReiniciar.visibility = View.VISIBLE
        }

        //Accion cuando se pulsa boton detener
        btnDetener.setOnClickListener {
            cronometro.reset() // Detener el cronómetro
            vueltas.clear() // Limpiar la lista de vueltas
            limpiarTablaVueltas() // Limpiar la tabla de vueltas

            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnIniciar.visibility = View.VISIBLE
        }
    }

    //Métodos para clase Cronómetro
    override fun onDestroy() {
        super.onDestroy()
        cronometro.reset() // Detener handler
    }

    //Metodo para registrar una vuelta
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

    //Metodo para agregar una fila a la tabla al dar vuelta
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
                textSize = 16f
                setTextColor(Color.BLACK)
                gravity = Gravity.CENTER
                setPadding(12, 12, 12, 12)
            }
        }

        fila.addView(createTableCell("Vuelta ${vuelta.numero}")) // Número de vuelta
        fila.addView(createTableCell(vuelta.tiempoVuelta)) // Tiempo de vuelta
        fila.addView(createTableCell(vuelta.tiempoTotal)) // Tiempo total

        tablaVueltas.addView(fila) // Agregar la fila a la tabla
    }

 //Metodo para limpiar la tabla de vueltas
    private fun limpiarTablaVueltas() {
        val childCount = tablaVueltas.childCount
        if (childCount > 1) { // Si hay más de una fila (la primera son los encabezados)
            tablaVueltas.removeViews(1, childCount - 1)
        }
    }

    //Metodo para formatear el tiempo
    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds) // Formato hh:mm:ss
        
    }

    //Metodo para insertar juegos si no existen
    suspend fun insertarJuegosSiNoExisten(juegoDao: JuegoDao) {
        val juegosExistentes = juegoDao.getAllJuegos()
        if (juegosExistentes.isEmpty()) {
            juegoDao.insertJuego(JuegoEntity(nombre = "Despeina a Kkey", descripcion = "Sopla al micro hasta llenar la barra de progreso", tieneDificultad = true, imagenResId = R.drawable.despeinakkey))
            juegoDao.insertJuego(JuegoEntity(nombre = "Despierta a Kkey", descripcion = "Agita el móvil hasta completar la barra de progreso", tieneDificultad = true, imagenResId = R.drawable.despiertaakkey))
            juegoDao.insertJuego(JuegoEntity(nombre = "Resta!", descripcion = "Resuelve 3 restas según el nivel de dificultad", tieneDificultad = true, imagenResId = R.drawable.resta))
            juegoDao.insertJuego(JuegoEntity(nombre = "Suma!", descripcion = "Resuelve 3 sumas según el nivel de dificultad", tieneDificultad = true, imagenResId = R.drawable.suma))
            juegoDao.insertJuego(JuegoEntity(nombre = "Scan Kkey", descripcion = "Escanea cualquier código de barras", tieneDificultad = false, imagenResId = R.drawable.scanner))
        }
    }

}
