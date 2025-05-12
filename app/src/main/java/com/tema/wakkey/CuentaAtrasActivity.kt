package com.tema.wakkey

import android.content.Intent
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.java

class CuentaAtrasActivity : AppCompatActivity() {

    private lateinit var editTiempo: EditText
    private lateinit var btnIniciar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnParar: Button
    private lateinit var btnReanudar: Button
    private lateinit var txtHoraCuentaAtras: TextView
    private lateinit var iconoCampana: ImageView

    private var timer: CountDownTimer? = null
    private var tiempoRestante: Long = 0
    private var isRunning = false
    private var isPaused = false
    private lateinit var btnCronometro: Button
    private lateinit var btnJuegos: Button
    private lateinit var btnAlarmas: Button
    private lateinit var btnCuentaAtras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cuenta_atras_main)

        editTiempo = findViewById(R.id.editTiempo)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnParar = findViewById(R.id.btnParar)
        btnReanudar = findViewById(R.id.btnReanudar)
        txtHoraCuentaAtras = findViewById(R.id.txtHoracuentatras)
        iconoCampana = findViewById(R.id.iconoCampana)



            // Inicialización de los botones
            btnCronometro = findViewById(R.id.btnCronometro)
            btnJuegos = findViewById(R.id.btnJuegos)
            btnAlarmas = findViewById(R.id.btnAlarma)
            btnCuentaAtras = findViewById(R.id.btnCuentaAtras)

            // Configuración de los botones para redirigir a las demás actividades
            btnCronometro.setOnClickListener {
                // Redirige a la actividad del cronómetro
                startActivity(Intent(this, CronometroUIActivity::class.java))
            }

            btnJuegos.setOnClickListener {
                // Redirige a la actividad de juegos
                startActivity(Intent(this, JuegosActivity::class.java))
            }

            btnAlarmas.setOnClickListener {
                // Redirige a la actividad de alarmas
                startActivity(Intent(this, AlarmActivity::class.java))
            }

            btnCuentaAtras.setOnClickListener {
                // Redirige a la actividad de cuenta atrás
                startActivity(Intent(this, CuentaAtrasActivity::class.java))
            }
        btnIniciar.setOnClickListener {
            val tiempo = editTiempo.text.toString()
            if (tiempo.isNotEmpty()) {
                val partes = tiempo.split(":")
                if (partes.size == 2 || partes.size == 3) {
                    val horas = partes[0].toIntOrNull() ?: 0
                    val minutos = partes[1].toIntOrNull() ?: 0
                    val segundos = if (partes.size == 3) partes[2].toIntOrNull() ?: 0 else 0

                    tiempoRestante = ((horas * 3600 + minutos * 60 + segundos) * 1000).toLong()

                    iniciarCuentaAtras()
                } else {
                    Toast.makeText(this, "Formato incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnEliminar.setOnClickListener {
            // Detener el temporizador si está corriendo
            if (isRunning) {
                timer?.cancel()
                isRunning = false
                isPaused = false
            }

            // Restablecer el tiempo a cero y actualizar la vista
            tiempoRestante = 0
            actualizarTiempoRestante()

            // Volver a habilitar el EditText para permitir al usuario ingresar un nuevo tiempo
            editTiempo.isEnabled = true
            editTiempo.text.clear()  // Limpiar el campo de texto

            // Volver a la configuración predeterminada de botones
            btnIniciar.visibility = Button.VISIBLE
            btnEliminar.visibility = Button.GONE
            btnParar.visibility = Button.GONE
            btnReanudar.visibility = Button.GONE

            // Limpiar el texto de la hora final
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

        // Si el temporizador no está corriendo, mostramos el botón "Iniciar" y ocultamos los demás
        if (!isRunning) {
            btnIniciar.visibility = Button.VISIBLE
            btnEliminar.visibility = Button.GONE
            btnParar.visibility = Button.GONE
            btnReanudar.visibility = Button.GONE
        } else {
            // Si el temporizador está corriendo, ocultamos el botón "Iniciar" y mostramos los demás
            btnIniciar.visibility = Button.GONE
            btnEliminar.visibility = Button.VISIBLE
            btnParar.visibility = Button.VISIBLE
            btnReanudar.visibility = Button.GONE
        }
    }

    private fun iniciarCuentaAtras() {
        timer = object : CountDownTimer(tiempoRestante, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tiempoRestante = millisUntilFinished
                actualizarTiempoRestante()
            }

            override fun onFinish() {
                Toast.makeText(this@CuentaAtrasActivity, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                detenerCuentaAtras()

                // Llamar a la actividad DesactivarActivity cuando termine el contador
                val intent = Intent(this@CuentaAtrasActivity, DetenerAlarmaActivity::class.java)
                startActivity(intent)

            }

        }
        timer?.start()
        isRunning = true
        isPaused = false
        btnIniciar.visibility = Button.GONE
        btnEliminar.visibility = Button.VISIBLE
        btnParar.visibility = Button.VISIBLE
        btnReanudar.visibility = Button.GONE

        // Mostrar la hora a la que va a sonar la cuenta atrás
        mostrarHoraFinal()
    }

    private fun detenerCuentaAtras() {
        timer?.cancel()
        isRunning = false
        isPaused = true
        btnIniciar.visibility = Button.GONE
        btnEliminar.visibility = Button.GONE
        btnParar.visibility = Button.GONE
        btnReanudar.visibility = Button.VISIBLE

        // Mostrar la hora a la que va a sonar la cuenta atrás al detener
        mostrarHoraFinal()
    }

    private fun reanudarCuentaAtras() {
        // Reanudar el contador
        iniciarCuentaAtras()
    }

    private fun actualizarTiempoRestante() {
        val horas = (tiempoRestante / 1000) / 3600
        val minutos = (tiempoRestante / 1000 % 3600) / 60
        val segundos = (tiempoRestante / 1000 % 60)

        val tiempoFormateado = String.format("%02d:%02d:%02d", horas, minutos, segundos)
        editTiempo.setText(tiempoFormateado)
    }

    private fun mostrarHoraFinal() {
        // Obtener la hora actual
        val calendario = Calendar.getInstance()
        calendario.timeInMillis = System.currentTimeMillis()

        // Sumar el tiempo restante
        calendario.add(Calendar.MILLISECOND, tiempoRestante.toInt())

        // Formatear la hora de la cuenta atrás
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaFinal = sdf.format(calendario.time)

        // Mostrar la hora final en el TextView
        txtHoraCuentaAtras.text = "$horaFinal"
    }
}
