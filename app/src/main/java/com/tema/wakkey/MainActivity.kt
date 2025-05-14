package com.tema.wakkey

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AppDatabase
import com.tema.wakkey.Database.JuegoEntity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            // Lanza directamente la pantalla de AlarmActivity
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()


        lifecycleScope.launch {
            // 1) Precarga los juegos predeterminados en la base de datos, reemplazando los existentes
            val juegoDao = AppDatabase.getInstance(this@MainActivity).juegoDao()

            lifecycleScope.launch {
                // Borra todos los juegos
                juegoDao.deleteAllJuegos()

                // Lista de juegos predeterminados
                val pred = listOf(
                    JuegoEntity(
                        nombre = "¡Suma!",
                        descripcion = "Completa las sumas para desactivar la alarma.",
                        tieneDificultad = true,
                        imagenResId = R.drawable.suma
                    ),
                    JuegoEntity(
                        nombre = "¡Resta!",
                        descripcion = "Completa las restas para desactivar la alarma.",
                        tieneDificultad = true,
                        imagenResId = R.drawable.restalogo
                    ),
                    JuegoEntity(
                        nombre = "Despeina a Kkey",
                        descripcion = "Sopla en el micrófono hasta despeinar a Kkey.",
                        tieneDificultad = true,
                        imagenResId = R.drawable.despeinakkey
                    ),
                    JuegoEntity(
                        nombre = "Despierta a Kkey",
                        descripcion = "Despierta a Kkey agitando el móvil y completando la barra.",
                        tieneDificultad = true,
                        imagenResId = R.drawable.despiertakkey
                    ),
                    JuegoEntity(
                        nombre = "Scan Kkey",
                        descripcion = "Escanéa cualquier código de barras y desactiva la alarma.",
                        tieneDificultad = false,
                        imagenResId = R.drawable.scankkey
                    )
                )

                // Inserta todos los juegos
                pred.forEach { juegoDao.insertJuego(it) }
            }
        }

        // 2) Referencias a botones de navegación
        val btnAlarma      = findViewById<Button>(R.id.btnAlarma)
        val btnJuegos      = findViewById<Button>(R.id.btnJuegos)
        val btnCronometro  = findViewById<Button>(R.id.btnCronometro)
        val btnCuentaAtras = findViewById<Button>(R.id.btnCuentaAtras)
        val btnMenuOpciones= findViewById<ImageButton>(R.id.btnMenuOpciones)
        val btnAddAlarma   = findViewById<ImageButton>(R.id.btnAddAlarma)

        // 3) Listeners de navegación
        btnAlarma.setOnClickListener {
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }


        btnJuegos.setOnClickListener {
            startActivity(Intent(this, JuegosActivity::class.java))
        }

        btnCronometro.setOnClickListener {
            startActivity(Intent(this, CronometroUIActivity::class.java))
        }

        btnCuentaAtras.setOnClickListener {
            startActivity(Intent(this, CuentaAtrasActivity::class.java))
        }

        btnAddAlarma.setOnClickListener {
            startActivity(Intent(this, CrearAlarmaActivity::class.java))
        }

        // 4) Menú de opciones para AlarmActivity con acción extra
        btnMenuOpciones.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.menuInflater.inflate(R.menu.menu_opciones, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                val intent = Intent(this, AlarmActivity::class.java)
                when (item.itemId) {
                    R.id.opcion_ordenar -> {
                        intent.putExtra("accion", "ordenar")
                        startActivity(intent)
                        true
                    }
                    R.id.opcion_eliminar -> {
                        intent.putExtra("accion", "eliminar")
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }




}
