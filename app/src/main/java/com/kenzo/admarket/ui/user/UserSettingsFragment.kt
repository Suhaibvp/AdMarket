package com.kenzo.admarket.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.kenzo.admarket.ui.login.LoginActivity

import com.kenzo.admarket.databinding.FragmentUserSettingsBinding

class UserSettingsFragment : Fragment() {

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
        val userId = prefs.getString("user_id", null)  // email is used as user ID
        if (userId.isNullOrEmpty()) return

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(userId)

        docRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    binding.tvName.text = doc.getString("name") ?: "N/A"
                    binding.tvEmail.text = doc.getString("email") ?: "N/A"
                    binding.tvContact.text = doc.getString("contact") ?: "N/A"
                    binding.tvState.text = doc.getString("state") ?: "N/A"
                    binding.tvDistrict.text = doc.getString("district") ?: "N/A"
                    binding.tvAddress.text =
                        doc.getString("state") ?: "N/A"  // optional if available
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

}