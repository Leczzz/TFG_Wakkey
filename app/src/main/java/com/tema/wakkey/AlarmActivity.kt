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
    private lateinit var alarmDao: AlarmDao // DAO para las alarmas
    private lateinit var adapter: AlarmAdapter // Adaptador para el RecyclerView
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar las alarmas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Llama al onCreate de la superclase
        setContentView(R.layout.activity_main) // Establece la vista de la actividad

        // 1. Inicializar base de datos y DAO primero
        val db = AppDatabase.getInstance(this)
        alarmDao = db.alarmDao()

        // 2. Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerAlarmas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Crear adapter con acceso al DAO ya inicializado
        adapter = AlarmAdapter(mutableListOf()) { alarma, isChecked ->
            lifecycleScope.launch {
                alarmDao.updateAlarm(alarma.copy(esActivo = isChecked))
                cargarAlarmas() // Carga las alarmas de nuevo después de actualizar
            }
        }
        recyclerView.adapter = adapter

        // 4. Cargar las alarmas al iniciar
        cargarAlarmas()

        // Botones
        findViewById<ImageButton>(R.id.btnAddAlarma).setOnClickListener {
            startActivity(Intent(this, CrearAlarmaActivity::class.java))
        }

        findViewById<Button>(R.id.btnCronometro).setOnClickListener {
            startActivity(Intent(this, CronometroUIActivity::class.java))
        }
        findViewById<Button>(R.id.btnJuegos).setOnClickListener {
            startActivity(Intent(this, JuegosActivity::class.java))
        }
        findViewById<Button>(R.id.btnCuentaAtras).setOnClickListener {
            startActivity(Intent(this, CuentaAtrasActivity::class.java))
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

        // Acción desde Intent
        when (intent.getStringExtra("accion")) {
            "ordenar" -> ordenarAlarmas()
            "eliminar" -> mostrarDialogoConfirmacion()
        }

    }


    override fun onResume() {
        super.onResume()
        cargarAlarmas()
    }

    // Metodo para cargar las alarmas desde la base de datos
    private fun cargarAlarmas() {
        lifecycleScope.launch {
            try {
                val alarmas = alarmDao.getAllAlarms()
                Log.d("AlarmActivity", "Número de alarmas cargadas: ${alarmas.size}")
                adapter.actualizarLista(alarmas)
            } catch (e: Exception) {
                Log.e("AlarmActivity", "Error al cargar alarmas", e)
            }
        }
    }

    // Metodo para ordenar las alarmas
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

    // Metodo para mostrar el dialogo de confirmacion
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

    // Crear el menú de opciones
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
        return true
    }

    // Manejar la seleccion de elementos del menú
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
