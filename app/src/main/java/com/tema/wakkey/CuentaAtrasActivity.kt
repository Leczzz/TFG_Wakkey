package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

//Se encarga de manejar la lógica y la interacción de la actividad de la cuenta atrás.
class CuentaAtrasActivity : AppCompatActivity() {

    private lateinit var etHoras: EditText
    private lateinit var etMinutos: EditText
    private lateinit var etSegundos: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnParar: Button
    private lateinit var btnReanudar: Button
    private lateinit var txtHoraCuentaAtras: TextView
    private lateinit var iconoCampana: ImageView

    private lateinit var btnCronometro: Button
    private lateinit var btnJuegos: Button
    private lateinit var btnAlarmas: Button
    private lateinit var btnCuentaAtras: Button

    private var timer: CountDownTimer? = null
    private var tiempoRestante: Long = 0
    private var isRunning = false
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta_atras_main)

        // Referencias a los EditText individuales
        etHoras = findViewById(R.id.etHoras)
        etMinutos = findViewById(R.id.etMinutos)
        etSegundos = findViewById(R.id.etSegundos)

        btnIniciar = findViewById(R.id.btnIniciar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnParar = findViewById(R.id.btnParar)
        btnReanudar = findViewById(R.id.btnReanudar)
        txtHoraCuentaAtras = findViewById(R.id.txtHoracuentatras)
        iconoCampana = findViewById(R.id.iconoCampana)

        btnCronometro = findViewById(R.id.btnCronometro)
        btnJuegos = findViewById(R.id.btnJuegos)
        btnAlarmas = findViewById(R.id.btnAlarma)
        btnCuentaAtras = findViewById(R.id.btnCuentaAtras)

        btnCronometro.setOnClickListener {
            startActivity(Intent(this, CronometroUIActivity::class.java))
        }

        btnJuegos.setOnClickListener {
            startActivity(Intent(this, JuegosActivity::class.java))
        }

        btnAlarmas.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

        btnCuentaAtras.setOnClickListener {
            startActivity(Intent(this, CuentaAtrasActivity::class.java))
        }

        btnIniciar.setOnClickListener { // Maneja el clic en el botón de iniciar
            val horas = etHoras.text.toString().toIntOrNull() ?: 0
            val minutos = etMinutos.text.toString().toIntOrNull() ?: 0
            val segundos = etSegundos.text.toString().toIntOrNull() ?: 0

            if (horas == 0 && minutos == 0 && segundos == 0) { // Si no se ha ingresado ningún tiempo
                Toast.makeText(this, "Introduce un tiempo válido", Toast.LENGTH_SHORT).show()
            } else {
                tiempoRestante = ((horas * 3600 + minutos * 60 + segundos) * 1000).toLong() // Calcula el tiempo en milisegundos
                iniciarCuentaAtras() // Inicia la cuenta atrás
            }
        }

        btnEliminar.setOnClickListener {
            if (isRunning) {
                timer?.cancel()
                isRunning = false
                isPaused = false
            }

            tiempoRestante = 0
            actualizarTiempoRestante()

            etHoras.setText("")
            etMinutos.setText("")
            etSegundos.setText("")

            etHoras.isEnabled = true
            etMinutos.isEnabled = true
            etSegundos.isEnabled = true

            btnIniciar.visibility = Button.VISIBLE
            btnEliminar.visibility = Button.GONE
            btnParar.visibility = Button.GONE
            btnReanudar.visibility = Button.GONE

            txtHoraCuentaAtras.text = ""
        }

        btnParar.setOnClickListener {
            detenerCuentaAtras()
        }

        btnReanudar.setOnClickListener {
            reanudarCuentaAtras()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isRunning) {
            btnIniciar.visibility = Button.VISIBLE
            btnEliminar.visibility = Button.GONE
            btnParar.visibility = Button.GONE
            btnReanudar.visibility = Button.GONE
        } else {
            btnIniciar.visibility = Button.GONE
            btnEliminar.visibility = Button.VISIBLE
            btnParar.visibility = Button.VISIBLE
            btnReanudar.visibility = Button.GONE
        }
    }

    private fun iniciarCuentaAtras() { // Inicia la cuenta atrás
        timer = object : CountDownTimer(tiempoRestante, 1000) { // Crea un temporizador
            override fun onTick(millisUntilFinished: Long) { // Se ejecuta cada segundo
                tiempoRestante = millisUntilFinished // Actualiza el tiempo restante
                actualizarTiempoRestante() // Actualiza la interfaz de usuario con el tiempo restante
            }

            override fun onFinish() { // Se ejecuta cuando el tiempo llega a cero
                Toast.makeText(this@CuentaAtrasActivity, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                detenerCuentaAtras()
                startActivity(Intent(this@CuentaAtrasActivity, DetenerAlarmaActivity::class.java))
            }
        }

        timer?.start() // Inicia el temporizador
        isRunning = true // Marca la cuenta como activa
        isPaused = false // Marca la cuenta como no pausada

        btnIniciar.visibility = Button.GONE
        btnEliminar.visibility = Button.VISIBLE
        btnParar.visibility = Button.VISIBLE
        btnReanudar.visibility = Button.GONE

        mostrarHoraFinal() // Muestra la hora final en la interfaz de usuario
    }

    private fun detenerCuentaAtras() { // Detiene la cuenta atrás
        timer?.cancel() // Cancela el temporizador
        isRunning = false // Marca la cuenta como inactiva
        isPaused = true // Marca la cuenta como pausada

        btnIniciar.visibility = Button.GONE
        btnEliminar.visibility = Button.GONE
        btnParar.visibility = Button.GONE
        btnReanudar.visibility = Button.VISIBLE

        mostrarHoraFinal() // Muestra la hora final en la interfaz de usuario
    }

    private fun reanudarCuentaAtras() {
        iniciarCuentaAtras() // Inicia la cuenta atrás nuevamente
    }

    private fun actualizarTiempoRestante() { // Actualiza la interfaz de usuario con el tiempo restante
        val horas = (tiempoRestante / 1000) / 3600
        val minutos = (tiempoRestante / 1000 % 3600) / 60
        val segundos = (tiempoRestante / 1000 % 60)

        etHoras.setText(String.format("%02d", horas)) // Formatea y muestra las horas
        etMinutos.setText(String.format("%02d", minutos)) // Formatea y muestra los minutos
        etSegundos.setText(String.format("%02d", segundos)) // Formatea y muestra los segundos
    }

    private fun mostrarHoraFinal() {
        // Calcula la hora final
        val calendario = Calendar.getInstance() // Crea un objeto de calendario
        calendario.timeInMillis = System.currentTimeMillis() // Establece la hora actual como inicio
        calendario.add(Calendar.MILLISECOND, tiempoRestante.toInt()) // Agrega el tiempo restante al inicio

        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaFinal = sdf.format(calendario.time) // Formatea la hora final
        txtHoraCuentaAtras.text = horaFinal // Muestra la hora final en la interfaz de usuario
    }
}
