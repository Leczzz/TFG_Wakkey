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

    private lateinit var recyclerView: RecyclerView // ReciclerView para mostrar las alarmas
    private lateinit var adapter: AlarmAdapter //Adaper para la lista de alarmas
    private lateinit var alarmDao: AlarmDao //Dao para acceder a la base de datos

    override fun onCreate(savedInstanceState: Bundle?) { //Crea la actividad
        super.onCreate(savedInstanceState) //Llama al onCreate de la clase padre
        setContentView(R.layout.activity_alarm)  //Establece la vista de la actividad

        recyclerView = findViewById(R.id.recyclerAlarmas) //Busca el RecyclerView en la vista
        recyclerView.layoutManager = LinearLayoutManager(this) //Establece el LinearLayoutManager para el RecyclerView


        val db = AppDatabase.getInstance(this) //Obtiene la instancia de la base de datos
        alarmDao = db.alarmDao() //Obtiene el DAO para las alarmas

        lifecycleScope.launch { //Ejecuta una corrutina en el ciclo de vida de la actividad
            val alarmas = alarmDao.getAllAlarmsOrderedByHour() //Obtiene todas las alarmas ordenadas por hora
            adapter = AlarmAdapter(alarmas) { alarma, isChecked -> //Crea un adaptador para la lista de alarmas
                alarma.esActivo = isChecked //Actualiza el estado de la alarma
                lifecycleScope.launch { //Ejecuta una corrutina en el ciclo de vida de la actividad
                    alarmDao.updateAlarm(alarma) //Actualiza la alarma en la base de datos
                }
            }
            recyclerView.adapter = adapter //Establece el adaptador en el RecyclerView
        }
    }
}
