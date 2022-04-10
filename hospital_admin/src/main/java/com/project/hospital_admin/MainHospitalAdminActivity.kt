package com.project.hospital_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.project.hospital_admin.databinding.ActivityMainHospitalAdminBinding

class MainHospitalAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainHospitalAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHospitalAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavController()
    }

    private fun setUpNavController() {
        val navController = findNavController(R.id.nav_host_controller)

        binding.bottomNavView.setupWithNavController(navController)
    }
}