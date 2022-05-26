package com.project.log_admin.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.project.core.domain.model.Log

class DivItemLogCallback(private val oldLog: List<Log>, private val newLog: List<Log>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldLog.size

    override fun getNewListSize(): Int = newLog.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldLog[oldItemPosition].date == newLog[newItemPosition].date

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldLog[oldItemPosition].date == newLog[newItemPosition].date
}