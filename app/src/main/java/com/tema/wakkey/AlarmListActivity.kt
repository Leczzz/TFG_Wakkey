package com.tema.wakkey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AlarmDao
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.launch

//Esta actividad muestra una lista de alarmas almacenadas en la base de datos usando un RecyclerView.
class AlarmListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar las alarmas
    private lateinit var adapter: AlarmAdapter // Adaptador para el RecyclerView
    private lateinit var alarmDao: AlarmDao // DAO para las alarmas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerAlarmas) // Encuentra el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this) // Configura el LayoutManager

        val db = AppDatabase.getInstance(this) // Obtiene la instancia de la base de datos
        alarmDao = db.alarmDao() // Obtiene el DAO para las alarmas

        lifecycleScope.launch {
            // Obtén las alarmas de la base de datos
            val alarmas = alarmDao.getAllAlarmsOrderedByHour()

            // Si el adaptador no está asignado, asignamos uno nuevo
            if (!::adapter.isInitialized) {
                // Crea el adaptador con la lista de alarmas
                adapter = AlarmAdapter(alarmas.toMutableList()) { alarma, isChecked ->
                    alarma.esActivo = isChecked
                    lifecycleScope.launch {
                        alarmDao.updateAlarm(alarma) // Actualiza el estado de la alarma en la base de datos
                    }
                }

                // Asigna el adaptador al RecyclerView
                recyclerView.adapter = adapter
            } else {
                // Si el adaptador ya estaba asignado, solo actualizamos la lista
                adapter.actualizarLista(alarmas)
            }
        }
    }
}



