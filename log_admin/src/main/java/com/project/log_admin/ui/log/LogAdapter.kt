package com.project.log_admin.ui.log

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.domain.model.Log
import com.project.core.domain.model.Registration
import com.project.log_admin.databinding.ItemListLogBinding
import com.project.log_admin.ui.utils.DivItemLogCallback

class LogAdapter: RecyclerView.Adapter<LogAdapter.ViewHodler>() {

    private var listener: ((String) -> Unit)? = null

    var log = mutableListOf<Log>()
    set(value) {
        val callback = DivItemLogCallback(field, value)
        val result = DiffUtil.calculateDiff(callback)
        field.clear()
        field.addAll(value)
        result.dispatchUpdatesTo(this)
    }

    var registration = mutableListOf<List<Registration>>()

    inner class ViewHodler(private val binding: ItemListLogBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: Log, registration: List<Registration>){
            binding.apply {
                tvCountWaiting.text = log.waiting.toString()
                tvCountReject.text = log.rejected.toString()
                tvCountAccept.text = log.accepted.toString()
                tvDate.text = log.date

                listener?.let {
                    itemView.setOnClickListener {
                        it(log.date)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHodler {
        val binding = ItemListLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHodler(binding)
    }

    override fun onBindViewHolder(holder: ViewHodler, position: Int) {
        holder.bind(log[position], registration[position])
    }

    override fun getItemCount(): Int = log.size

    fun onClick(listener: ((String) -> Unit)?){
        this.listener = listener
    }
}