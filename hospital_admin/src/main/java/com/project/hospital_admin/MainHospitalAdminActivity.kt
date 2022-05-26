package com.project.hospital_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.hospital_admin.databinding.ActivityMainHospitalAdminBinding

class MainHospitalAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainHospitalAdminBinding

    private lateinit var adapter: SectionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHospitalAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        adapter = SectionPagerAdapter(this)

        setUpViewPager()
    }

    private fun setUpViewPager() {
        binding.apply {

            viewPager2.also {
                it.isSaveEnabled = true
                it.adapter = adapter
                it.isUserInputEnabled = false
            }

            bottomNavView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> {
                        viewPager2.currentItem = 0
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_log -> {
                        viewPager2.currentItem = 1
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile_hospital -> {
                        viewPager2.currentItem = 2
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_profile -> {
                        viewPager2.currentItem = 3
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> false
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager2.adapter = null
    }

}