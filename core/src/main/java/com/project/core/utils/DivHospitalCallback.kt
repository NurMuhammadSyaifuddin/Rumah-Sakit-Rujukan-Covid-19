package com.project.core.utils

import androidx.recyclerview.widget.DiffUtil
import com.project.core.domain.model.Hospital

class DivHospitalCallback(private val oldHospitals: List<Hospital>, private val newHospitals: List<Hospital>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldHospitals.size

    override fun getNewListSize(): Int = newHospitals.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldHospitals[oldItemPosition].id == newHospitals[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldHospitals[oldItemPosition].id == newHospitals[newItemPosition].id
}