package com.tema.wakkey

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
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
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true
                    return
                }

                val sonidoSeleccionado = parent.getItemAtPosition(position).toString()
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

            if (!horaTexto.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
                Toast.makeText(this, "Formato de hora inválido. Usa HH:mm", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (horaNum, minutoNum) = horaTexto.split(":").map { it.toIntOrNull() ?: -1 }
            if (horaNum !in 0..23 || minutoNum !in 0..59) {
                Toast.makeText(this, "La hora debe estar entre 00:00 y 23:59", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val juego = spinnerJuego.selectedItem.toString()
            val dificultadTexto = spinnerDificultad.selectedItem.toString()
            val dificultad = when (dificultadTexto.lowercase()) {
                "fácil" -> 'F'
                "media" -> 'M'
                "difícil" -> 'D'
                else -> 'F'
            }

            val diasActivos = buildString {
                if (cbLunes.isChecked) append("L")
                if (cbMartes.isChecked) append("M")
                if (cbMiercoles.isChecked) append("X")
                if (cbJueves.isChecked) append("J")
                if (cbViernes.isChecked) append("V")
                if (cbSabado.isChecked) append("S")
                if (cbDomingo.isChecked) append("D")
            }

            val sonido = spinnerSonido.selectedItem.toString()

            val idJuego = when (juego) {
                "Despierta a Kkey" -> 1
                "Despeina a Kkey" -> 2
                "¡Resta!" -> 3
                "¡Suma!" -> 4
                "Scan Kkey" -> 5
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

            val db = AppDatabase.getInstance(applicationContext)

            lifecycleScope.launch {
                db.alarmDao().insertAlarm(nuevaAlarma)
                Log.d("CrearAlarma", "Alarma guardada: $nuevaAlarma")

                // === PROGRAMAR LA ALARMA ===
                val alarmTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, horaNum)
                    set(Calendar.MINUTE, minutoNum)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_MONTH, 1)
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
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime.timeInMillis,
                    pendingIntent
                )

                runOnUiThread {
                    Toast.makeText(this@CrearAlarmaActivity, "Alarma guardada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
