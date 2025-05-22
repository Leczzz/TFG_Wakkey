package com.tema.wakkey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.JuegoEntity

 // Adaptador para la lista de juegos
class JuegoAdapter(
    private val juegos: List<JuegoEntity>, // Lista de juegos
    private val onJugarClickListener: (JuegoEntity, String) -> Unit // Manejador de clics en "Jugar"
) : RecyclerView.Adapter<JuegoViewHolder>() { // Adaptador para la lista de juegos

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JuegoViewHolder {
        val view = LayoutInflater.from(parent.context) // Inflar la vista
            .inflate(R.layout.item_juego, parent, false) // Definir el diseño del item
        return JuegoViewHolder(view) // Crear un nuevo ViewHolder
    }

    override fun onBindViewHolder(holder: JuegoViewHolder, position: Int) {
        val juego = juegos[position] // Obtener el juego en la posición
        holder.bind(juego, onJugarClickListener) // Configurar el ViewHolder
    }

    override fun getItemCount(): Int = juegos.size // Cantidad de juegos
}
