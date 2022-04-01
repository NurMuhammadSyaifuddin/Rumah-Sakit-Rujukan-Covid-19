package com.project.activity_user.utils

import androidx.recyclerview.widget.DiffUtil
import com.project.core.domain.model.Registration

class DivItemRegistrationCallback(private val oldItem: List<Registration>, private val newItem: List<Registration>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItem.size

    override fun getNewListSize(): Int = newItem.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItem[oldItemPosition].id == newItem[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItem[oldItemPosition].id == newItem[newItemPosition].id
}