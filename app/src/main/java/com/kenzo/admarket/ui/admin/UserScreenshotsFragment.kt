package com.kenzo.admarket.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.adapters.AdminTransactionAdapter
import com.kenzo.admarket.databinding.FragmentAdminDashboardBinding
import com.kenzo.admarket.databinding.FragmentUserDetailsBinding
import com.kenzo.admarket.model.Transaction
import com.kenzo.admarket.R

class UserScreenshotsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Now you can use `userId` to fetch user details
        if (!userId.isNullOrEmpty()) {
            Firebase.firestore.collection("users")
                .document(userId!!)
                .get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("name") ?: "Unknown"
                    val email = doc.getString("email") ?: "N/A"
                    val mobNumber=doc.getString("contact")?:"N/A"
                    binding.tvUserName.text = "Name:$name"
                    binding.tvUserEmail.text = "Email: $email"
                    binding.tvUserPhone.text="Phone: $mobNumber"


                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load user", Toast.LENGTH_SHORT).show()
                }
        }
        userId?.let { loadUserTransactions(it) }
        binding.btnShowScreenshots.setOnClickListener {
            val bundle = Bundle().apply {
                putString("user_id", userId)
            }
            val galleryFragment = UserScreenshotsGalleryFragment()
            galleryFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, galleryFragment)
                .addToBackStack(null)
                .commit()
        }


    }

    private fun loadUserTransactions(userId: String) {
        Firebase.firestore.collection("withdrawals")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val transactions = documents.mapNotNull { doc ->
                    val id = doc.id
                    val amount = doc.getDouble("amount")
                    val status = doc.getString("status")
                    val date=doc.getLong("timestamp")
                    if (amount != null && status != null) {
                        Transaction(id, date= date.toString(),amount=amount, status = status)
                    } else null
                }
                binding.recyclerScreenshots.layoutManager=LinearLayoutManager(requireContext())
                binding.recyclerScreenshots.adapter = AdminTransactionAdapter(requireContext(), transactions)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getString("user")  // <- Retrieve from bundle
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}