package com.tema.wakkey


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.JuegoEntity

class JuegoAdapter(
    private val juegos: List<JuegoEntity>,
    private val onJugarClickListener: (JuegoEntity, String) -> Unit
) : RecyclerView.Adapter<JuegoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JuegoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_juego, parent, false)
        return JuegoViewHolder(view)
    }

    override fun onBindViewHolder(holder: JuegoViewHolder, position: Int) {
        holder.bind(juegos[position], onJugarClickListener)
    }

    override fun getItemCount(): Int = juegos.size
}
