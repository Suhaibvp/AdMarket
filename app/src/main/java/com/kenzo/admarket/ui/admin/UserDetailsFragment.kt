package com.kenzo.admarket.ui.admin


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.R

import com.kenzo.admarket.adapters.TransactionAdapter
import com.kenzo.admarket.adapters.UserAdapter2
import com.kenzo.admarket.databinding.FragmentUserListBinding
import com.kenzo.admarket.model.Transaction
import com.kenzo.admarket.model.User

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserList()

        //populateDemoTransactions()
    }
    private fun loadUserList() {
        Firebase.firestore.collection("users")
            .whereEqualTo("role", "user")
            .get()
            .addOnSuccessListener { documents ->
                val userList = documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java).copy(email = doc.id)
                    user
                }

                binding.userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.userRecyclerView.adapter = UserAdapter2(userList) { user ->
                    val bundle = Bundle().apply {
                        putString("user", user)
                    }
                    val fragment = UserScreenshotsFragment()
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, fragment) // Replace with your container ID
                        .addToBackStack(null)
                        .commit()
                }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
            }
    }

    //private fun putParcelable(string: String, string: String) {}


//    private fun populateDemoTransactions() {
//
//        transactionAdapter.submitList(demoList)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
