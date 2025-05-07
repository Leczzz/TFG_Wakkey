package com.tema.wakkey

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tema.wakkey.Database.AlarmEntity


class AlarmAdapter(
    private val alarmas: MutableList<AlarmEntity>,
    private val onToggle: (AlarmEntity, Boolean) -> Unit
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
        val diffCallback = AlarmDiffCallback(alarmas, nuevaLista)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        alarmas.clear()
        alarmas.addAll(nuevaLista)
        diffResult.dispatchUpdatesTo(this)
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
        return diasActivos.mapIndexedNotNull { index, c ->
            if (c == '1') dias[index] else null
        }.joinToString(" ")
    }

    private fun onSwitchToggle(alarma: AlarmEntity, isChecked: Boolean) {
        onToggle(alarma, isChecked)
    }
}
