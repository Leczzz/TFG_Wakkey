package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AppDatabase
import com.tema.wakkey.Database.JuegoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JuegosActivity : AppCompatActivity() {

    private lateinit var adapter: JuegoAdapter
    private lateinit var juegosList: List<JuegoEntity>

    // Inicialización de los elementos de la interfaz de usuario
    private lateinit var recyclerAlarmas: RecyclerView
    private lateinit var btnAlarma: Button
    private lateinit var btnJuegos: Button
    private lateinit var btnCronometro: Button
    private lateinit var btnCuentaAtras: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.juegos_main)

        // Inicializar las vistas
        recyclerAlarmas = findViewById(R.id.recyclerAlarmas)
        btnAlarma = findViewById(R.id.btnAlarma)
        btnJuegos = findViewById(R.id.btnJuegos)
        btnCronometro = findViewById(R.id.btnCronometro)
        btnCuentaAtras = findViewById(R.id.btnCuentaAtras)

        // Configurar el RecyclerView
        recyclerAlarmas.layoutManager = LinearLayoutManager(this)

        // Cargar juegos desde la base de datos
        lifecycleScope.launch {
            val juegos = cargarJuegosDesdeDB()
            juegosList = juegos
            adapter = JuegoAdapter(juegosList) { juego, dificultad ->
                abrirJuego(juego, dificultad)
            }
            recyclerAlarmas.adapter = adapter
        }

        // Configurar el menú inferior
        configurarMenuInferior()
    }

    private suspend fun cargarJuegosDesdeDB(): List<JuegoEntity> {
        return withContext(Dispatchers.IO) {
            val db = AppDatabase.getInstance(applicationContext)
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
                Log.d("JuegosActivity", "Juego no implementado: ${juego.nombre}")
            }
        }
    }

    private fun configurarMenuInferior() {
        btnAlarma.setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }
        btnJuegos.setOnClickListener {
            // Ya estás en esta Activity, no hace nada
        }
        btnCronometro.setOnClickListener {
            startActivity(Intent(this, CronometroActivity::class.java))
        }

    }
}
