package com.tema.wakkey

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tema.wakkey.Database.AppDatabase
import androidx.lifecycle.lifecycleScope
import com.tema.wakkey.Database.AlarmEntity
import kotlinx.coroutines.launch

class CrearAlarmaActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_alarma)

        val spinnerSonido = findViewById<Spinner>(R.id.spinnerSonido)

        val sonidosMap = mapOf(
            "Beep Beep " to R.raw.beepbepp,
            "Crystal Waters" to R.raw.crystalwaters,
            "Hawaii" to R.raw.hawai,
            "Lofi" to R.raw.lofi,
            "Morning" to R.raw.morning,
            "Piano" to R.raw.piano
        )

        spinnerSonido.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val sonidoSeleccionado = parent.getItemAtPosition(position).toString()
                val sonidoResId = sonidosMap[sonidoSeleccionado] ?: return

                if (::mediaPlayer.isInitialized) {
                    mediaPlayer.release()
                }

                mediaPlayer = MediaPlayer.create(this@CrearAlarmaActivity, sonidoResId)
                mediaPlayer.start()

                handler.postDelayed({
                    if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.release()
                    }
                }, 5000)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Referencias UI
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
            val nombre = etNombre.text.toString()
            val hora = etHora.text.toString()
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
                hora = hora,
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
                setResult(RESULT_OK)

                val intent = Intent(this@CrearAlarmaActivity, AlarmActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
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
