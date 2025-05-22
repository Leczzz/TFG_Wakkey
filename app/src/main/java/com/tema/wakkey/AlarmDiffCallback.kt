package com.tema.wakkey

import androidx.recyclerview.widget.DiffUtil
import com.tema.wakkey.Database.AlarmEntity

// Esta clase sirve para optimizar la actualización de listas en un RecyclerView en Android cuando los datos cambian.
class AlarmDiffCallback(
    private val oldList: List<AlarmEntity>,
    private val newList: List<AlarmEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size // Tamaño de la lista antigua
    override fun getNewListSize(): Int = newList.size // Tamaño de la lista nueva

    // Comparar elementos
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Comparar contenidos
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

