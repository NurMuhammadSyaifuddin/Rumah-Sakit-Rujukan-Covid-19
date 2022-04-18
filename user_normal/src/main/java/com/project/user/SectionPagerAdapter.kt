package com.project.user

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.activity_user.ActivitiesUserFragment
import com.project.bookmark_user.BookmarkHospitalFragment
import com.project.profile_user.ProfileUserFragment
import com.project.user.ui.home.HomeFragment

class SectionPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> HomeFragment()
            1 -> ActivitiesUserFragment()
            2 -> BookmarkHospitalFragment()
            else -> ProfileUserFragment()
        }
}