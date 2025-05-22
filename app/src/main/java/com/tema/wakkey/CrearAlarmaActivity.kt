package com.tema.wakkey

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AlarmEntity
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.launch
import java.util.*

//Esta clase sirve para crear una nueva alarma
class CrearAlarmaActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper()) // Manejador de mensajes
    private var isSpinnerInitialized = false // Bandera para verificar si el Spinner se ha inicializado

    override fun onCreate(savedInstanceState: Bundle?) { // Crea la actividad
        super.onCreate(savedInstanceState) // Llama al onCreate de la superclase
        setContentView(R.layout.crear_alarma) // Establece la vista de la actividad

        val spinnerSonido = findViewById<Spinner>(R.id.spinnerSonido) // Spinner para seleccionar el sonido
        val sonidosMap = mapOf( // Mapeo de sonidos con sus respectivos recursos
            "Crystal Waters" to R.raw.crystalwaters,
            "Hawaii" to R.raw.hawai,
            "Lofi" to R.raw.lofi,
            "Morning" to R.raw.morning,
            "Piano" to R.raw.piano
        )

        spinnerSonido.onItemSelectedListener = object : AdapterView.OnItemSelectedListener { // Escuchador para el Spinner
            override fun onItemSelected( // Maneja la selección de un elemento
                parent: AdapterView<*>?, // Vista del spinner
                view: View?, // Vista de la selección
                position: Int, // Posición del elemento seleccionado
                id: Long // Id del elemento seleccionado
            ) {
                if (!isSpinnerInitialized) { // Si el Spinner no se ha inicializado, lo inicializa
                    isSpinnerInitialized = true
                    return
                }

                val sonidoSeleccionado = parent?.getItemAtPosition(position).toString() // Obtiene el sonido seleccionado
                val sonidoResId = sonidosMap[sonidoSeleccionado] ?: return // Obtiene el recurso de sonido correspondiente

                AlarmSoundPlayer.stop() // Para cualquier sonido que pueda estar reproduciendo
                AlarmSoundPlayer.start(this@CrearAlarmaActivity, sonidoResId) // Reproduce el sonido seleccionado

                handler.postDelayed({ // Despues de 5 segundos, para el sonido
                    AlarmSoundPlayer.stop() //Llama al metodo stop para parar el sonido
                }, 5000)

                handler.removeCallbacksAndMessages(null) // Elimina cualquier mensaje en cola
            }

            override fun onNothingSelected(parent: AdapterView<*>) {} // Maneja la ausencia de selección
        }

        // Variables
        val etNombre = findViewById<EditText>(R.id.etNombreAlarma)
        val etHora = findViewById<EditText>(R.id.etHora)
        val etMinutos = findViewById<EditText>(R.id.etMinutos)
        val spinnerJuego = findViewById<Spinner>(R.id.spinnerJuego)
        val spinnerDificultad = findViewById<Spinner>(R.id.spinnerDificultad)

        val cbLunes = findViewById<CheckBox>(R.id.cbLunes)
        val cbMartes = findViewById<CheckBox>(R.id.cbMartes)
        val cbMiercoles = findViewById<CheckBox>(R.id.cbMiercoles)
        val cbJueves = findViewById<CheckBox>(R.id.cbJueves)
        val cbViernes = findViewById<CheckBox>(R.id.cbViernes)
        val cbSabado = findViewById<CheckBox>(R.id.cbSabado)
        val cbDomingo = findViewById<CheckBox>(R.id.cbDomingo)

        val btnAnadir = findViewById<Button>(R.id.btnAnadir)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnCancelar.setOnClickListener { // Maneja el clic en el botón de cancelar
            AlarmSoundPlayer.stop()
            finish()
        }

        btnAnadir.setOnClickListener { // Maneja el clic en el botón de añadir
            AlarmSoundPlayer.stop() // Para cualquier sonido que pueda estar reproduciendo

            val nombre = etNombre.text.toString().ifBlank { "Alarma sin nombre" } // Obtiene el nombre de la alarma
            val horaTexto = etHora.text.toString() // Obtiene la hora
            val minutoTexto = etMinutos.text.toString() // Obtiene los minutos
            val horaNum = horaTexto.toIntOrNull() // Convierte a entero
            val minutoNum = minutoTexto.toIntOrNull() // Convierte a entero

            if (horaNum == null || minutoNum == null || horaNum !in 0..23 || minutoNum !in 0..59) { // Valida la hora si no esta entre 00 y 23 y 00 y 59
                Toast.makeText(
                    this,
                    "Hora inválida. Introduce valores entre 00–23 y 00–59",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val juego = spinnerJuego.selectedItem.toString() // Obtiene el juego seleccionado
            val dificultadTexto = spinnerDificultad.selectedItem.toString() // Obtiene la dificultad seleccionada

            val dificultad = when (dificultadTexto) { // Convierte la dificultad a una letra
                "Fácil" -> "F"
                "Medio" -> "M"
                "Difícil" -> "D"
                else -> "F"
            }

            val diasActivos = getTextoDias( // Obtiene los días activos en texto
                cbLunes.isChecked,
                cbMartes.isChecked,
                cbMiercoles.isChecked,
                cbJueves.isChecked,
                cbViernes.isChecked,
                cbSabado.isChecked,
                cbDomingo.isChecked
            )

            val sonido = spinnerSonido.selectedItem.toString() // Obtiene el sonido seleccionado

            val idJuego = when (juego) { // Convierte el juego a un entero
                "Despierta a Kkey" -> 1
                "Despeina a Kkey" -> 2
                "¡Resta!" -> 3
                "¡Suma!" -> 4
                "Scan Kkey" -> 5
                "Desactivar Manualmente" -> 6
                else -> 0
            }

            val nuevaAlarma = AlarmEntity( // Crea una nueva alarma
                nombre = nombre, // Nombre de la alarma
                hora = "%02d:%02d".format(horaNum, minutoNum), // Hora de la alarma
                idJuego = idJuego, // ID del juego
                dificultad = dificultad,// Dificultad de la alarma
                diasActivos = diasActivos, // Días activos de la alarma
                esActivo = true, // Alarma está activa
                sonido = sonido // Sonido de la alarma
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Si la versión de Android es S o superior
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager // Servicio de alarmas
                if (!alarmManager.canScheduleExactAlarms()) { // Si no se puede programar alarmas exactas
                    AlertDialog.Builder(this) // Muestra un cuadro de diálogo
                        .setTitle("Permiso requerido") // Título del cuadro de diálogo
                        .setMessage("Para que las alarmas suenen correctamente, activa la opción 'Permitir alarmas exactas' en los ajustes de la app.")
                        .setPositiveButton("Ir a ajustes") { _, _ ->
                            val intent = Intent().apply {
                                action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS // Acción para ir a ajustes de la app
                                data = android.net.Uri.fromParts("package", packageName, null) // Datos de la app
                            }
                            startActivity(intent) // Inicia la actividad de ajustes
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                    return@setOnClickListener
                }
            }

            lifecycleScope.launch { //
                val alarmDao = AppDatabase.getInstance(applicationContext).alarmDao() // DAO de alarmas
                val idGenerado = alarmDao.insertAlarm(nuevaAlarma).toInt() // Inserta la alarma y obtiene el ID generado

                val diasSeleccionados = diasActivos.split(" ") // Divide los días activos en una lista
                val diasMap = mapOf( // Mapeo de días con sus respectivos valores
                    "L" to Calendar.MONDAY,
                    "M" to Calendar.TUESDAY,
                    "X" to Calendar.WEDNESDAY,
                    "J" to Calendar.THURSDAY,
                    "V" to Calendar.FRIDAY,
                    "S" to Calendar.SATURDAY,
                    "D" to Calendar.SUNDAY
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager // Servicio de alarmas

                for (dia in diasSeleccionados) { // Programa una alarma para cada día seleccionado
                    val calendar = Calendar.getInstance().apply { // Crea una instancia de Calendar
                        set(Calendar.HOUR_OF_DAY, horaNum) // Configura la hora
                        set(Calendar.MINUTE, minutoNum) // Configura los minutos
                        set(Calendar.SECOND, 0) // Configura los segundos
                        set(Calendar.MILLISECOND, 0) // Configura los milisegundos
                        set(Calendar.DAY_OF_WEEK, diasMap[dia]!!)// Configura el día de la semana

                        // Si el día ya pasó esta semana, programa para la próxima semana
                        if (before(Calendar.getInstance())) {
                            add(Calendar.WEEK_OF_YEAR, 1)
                        }
                    }

                    val alarmIntentDia = Intent(applicationContext, AlarmReceiver::class.java).apply { // Crea un intent para el receiver
                        putExtra("sonido", sonido) // Pasa el sonido
                        putExtra("idJuego", idJuego) // Pasa el ID del juego
                        putExtra("dificultad", dificultad) // Pasa la dificultad
                        putExtra("idAlarma", idGenerado) // Pasa el ID de la alarma
                    }

                    val pendingIntentDia = PendingIntent.getBroadcast( // Crea un PendingIntent para la alarma
                        applicationContext, // Contexto de la aplicación
                        "$idGenerado${diasMap[dia]}".toInt(), // ID único para la alarma
                        alarmIntentDia,// Intent de la alarma
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Actualiza la actividad existente
                    )

                    alarmManager.setExactAndAllowWhileIdle( // Programa la alarma
                        AlarmManager.RTC_WAKEUP, // Tipo de wakeup
                        calendar.timeInMillis, // Fecha y hora de la alarma
                        pendingIntentDia // PendingIntent de la alarma
                    )
                }

                runOnUiThread {
                    Toast.makeText(
                        this@CrearAlarmaActivity,
                        "Alarma guardada correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    // Función para convertir los días activos en texto
    private fun getTextoDias(
        lunes: Boolean,
        martes: Boolean,
        miercoles: Boolean,
        jueves: Boolean,
        viernes: Boolean,
        sabado: Boolean,
        domingo: Boolean
    ): String {
        val dias = listOf("L", "M", "X", "J", "V", "S", "D")
        return listOf(lunes, martes, miercoles, jueves, viernes, sabado, domingo)
            .mapIndexedNotNull { index, isChecked ->
                if (isChecked) dias[index] else null
            }
            .joinToString(" ")
    }
}
