package com.project.hospital_admin

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.hospital_admin.ui.home.HomeFragment
import com.project.log_admin.ui.log.LogAdminFragment
import com.project.profile_admin.ProfileAdminFragment
import com.project.profile_hospital.ProfileHospitalFragment

class SectionPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> HomeFragment()
            1 -> LogAdminFragment()
            2 -> ProfileHospitalFragment()
            else -> ProfileAdminFragment()
        }
}