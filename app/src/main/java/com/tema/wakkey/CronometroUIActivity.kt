package com.tema.wakkey


import CronometroController
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CronometroUIActivity : AppCompatActivity() {
    private lateinit var cronometro: CronometroController
    private lateinit var txtTiempo: TextView
    private lateinit var tablaVueltas: TableLayout
    private lateinit var btnIniciar: Button
    private lateinit var btnPausar: Button
    private lateinit var btnReanudar: Button
    private lateinit var btnVuelta: Button
    private lateinit var btnDetener: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cronometro_main)

        txtTiempo = findViewById(R.id.txtTiempo)
        tablaVueltas = findViewById(R.id.tablaVueltas)

        // Inicialización del cronómetro
        cronometro = CronometroController { tiempoFormateado ->
            runOnUiThread {
                txtTiempo.text = tiempoFormateado
            }
        }

        // Inicializar botones
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausar = findViewById(R.id.btnPausar)
        btnReanudar = findViewById(R.id.btnReanudar)
        btnVuelta = findViewById(R.id.btnVuelta)
        btnDetener = findViewById(R.id.btnDetener)

        // Configurar botones
        configurarBotones()

        // Iniciar el cronómetro
        btnIniciar.setOnClickListener {
            cronometro.start()
            configurarBotones()
        }

        // Pausar el cronómetro
        btnPausar.setOnClickListener {
            cronometro.pause()
            configurarBotones()
        }

        // Reanudar el cronómetro
        btnReanudar.setOnClickListener {
            cronometro.resume()
            configurarBotones()
        }

        // Detener el cronómetro
        btnDetener.setOnClickListener {
            cronometro.reset()
            configurarBotones()
        }

        // Registrar vuelta
        btnVuelta.setOnClickListener {
            cronometro.registrarVuelta(tablaVueltas)
        }

        // Navegar a la actividad de Alarmas cuando el usuario toque el botón "Alarma"
        val btnAlarma: Button = findViewById(R.id.btnAlarma)
        btnAlarma.setOnClickListener {
            // Navegar a la actividad de alarmas
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }
    }
    // Metodo para configurar la visibilidad de los botones según el estado del cronómetro
    private fun configurarBotones() {
        if (cronometro.isRunning) {
            // Si el cronómetro está corriendo, ocultar "Iniciar" y mostrar "Pausar" y "Vuelta"
            btnIniciar.visibility = View.GONE
            btnPausar.visibility = View.VISIBLE
            btnVuelta.visibility = View.VISIBLE
            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
        } else if (cronometro.isPaused) {
            // Si el cronómetro está pausado, ocultar "Iniciar" y mostrar "Reanudar" y "Detener"
            btnIniciar.visibility = View.GONE
            btnPausar.visibility = View.GONE
            btnVuelta.visibility = View.GONE
            btnReanudar.visibility = View.VISIBLE
            btnDetener.visibility = View.VISIBLE
        } else {
            // Si el cronómetro está detenido, mostrar "Iniciar" y ocultar los demás botones
            btnIniciar.visibility = View.VISIBLE
            btnPausar.visibility = View.GONE
            btnVuelta.visibility = View.GONE
            btnReanudar.visibility = View.GONE
            btnDetener.visibility = View.GONE
        }
    }
}
