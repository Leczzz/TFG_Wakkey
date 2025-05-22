package com.tema.wakkey

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

// ViewHolder para el RecyclerView de alarmas
class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtHora: TextView = itemView.findViewById(R.id.txtHora)
    val txtJuego: TextView = itemView.findViewById(R.id.txtJuego)
    val txtDias: TextView = itemView.findViewById(R.id.txtDias)
    val switchActivo: SwitchMaterial = itemView.findViewById(R.id.switchActivo)
}
