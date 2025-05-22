package com.tema.wakkey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AlarmEntity


class AlarmAdapter(
    private val alarmas: MutableList<AlarmEntity>, // Lista de alarmas
    private val onToggle: (AlarmEntity, Boolean) -> Unit // Callback para el cambio de estado del Switch
) : RecyclerView.Adapter<AlarmViewHolder>() { // Adaptador para el RecyclerView de alarmas

    // Crear una nueva vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    // Enlazar datos a una vista
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

    override fun getItemCount(): Int = alarmas.size // Cantidad de elementos en la lista

    // Actualizar la lista de alarmas
    fun actualizarLista(nuevaLista: List<AlarmEntity>) {
        val diffCallback = AlarmDiffCallback(alarmas, nuevaLista)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        alarmas.clear()
        alarmas.addAll(nuevaLista)
        diffResult.dispatchUpdatesTo(this)
    }


    private fun getNombreJuego(idJuego: Int): String { //Coge en base al ID del juego el nombre
        return when (idJuego) {
            1 -> "Despierta a Kkey"
            2 -> "Despeina a Kkey"
            3 -> "Resta!"
            4 -> "Suma!"
            5 -> "Scan Kkey"
            else -> "Desactivacion Manual"

        }
    }

    private fun getTextoDias(diasActivos: String): String { //Coge en base a los dias activos el texto
        val dias = listOf("L", "M", "X", "J", "V", "S", "D")
        return diasActivos.mapIndexedNotNull { index, c ->
            if (c == '1') dias[index] else null
        }.joinToString(" ")
    }

    private fun onSwitchToggle(alarma: AlarmEntity, isChecked: Boolean) { // Callback para el cambio de estado del Switch
        onToggle(alarma, isChecked)
    }
}
