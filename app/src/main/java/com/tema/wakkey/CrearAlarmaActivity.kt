package com.tema.wakkey

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AlarmEntity
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.launch
import java.util.*

class CrearAlarmaActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private var isSpinnerInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_alarma)

        val spinnerSonido = findViewById<Spinner>(R.id.spinnerSonido)
        val sonidosMap = mapOf(
            "Crystal Waters" to R.raw.crystalwaters,
            "Hawaii" to R.raw.hawai,
            "Lofi" to R.raw.lofi,
            "Morning" to R.raw.morning,
            "Piano" to R.raw.piano
        )

        spinnerSonido.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true
                    return
                }

                val sonidoSeleccionado = parent?.getItemAtPosition(position).toString()
                val sonidoResId = sonidosMap[sonidoSeleccionado] ?: return

                if (::mediaPlayer.isInitialized) {
                    mediaPlayer.stop()
                    mediaPlayer.release()
                }

                mediaPlayer = MediaPlayer.create(this@CrearAlarmaActivity, sonidoResId)
                mediaPlayer.setOnPreparedListener {
                    it.start()
                    handler.postDelayed({
                        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                            mediaPlayer.stop()
                            mediaPlayer.release()
                        }
                    }, 5000)
                }

                handler.removeCallbacksAndMessages(null)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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

        btnCancelar.setOnClickListener {
            finish()
        }

        btnAnadir.setOnClickListener {
            val nombre = etNombre.text.toString().ifBlank { "Alarma sin nombre" }

            val horaTexto = etHora.text.toString()
            val minutoTexto = etMinutos.text.toString()

            val horaNum = horaTexto.toIntOrNull()
            val minutoNum = minutoTexto.toIntOrNull()

            if (horaNum == null || minutoNum == null || horaNum !in 0..23 || minutoNum !in 0..59) {
                Toast.makeText(
                    this,
                    "Hora inválida. Introduce valores entre 00–23 y 00–59",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val juego = spinnerJuego.selectedItem.toString()
            Log.d("CrearAlarma", "Juego seleccionado: $juego")  // Log para depurar el nombre del juego
            val dificultadTexto = spinnerDificultad.selectedItem.toString()
            Log.d("CrearAlarma", "Dificultad seleccionada: $dificultadTexto")

            val dificultad = when (dificultadTexto) {
                "Fácil" -> "F"
                "Medio" -> "M"
                "Difícil" -> "D"
                else -> "F"
            }


            // Convertir los días activos en texto
            val diasActivos = getTextoDias(
                cbLunes.isChecked,
                cbMartes.isChecked,
                cbMiercoles.isChecked,
                cbJueves.isChecked,
                cbViernes.isChecked,
                cbSabado.isChecked,
                cbDomingo.isChecked
            )

            val sonido = spinnerSonido.selectedItem.toString()

            val idJuego = when (juego) {
                "Despierta a Kkey" -> 1
                "Despeina a Kkey" -> 2
                "¡Resta!" -> 3
                "¡Suma!" -> 4
                "Scan Kkey" -> 5
                "Desactivar Manualmente" -> 6
                else -> 0
            }

            val nuevaAlarma = AlarmEntity(
                nombre = nombre,
                hora = "%02d:%02d".format(horaNum, minutoNum),
                idJuego = idJuego,
                dificultad = dificultad,
                diasActivos = diasActivos,
                esActivo = true,
                sonido = sonido
            )

            val now = Calendar.getInstance()
            val alarmTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, horaNum)
                set(Calendar.MINUTE, minutoNum)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // Si la hora ya ha pasado hoy, programa para mañana
                if (timeInMillis <= now.timeInMillis) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java).apply {
                putExtra("sonido", sonido)
                putExtra("idJuego", idJuego)
                putExtra("dificultad", dificultad)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                System.currentTimeMillis().toInt(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Para que las alarmas suenen correctamente, activa la opción 'Permitir alarmas exactas' en los ajustes de la app.")
                    .setPositiveButton("Ir a ajustes") { _, _ ->
                        val intent = Intent().apply {
                            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = android.net.Uri.fromParts("package", packageName, null)
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                lifecycleScope.launch {
                    AppDatabase.getInstance(applicationContext).alarmDao().insertAlarm(nuevaAlarma)
                    Log.d("CrearAlarma", "Alarma guardada: $nuevaAlarma")

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime.timeInMillis,
                        pendingIntent
                    )

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
