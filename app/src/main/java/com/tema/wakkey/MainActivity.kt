package com.tema.wakkey


import android.os.Bundle

import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.Database.JuegoDao
import com.tema.wakkey.Database.JuegoEntity


class MainActivity : AppCompatActivity() {

    private lateinit var cronometro: CronometroController // Cronómetro
    private lateinit var txtTiempo: TextView // Cronómetro
    private lateinit var btnIniciar: Button // Cronómetro
    private lateinit var btnParar: Button // Cronómetro
    private lateinit var btnVuelta: Button // Cronómetro
    private lateinit var btnReanudar: Button // Cronómetro
    private lateinit var btnDetener: Button // Cronómetro
    private lateinit var tablaVueltas: TableLayout // Cronómetro

    private var vueltas = mutableListOf<CronometroController.Vuelta>() // Cronómetro
    private var tiempoUltimaVuelta: Long = 0L // Cronómetro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cronometro_main)

        // Inicializa las vistas del cronómetro
        txtTiempo = findViewById(R.id.txtTiempo)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnParar = findViewById(R.id.btnParar)
        btnVuelta = findViewById(R.id.btnVuelta)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnDetener = findViewById(R.id.btnDetener)
        tablaVueltas = findViewById(R.id.tablaVueltas)

        // Inicializa el cronómetro
        cronometro = CronometroController { tiempo ->
            runOnUiThread {
                txtTiempo.text = tiempo
            }
        }

        // Acción cuando se pulsa el botón Iniciar
        btnIniciar.setOnClickListener {
            cronometro.start()
            btnIniciar.visibility = View.GONE
            btnParar.visibility = View.VISIBLE
            btnVuelta.visibility = View.VISIBLE
            tiempoUltimaVuelta = 0L
            limpiarTablaVueltas()
        }

        // Acción cuando se pulsa el botón Parar
        btnParar.setOnClickListener {
            cronometro.registrarVuelta(tablaVueltas)
        }

        // Acción cuando se pulsa el botón Vuelta
        btnVuelta.setOnClickListener {
            cronometro.pause()
            btnParar.visibility = View.GONE
            btnVuelta.visibility = View.GONE
            btnReanudar.visibility = View.VISIBLE
            btnDetener.visibility = View.VISIBLE
        }

        // Acción cuando se pulsa el botón Reanudar
        btnReanudar.setOnClickListener {
            cronometro.resume()
            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnParar.visibility = View.VISIBLE
            btnVuelta.visibility = View.VISIBLE
        }

        // Acción cuando se pulsa el botón Detener
        btnDetener.setOnClickListener {
            cronometro.reset() // Detener el cronómetro
            vueltas.clear() // Limpiar la lista de vueltas
            limpiarTablaVueltas() // Limpiar la tabla de vueltas

            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
            btnIniciar.visibility = View.VISIBLE
        }



    }

    // Metodo para limpiar la tabla de vueltas
    private fun limpiarTablaVueltas() {
        val childCount = tablaVueltas.childCount
        if (childCount > 1) { // Si hay más de una fila (la primera son los encabezados)
            tablaVueltas.removeViews(1, childCount - 1)
        }
    }

    // Metodo para insertar juegos si no existen
    private suspend fun insertarJuegosSiNoExisten(juegoDao: JuegoDao) {
        val juegosExistentes = juegoDao.getAllJuegos()
        if (juegosExistentes.isEmpty()) {
            juegoDao.insertJuego(JuegoEntity(nombre = "Despeina a Kkey", descripcion = "Sopla al micro hasta llenar la barra de progreso", tieneDificultad = true, imagenResId = R.drawable.despeinakkey))
            juegoDao.insertJuego(JuegoEntity(nombre = "Despierta a Kkey", descripcion = "Agita el móvil hasta completar la barra de progreso", tieneDificultad = true, imagenResId = R.drawable.despiertakkey))
            juegoDao.insertJuego(JuegoEntity(nombre = "Resta!", descripcion = "Resuelve 3 restas según el nivel de dificultad", tieneDificultad = true, imagenResId = R.drawable.resta))
            juegoDao.insertJuego(JuegoEntity(nombre = "Suma!", descripcion = "Resuelve 3 sumas según el nivel de dificultad", tieneDificultad = true, imagenResId = R.drawable.suma))
        }
    }
}
