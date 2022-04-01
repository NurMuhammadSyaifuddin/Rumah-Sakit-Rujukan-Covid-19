package com.project.profile_admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.profile_admin.databinding.FragmentProfileAdminBinding

class ProfileAdminFragment : Fragment() {

    private var _binding: FragmentProfileAdminBinding? = null
    private lateinit var binding: FragmentProfileAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileAdminBinding.inflate(inflater, container, false)
        if (_binding != null){
            binding = _binding as FragmentProfileAdminBinding
        }
        return inflater.inflate(R.layout.fragment_profile_admin, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}