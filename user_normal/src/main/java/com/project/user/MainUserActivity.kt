package com.project.user

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.user.databinding.ActivityMainUserBinding

class MainUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainUserBinding

    private lateinit var adapter: SectionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        adapter = SectionPagerAdapter(this)

        setUpViewPager()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpViewPager() {
        binding.apply {

            viewPager2.also {
                it.isSaveEnabled = true
                it.adapter = adapter
                it.isUserInputEnabled = false
            }

            bottomNavMenu.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> {
                        viewPager2.currentItem = 0
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_activity -> {
                        viewPager2.currentItem = 1
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.navigation_bookmark -> {
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