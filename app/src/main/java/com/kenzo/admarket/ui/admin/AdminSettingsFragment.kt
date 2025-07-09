package com.kenzo.admarket.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kenzo.admarket.databinding.FragmentUserSettingsBinding
import com.kenzo.admarket.ui.login.LoginActivity

class AdminSettingsFragment : Fragment() {

    private var _binding: FragmentUserSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", null)  // You must have saved this during login
        binding.tvName.text=name
        binding.btnLogout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })

        }
    }
}