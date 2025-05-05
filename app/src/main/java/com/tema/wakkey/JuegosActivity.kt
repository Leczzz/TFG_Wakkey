package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tema.wakkey.Database.AppDatabase
import com.tema.wakkey.Database.JuegoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JuegosActivity : AppCompatActivity() {

    private lateinit var adapter: JuegoAdapter
    private lateinit var juegosList: List<JuegoEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.juegos_main)

        r

        // Cargar juegos desde Room
        lifecycleScope.launch {
            val juegos = cargarJuegosDesdeDB()
            juegosList = juegos
            adapter = JuegoAdapter(juegosList) { juego, dificultad ->
                abrirJuego(juego, dificultad)
            }
            recyclerAlarmas.adapter = adapter
        }

        configurarMenuInferior()
    }

    private suspend fun cargarJuegosDesdeDB(): List<JuegoEntity> {
        return withContext(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            db.juegoDao().getAllJuegos()
        }
    }

    private fun abrirJuego(juego: JuegoEntity, dificultad: String) {
        when (juego.nombre) {
            "¡Suma!" -> {
                val intent = Intent(this, SumaActivity::class.java)
                intent.putExtra("dificultad", dificultad)
                startActivity(intent)
            }
            // Puedes añadir más juegos aquí con más condiciones
            else -> {
                // Opción por defecto
                // Puedes poner un Toast si aún no está implementado
            }
        }
    }

    private fun configurarMenuInferior() {
        btnAlarma.setOnClickListener {
            startActivity(Intent(this, AlarmasActivity::class.java))
        }
        btnJuegos.setOnClickListener {
            // Ya estás en esta Activity, no hace nada
        }
        btnCronometro.setOnClickListener {
            startActivity(Intent(this, CronometroActivity::class.java))
        }
        btnCuentaAtras.setOnClickListener {
            startActivity(Intent(this, CuentaAtrasActivity::class.java))
        }
    }
}
