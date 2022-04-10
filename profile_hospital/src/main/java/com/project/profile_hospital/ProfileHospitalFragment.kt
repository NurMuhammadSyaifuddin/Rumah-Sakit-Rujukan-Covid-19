package com.project.profile_hospital

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.profile_hospital.databinding.FragmentProfileHospitalBinding

class ProfileHospitalFragment : Fragment() {

    private var _binding: FragmentProfileHospitalBinding? = null
    private lateinit var binding: FragmentProfileHospitalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileHospitalBinding.inflate(inflater, container, false)
        if (_binding != null){
            binding = _binding as FragmentProfileHospitalBinding
        }
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}