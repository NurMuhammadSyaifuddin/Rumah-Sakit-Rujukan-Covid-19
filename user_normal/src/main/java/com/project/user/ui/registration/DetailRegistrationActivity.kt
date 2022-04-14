package com.project.user.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.user.R
import com.project.user.databinding.ActivityDetailRegistrationBinding
import com.project.core.domain.model.Registration
import com.project.core.utils.EXTRA_DATA_FOR_REGISTRATION

class DetailRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDataIntent()
    }

    private fun getDataIntent() {
        binding.apply {
            intent?.let {
                val data = it.extras?.getParcelable<Registration>(EXTRA_DATA_FOR_REGISTRATION)

                tvQueue.text = data?.queue.toString()
                tvDate.text = data?.registrationDate.toString()
                tvRegistrationNumber.text = data?.registrationNumber.toString()
                tvName.text = data?.name.toString()
                tvHospitalName.text = data?.hospitalName.toString()
                tvStatusRegistration.text = data?.statusRegistration.toString()
                tvAcceptDate.text =
                    if (data?.acceptDate.isNullOrBlank()) getString(R.string.default_text) else data?.acceptDate.toString()
                tvAdminNotes.text =
                    if (data?.note.isNullOrBlank()) getString(R.string.default_text) else data?.note.toString()
                tvReferredTo.text = data?.referredTo.toString()
            }

            btnOk.setOnClickListener {
                finish()
            }
        }
    }

}