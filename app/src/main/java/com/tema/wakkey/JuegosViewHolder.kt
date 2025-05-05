package com.tema.wakkey

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.JuegoEntity
import com.tema.wakkey.R

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
        tvNombreJuego.text = juego.nombre
        tvDescripcion.text = juego.descripcion

        // Mostrar u ocultar el Spinner según tengaDificultad
        spinnerDificultad.visibility = if (juego.tieneDificultad) View.VISIBLE else View.GONE

        // Asignar logo según el nombre del juego
        val logoResId = when (juego.nombre) {
            "Despeina a Kkey" -> R.drawable.despeinakkey
            "Despierta a Kkey" -> R.drawable.despiertakkey
            "¡Suma!" -> R.drawable.suma
            "Scan Kkey" -> R.drawable.scankkey
            else -> ""
        }

        ivLogo.setImageResource(logoResId as Int)

        btnJugar.setOnClickListener {
            val dificultad = if (juego.tieneDificultad) {
                spinnerDificultad.selectedItem.toString()
            } else {
                ""
            }
            onJugarClickListener(juego, dificultad)
        }
    }



}
