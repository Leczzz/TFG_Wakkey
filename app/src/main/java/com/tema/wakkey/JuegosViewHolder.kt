package com.tema.wakkey

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.JuegoEntity

class JuegoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
    private val tvNombreJuego: TextView = itemView.findViewById(R.id.tvNombreJuego)
    private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
    private val spinnerDificultad: Spinner = itemView.findViewById(R.id.spinnerDificultad)
    private val btnJugar: Button = itemView.findViewById(R.id.btnJugar)

    fun bind(
        juego: JuegoEntity,
        onJugarClickListener: (JuegoEntity, String) -> Unit
    ) {
        // Setear texto
        tvNombreJuego.text = juego.nombre
        tvDescripcion.text = juego.descripcion

        // Mostrar u ocultar spinner según el juego
        if (juego.tieneDificultad) {
            spinnerDificultad.visibility = View.VISIBLE
            val dificultades = listOf("Fácil", "Media", "Difícil")
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, dificultades)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDificultad.adapter = adapter
        } else {
            spinnerDificultad.visibility = View.GONE
        }

        // Establecer imagen según el nombre
        val logoResId = when (juego.nombre) {
            "¡Suma!" -> R.drawable.suma
            "¡Resta!" -> R.drawable.restalogo
            "Despierta a Kkey" -> R.drawable.despiertakkey
            "Despeina a Kkey" -> R.drawable.despeinakkey
            "Scan Kkey" -> R.drawable.scankkey
            else -> R.drawable.placeholder
        }
        ivLogo.setImageResource(logoResId)
        btnJugar.setOnClickListener {
            val dificultadSeleccionada = spinnerDificultad.selectedItem?.toString() ?: "F"
            onJugarClickListener(juego, dificultadSeleccionada)
        }

        }
    }

