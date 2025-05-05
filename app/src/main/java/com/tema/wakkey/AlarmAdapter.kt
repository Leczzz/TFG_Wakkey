package com.tema.wakkey


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.tema.wakkey.Database.AlarmEntity

class AlarmAdapter(
    private var alarmas: List<AlarmEntity>,
    private val onSwitchToggle: (AlarmEntity, Boolean) -> Unit
) : RecyclerView.Adapter<AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarma = alarmas[position]

        holder.txtHora.text = alarma.hora
        holder.txtJuego.text = getNombreJuego(alarma.idJuego)
        holder.txtDias.text = getTextoDias(alarma.diasActivos)

        // Protege el Switch para que no se dispare al reciclar la vista
        holder.switchActivo.setOnCheckedChangeListener(null)
        holder.switchActivo.isChecked = alarma.esActivo
        holder.switchActivo.setOnCheckedChangeListener { _, isChecked ->
            onSwitchToggle(alarma, isChecked)
        }
    }

    override fun getItemCount(): Int = alarmas.size

    fun actualizarLista(nuevaLista: List<AlarmEntity>) {
        this.alarmas = nuevaLista
        notifyDataSetChanged()
    }

    private fun getNombreJuego(idJuego: Int): String {
        return when (idJuego) {
            1 -> "Despeina a Kkey"
            2 -> "Despierta a Kkey"
            3 -> "Resta!"
            4 -> "Suma!"
            else -> "Scan Kkey"
        }
    }

    private fun getTextoDias(diasActivos: String): String {
        val dias = listOf("L", "M", "X", "J", "V", "S", "D")
        val diasActivosTexto = StringBuilder()

        // Recorremos cada carácter de la cadena diasActivos
        for (i in diasActivos.indices) {
            if (diasActivos[i] == '1') {
                diasActivosTexto.append("${dias[i]} ")  // Añadimos el nombre del día
            }
        }
        return diasActivosTexto.toString().trim()
    }
}

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val txtHora: TextView = itemView.findViewById(R.id.txtHora)
    val txtJuego: TextView = itemView.findViewById(R.id.txtJuego)
    val txtDias: TextView = itemView.findViewById(R.id.txtDias)
    val switchActivo: SwitchMaterial = itemView.findViewById(R.id.switchActivo)
}
