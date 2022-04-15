package com.project.hospital_admin.ui.registration

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding2.widget.RxTextView
import com.project.hospital_admin.R
import com.project.hospital_admin.databinding.ActivityCheckingRegistrationBinding
import com.project.rumahsakitrujukancovid_19.utils.disable
import com.project.rumahsakitrujukancovid_19.utils.enable
import com.project.rumahsakitrujukancovid_19.utils.showAlertLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckingRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckingRegistrationBinding

    private lateinit var loading: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckingRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        loading = showAlertLoading(this)

        onAction()
    }

    @SuppressLint("CheckResult")
    private fun onAction() {
        binding.apply {

            tvCharCount.text = getString(R.string.character_s_100, 0.toString())

            val noteStream = RxTextView.textChanges(edtNote)
                .skipInitialValue()
                .map { note ->
                    note.length > 100
                }

            noteStream.subscribe {
                showNoteExistAlert(it)
            }

            btnReject.setOnClickListener {
                loading.show()
            }

        }
    }

    private fun showNoteExistAlert(isNotValid: Boolean) {
        binding.apply {
            if (isNotValid) {
                textInputHospital.error = getString(R.string.note_max_100_char)
                btnAccept.disable()
                btnReject.disable()
            }else{
                textInputHospital.error = null
                btnAccept.enable()
                btnReject.enable()
            }

            lifecycleScope.launch(Dispatchers.Default) {
                val charNoteCount = edtNote.text.length.toString()

                withContext(Dispatchers.Main){
                    tvCharCount.text = getString(R.string.character_s_100, charNoteCount)
                }
            }

        }
    }
}