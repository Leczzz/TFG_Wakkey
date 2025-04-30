package com.tema.wakkey

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val btnAlarma = findViewById<Button>(R.id.btnAlarma)
        val btnJuegos = findViewById<Button>(R.id.btnJuegos)
        val btnCronometro = findViewById<Button>(R.id.btnCronometro)
        val btnCuentaAtras = findViewById<Button>(R.id.btnCuentaAtras)
        val btnMenuOpciones = findViewById<ImageButton>(R.id.btnMenuOpciones)
        val btnAddAlarma = findViewById<ImageButton>(R.id.btnAddAlarma)

        btnAlarma.setOnClickListener {
             val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        btnJuegos.setOnClickListener {
            startActivity(Intent(this, GamesActivity::class.java))
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

        btnMenuOpciones.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.menu_opciones, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_ordenar -> {
                        val intent = Intent(this, AlarmActivity::class.java)
                        intent.putExtra("accion", "ordenar")
                        startActivity(intent)
                        true
                    }
                    R.id.opcion_eliminar -> {
                        val intent = Intent(this, AlarmActivity::class.java)
                        intent.putExtra("accion", "eliminar")
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
