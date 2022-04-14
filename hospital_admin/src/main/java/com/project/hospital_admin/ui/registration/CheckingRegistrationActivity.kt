package com.project.hospital_admin.ui.registration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.hospital_admin.R
import com.project.hospital_admin.databinding.ActivityCheckingRegistrationBinding

class CheckingRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckingRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckingRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}