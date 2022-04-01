package com.project.chat_user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.chat_user.databinding.FragmentChatUserBinding

class ChatUserFragment : Fragment() {

    private var _binding: FragmentChatUserBinding? = null
    private lateinit var binding: FragmentChatUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatUserBinding.inflate(inflater, container, false)
        if (_binding != null){
            binding = _binding as FragmentChatUserBinding
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}