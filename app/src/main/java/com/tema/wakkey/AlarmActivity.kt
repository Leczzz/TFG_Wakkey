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

        // Implementa aquí la lógica específica de alarmas
    }

    private fun setupBottomMenu() {
        // Similar al de MainActivity pero marcando el botón activo
        findViewById<Button>(R.id.btnAlarma).setTextColor(Color.WHITE)
        findViewById<Button>(R.id.btnCronometro).setTextColor(Color.parseColor("#808080"))

        findViewById<Button>(R.id.btnJuegos).setOnClickListener {
            startActivity(Intent(this, JuegosActivity::class.java))
        }

    }
}