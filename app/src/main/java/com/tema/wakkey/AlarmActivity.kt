package com.tema.wakkey

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configura el menú inferior también aquí para mantener consistencia
        setupBottomMenu()

<<<<<<< Updated upstream
        // Implementa aquí la lógica específica de alarmas
=======
        adapter = AlarmAdapter(mutableListOf()) { alarma, isChecked ->
            lifecycleScope.launch {
                alarmDao.updateAlarm(alarma.copy(esActivo = isChecked))
                cargarAlarmas()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        cargarAlarmas()

        findViewById<ImageButton>(R.id.btnAddAlarma).setOnClickListener {
            startActivity(Intent(this, CrearAlarmaActivity::class.java))
        }

        findViewById<Button>(R.id.btnCronometro).setOnClickListener {
            startActivity(Intent(this, CronometroUIActivity::class.java))
        }

        findViewById<Button>(R.id.btnAlarma).setOnClickListener {}

        val btnMenuOpciones = findViewById<ImageButton>(R.id.btnMenuOpciones)
        btnMenuOpciones.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_opciones, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_ordenar -> {
                        ordenarAlarmas()
                        true
                    }
                    R.id.opcion_eliminar -> {
                        mostrarDialogoConfirmacion()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        when (intent.getStringExtra("accion")) {
            "ordenar" -> ordenarAlarmas()
            "eliminar" -> mostrarDialogoConfirmacion()
        }
>>>>>>> Stashed changes
    }

    private fun setupBottomMenu() {
        // Similar al de MainActivity pero marcando el botón activo
        findViewById<Button>(R.id.btnAlarma).setTextColor(Color.WHITE)
        findViewById<Button>(R.id.btnCronometro).setTextColor(Color.parseColor("#808080"))

<<<<<<< Updated upstream
        findViewById<Button>(R.id.btnJuegos).setOnClickListener {
            startActivity(Intent(this, JuegosActivity::class.java))
=======
    private fun cargarAlarmas() {
        lifecycleScope.launch {
            try {
                val alarmas = alarmDao.getAllAlarms()
                adapter.actualizarLista(alarmas)
            } catch (e: Exception) {
                Log.e("AlarmActivity", "Error al cargar alarmas", e)
            }
>>>>>>> Stashed changes
        }

<<<<<<< Updated upstream
    }
}
=======
    private fun ordenarAlarmas() {
        lifecycleScope.launch {
            try {
                val alarmasOrdenadas = alarmDao.getAllAlarmsOrderedByHour()
                adapter.actualizarLista(alarmasOrdenadas)
            } catch (e: Exception) {
                Log.e("AlarmActivity", "Error al ordenar alarmas", e)
            }
        }
    }

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar todas las alarmas")
            .setMessage("¿Estás segura de que quieres eliminar todas las alarmas?")
            .setPositiveButton("Sí") { _, _ ->
                lifecycleScope.launch {
                    try {
                        alarmDao.deleteAll()
                        adapter.actualizarLista(emptyList())
                    } catch (e: Exception) {
                        Log.e("AlarmActivity", "Error al eliminar alarmas", e)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.opcion_ordenar -> {
                ordenarAlarmas()
                true
            }
            R.id.opcion_eliminar -> {
                mostrarDialogoConfirmacion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
>>>>>>> Stashed changes
