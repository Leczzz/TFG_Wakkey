package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AppDatabase
import com.tema.wakkey.Database.JuegoEntity
import kotlinx.coroutines.launch

class JuegoListActivity : AppCompatActivity() {

    private lateinit var recyclerJuegos: RecyclerView
    private lateinit var juegoAdapter: JuegoAdapter
    private lateinit var juegosList: List<JuegoEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.juegos_main)

        recyclerJuegos = findViewById(R.id.recyclerJuegos)
        recyclerJuegos.layoutManager = LinearLayoutManager(this)

        // Cargar los juegos desde la base de datos
        lifecycleScope.launch {
            try {
                val juegoDao = AppDatabase.getInstance(this@JuegoListActivity).juegoDao()
                juegosList = juegoDao.getAllJuegos()

                // Mostrar los juegos por consola (debug)
                Log.d("JuegoListActivity", "Juegos encontrados: ${juegosList.joinToString { it.nombre }}")

                // Crear el adaptador y asignarlo al RecyclerView
                juegoAdapter = JuegoAdapter(juegosList) { juego, dificultad ->
                    when (juego.nombre) {
                        "¡Suma!" -> {
                            val intent = Intent(this@JuegoListActivity, SumaActivity::class.java)
                            intent.putExtra("juego", juego)
                            intent.putExtra("dificultad", dificultad)
                            startActivity(intent)
                        }
                        "¡Resta!" -> {
                            val intent = Intent(this@JuegoListActivity, RestaActivity::class.java)
                            intent.putExtra("juego", juego)
                            intent.putExtra("dificultad", dificultad)
                            startActivity(intent)
                        }

                        "Despeina a Kkey" -> {
                            val intent = Intent(this@JuegoListActivity, DespeinaActivity::class.java)
                            intent.putExtra("juego", juego)
                            intent.putExtra("dificultad", dificultad)
                            startActivity(intent)
                        }
                        "Despierta a Kkey" -> {
                            val intent = Intent(this@JuegoListActivity, DespiertaActivity::class.java)
                            intent.putExtra("juego", juego)
                            intent.putExtra("dificultad", dificultad)
                            startActivity(intent)
                        }

                        else -> {
                            Toast.makeText(
                                this@JuegoListActivity,
                                "Juego no reconocido: ${juego.nombre}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                recyclerJuegos.adapter = juegoAdapter

            } catch (e: Exception) {
                Log.e("JuegoListActivity", "Error al cargar juegos", e)
                Toast.makeText(this@JuegoListActivity, "Error al cargar los juegos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
