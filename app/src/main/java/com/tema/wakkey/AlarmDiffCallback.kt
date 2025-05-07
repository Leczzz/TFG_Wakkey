package com.tema.wakkey

import androidx.recyclerview.widget.DiffUtil
import com.tema.wakkey.Database.AlarmEntity

class AlarmDiffCallback(
    private val oldList: List<AlarmEntity>,
    private val newList: List<AlarmEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

