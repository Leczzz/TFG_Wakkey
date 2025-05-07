package com.tema.wakkey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AlarmDao
import com.tema.wakkey.Database.AppDatabase
import kotlinx.coroutines.launch

class AlarmListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmDao: AlarmDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerAlarmas) // Encuentra el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this) // Configura el LayoutManager

        val db = AppDatabase.getInstance(this)
        alarmDao = db.alarmDao()

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



