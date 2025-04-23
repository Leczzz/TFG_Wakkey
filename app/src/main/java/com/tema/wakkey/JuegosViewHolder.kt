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

        // Configurar visibilidad del spinner según tieneDificultad
        spinnerDificultad.visibility = if (juego.tieneDificultad) View.VISIBLE else View.GONE

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