package com.project.hospital_admin.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.domain.model.Registration
import com.project.core.utils.*
import com.project.hospital_admin.R
import com.project.hospital_admin.databinding.ItemRegistrationAdminBinding
import com.project.hospital_admin.utils.DivItemRegistrationCallback
import com.project.rumahsakitrujukancovid_19.utils.loadImage

class ActivityAdminAdapter: RecyclerView.Adapter<ActivityAdminAdapter.ViewHolder>() {

    private var listener: ((Registration) -> Unit)? = null

    var registration = mutableListOf<Registration>()
        set(value) {
            val callback = DivItemRegistrationCallback(field, value)
            val result = DiffUtil.calculateDiff(callback)
            field.clear()
            field.addAll(value)
            result.dispatchUpdatesTo(this)
        }

    inner class ViewHolder(private val binding: ItemRegistrationAdminBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Registration){
            binding.apply {
                tvTypeActivities.text = item.typeActivities.toString()
                imgUser.loadImage(item.photoUrl.toString())
                tvName.text = item.name.toString()
                tvRegistrationNumber.text = item.registrationNumber.toString()
                tvRegistrationDate.text = item.registrationDate.toString()

                when (val status = item.statusRegistration) {
                    itemView.context.wait() -> {
                        tvStatusRegistration.text = status
                        tvStatusRegistration.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.resources,
                                R.color.textColorPrimary,
                                null
                            )
                        )
                        tvStatusRegistration.background = ResourcesCompat.getDrawable(
                            itemView.resources,
                            R.drawable.bg_wait,
                            null
                        )
                    }
                    itemView.context.accepted() -> {
                        tvStatusRegistration.text = status
                        tvStatusRegistration.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.resources,
                                R.color.white,
                                null
                            )
                        )
                        tvStatusRegistration.background = ResourcesCompat.getDrawable(
                            itemView.resources,
                            R.drawable.bg_accept,
                            null
                        )
                    }
                    itemView.context.rejected() -> {
                        tvStatusRegistration.text = status
                        tvStatusRegistration.setTextColor(
                            ResourcesCompat.getColor(
                                itemView.resources,
                                R.color.white,
                                null
                            )
                        )
                        tvStatusRegistration.background = ResourcesCompat.getDrawable(
                            itemView.resources,
                            R.drawable.bg_reject,
                            null
                        )
                    }

                }

                listener?.let {
                    itemView.setOnClickListener {
                        it(item)
                    }
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRegistrationAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(registration[position])
    }

    override fun getItemCount(): Int = registration.size

    fun onClick(listener: ((Registration) -> Unit)?) {
        this.listener = listener
    }
}