package com.project.log_admin.ui.activities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.domain.model.Registration
import com.project.core.utils.accepted
import com.project.core.utils.rejected
import com.project.core.utils.wait
import com.project.log_admin.R
import com.project.log_admin.databinding.ItemActivitiesAdminBinding
import com.project.log_admin.ui.utils.DivRegistrationCallback
import com.project.rumahsakitrujukancovid_19.utils.loadImage

class ActivitiesAdminAdapter: RecyclerView.Adapter<ActivitiesAdminAdapter.ViewHolder>() {

    private var listener: ((Registration) -> Unit)? = null

    var registrations = mutableListOf<Registration>()
    set(value) {
        val callback = DivRegistrationCallback(field, value)
        val result = DiffUtil.calculateDiff(callback)
        field.clear()
        field.addAll(value)
        result.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemActivitiesAdminBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Registration){
            binding.apply{
                tvName
                tvTypeActivities.text = item.typeActivities.toString()
                imgUser.loadImage(item.photoUrl.toString())
                tvName.text = item.name.toString()
                tvRegistrationNumber.text = item.registrationNumber.toString()
                tvRegistrationDate.text = item.registrationDate.toString()
                tvReferredTo.text = itemView.context.getString(R.string.referred_to, item.referredTo.toString())

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
        val binding = ItemActivitiesAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(registrations[position])
    }

    override fun getItemCount(): Int = registrations.size

    fun onClick(listener: ((Registration) -> Unit)?){
        this.listener = listener
    }
}