package com.project.log_admin.ui.utils

import androidx.recyclerview.widget.DiffUtil
import com.project.core.domain.model.Registration

class DivRegistrationCallback(private val oldRegistration: List<Registration>, private val newRegistration: List<Registration>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldRegistration.size

    override fun getNewListSize(): Int = newRegistration.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldRegistration[oldItemPosition].id == newRegistration[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldRegistration[oldItemPosition].id == newRegistration[newItemPosition].id
}