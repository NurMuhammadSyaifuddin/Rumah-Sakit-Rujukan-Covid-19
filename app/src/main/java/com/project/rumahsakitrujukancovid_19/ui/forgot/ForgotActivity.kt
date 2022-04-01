package com.project.rumahsakitrujukancovid_19.ui.forgot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.rumahsakitrujukancovid_19.databinding.ActivityForgotBinding

class ForgotActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}