package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AppDatabase
import com.tema.wakkey.Database.JuegoEntity
import kotlinx.coroutines.launch

// Esta clase se encarga de manejar la lógica y la interacción de la actividad de Juegos.
class JuegosActivity : AppCompatActivity() {

    private lateinit var recyclerJuegos: RecyclerView // Lista de juegos
    private lateinit var juegoAdapter: JuegoAdapter // Adaptador de juegos
    private lateinit var juegosList: List<JuegoEntity> // Lista de juegos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.juegos_main)

        recyclerJuegos = findViewById(R.id.recyclerJuegos) // Lista de juegos
        recyclerJuegos.layoutManager = LinearLayoutManager(this) // Configurar el layout

        // Botones para acciones
        findViewById<Button>(R.id.btnCronometro).setOnClickListener {
            startActivity(Intent(this, CronometroUIActivity::class.java))
        }

        findViewById<Button>(R.id.btnAlarma).setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }

        findViewById<Button>(R.id.btnCuentaAtras).setOnClickListener {
            startActivity(Intent(this, CuentaAtrasActivity::class.java))
        }

        findViewById<Button>(R.id.btnJuegos).setOnClickListener {}

        // Usar una coroutine para obtener los juegos desde la base de datos
        lifecycleScope.launch {
            try {
                // Obtener los juegos desde la base de datos usando Room
                val juegoDao = AppDatabase.getInstance(this@JuegosActivity).juegoDao()
                juegosList = juegoDao.getAllJuegos()

                juegoAdapter = JuegoAdapter(juegosList) { juego, dificultad ->
                    val intent = when (juego.nombre) { // Navegación entre actividades
                        "¡Suma!" -> {
                            val intent = Intent(this@JuegosActivity, SumaActivity::class.java)

                            // Normalizar la dificultad
                            val dificultadNormalizada = when (dificultad) {
                                "Fácil" -> "F"
                                "Media" -> "M"
                                "Difícil" -> "D"
                                else -> "F"
                            }

                            intent.putExtra("dificultad", dificultadNormalizada)
                            intent
                        }
                        "¡Resta!" -> {
                            val intent = Intent(this@JuegosActivity, RestaActivity::class.java)

                            // Normalizar la dificultad
                            val dificultadNormalizada = when (dificultad) {
                                "Fácil" -> "F"
                                "Media" -> "M"
                                "Difícil" -> "D"
                                else -> "F"
                            }

                            intent.putExtra("dificultad", dificultadNormalizada)
                            intent
                        }
                        "Despeina a Kkey" -> {
                            val intent = Intent(this@JuegosActivity, DespeinaActivity::class.java)

                            val dificultadNormalizada = when (dificultad) {
                                "Fácil" -> "F"
                                "Media" -> "M"
                                "Difícil" -> "D"
                                else -> "F"
                            }

                            intent.putExtra("dificultad", dificultadNormalizada)
                            intent
                        }
                        "Despierta a Kkey" -> {
                            val intent = Intent(this@JuegosActivity, DespiertaActivity::class.java)

                            val dificultadNormalizada = when (dificultad) {
                                "Fácil" -> "F"
                                "Media" -> "M"
                                "Difícil" -> "D"
                                else -> "F"
                            }

                            intent.putExtra("dificultad", dificultadNormalizada)
                            intent
                        }
                        "Scan Kkey" -> {
                            val intent = Intent(this@JuegosActivity, ScanActivity::class.java)
                            intent
                        }

                        else -> {
                            val intent = Intent(this@JuegosActivity, DetenerAlarmaActivity::class.java)
                            intent
                        }

                    }

                    // Si el intent es válido, iniciar la actividad
                    intent.let { startActivity(it) }
                }

                recyclerJuegos.adapter = juegoAdapter // Configurar el adaptador
            } catch (e: Exception) {
                Toast.makeText(this@JuegosActivity, "Error al cargar los juegos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
