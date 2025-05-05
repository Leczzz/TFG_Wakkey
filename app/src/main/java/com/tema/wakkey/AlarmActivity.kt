package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AlarmDao
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.launch

class AlarmActivity : AppCompatActivity() {
    private lateinit var alarmDao: AlarmDao
    private lateinit var adapter: AlarmAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerAlarmas)
        val db = AppDatabase.getInstance(this)
        alarmDao = db.alarmDao()

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
    }

    override fun onResume() {
        super.onResume()
        cargarAlarmas()
    }

    private fun cargarAlarmas() {
        lifecycleScope.launch {
            try {
                val alarmas = alarmDao.getAllAlarms()
                adapter.actualizarLista(alarmas)
            } catch (e: Exception) {
                Log.e("AlarmActivity", "Error al cargar alarmas", e)
            }
        }
    }

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
