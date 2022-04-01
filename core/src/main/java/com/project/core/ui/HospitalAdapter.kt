package com.project.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.databinding.ItemListHospitalBinding
import com.project.core.domain.model.Hospital
import com.project.core.utils.DivHospitalCallback
import com.project.core.utils.loadImage

class HospitalAdapter: RecyclerView.Adapter<HospitalAdapter.ViewHolder>() {

    private var listener: ((Hospital) -> Unit)? = null

    var listHospital = mutableListOf<Hospital>()
    set(value) {
        val callback = DivHospitalCallback(field, value)
        val result = DiffUtil.calculateDiff(callback)
        field.clear()
        field.addAll(value)
        result.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemListHospitalBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(hospital: Hospital){
            binding.apply {
                imgPoster.loadImage(hospital.imageUrl.toString())
                tvName.text = hospital.name.toString()

                listener?.let {
                    itemView.setOnClickListener {
                        it(hospital)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ItemListHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false).also {
            return ViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listHospital[position])
    }

    override fun getItemCount(): Int = listHospital.size

    fun onClick(listener: ((Hospital) -> Unit)?){
        this.listener = listener
    }
}