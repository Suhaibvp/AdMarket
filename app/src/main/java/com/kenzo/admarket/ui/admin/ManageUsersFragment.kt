package com.kenzo.admarket.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.R
import com.kenzo.admarket.adapters.UserAdapter
import com.kenzo.admarket.databinding.FragmentAdminClubMembersBinding
import com.kenzo.admarket.databinding.FragmentManageUsersBinding
import com.kenzo.admarket.model.User

class ManageUsersFragment : Fragment() {

    private var _binding: FragmentAdminClubMembersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminClubMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val allUsers = mutableListOf<User>()

                for (doc in documents) {
                    val user = doc.toObject(User::class.java)
                    if (user.role == "user") {
                        allUsers.add(user)
                    }
                }
                binding.userCountText.text = "Total Users: ${allUsers.size}"

                binding.recyclerLevel1.apply {
                    adapter = UserAdapter(allUsers, { editUser(it) }, { confirmDeleteUser(it) })
                    layoutManager = LinearLayoutManager(requireContext())
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show()
            }
    }
    private fun confirmDeleteUser(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.name}? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun deleteUser(user: User) {
        val userRef = Firebase.firestore.collection("users").document(user.email)

        userRef.delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
                // ✅ Update UI locally (optional optimization)
                (binding.recyclerLevel1.adapter as? UserAdapter)?.let {
                    val updatedList = it.users.toMutableList()
                    updatedList.remove(user)
                    binding.recyclerLevel1.adapter = UserAdapter(updatedList, { editUser(it) }, { confirmDeleteUser(it) })
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete user", Toast.LENGTH_SHORT).show()
            }
    }


    private fun editUser(user: User) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_user, null)

        val nameView = dialogView.findViewById<TextView>(R.id.tv_name)
        val contactView = dialogView.findViewById<TextView>(R.id.tv_contact)
        val checkbox = dialogView.findViewById<CheckBox>(R.id.checkbox_verified)
        Firebase.firestore.collection("users")
            .document(user.email)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Log raw data
                    Log.i("DEBUG", "Raw document: ${doc.data}")

                    // Safely extract 'verified'
                    val isVerified = doc.getBoolean("verified") ?: false
                    Log.i("DEBUG", "Raw document: ${isVerified}")
                    checkbox.isChecked = isVerified

                }
            }
        // Set values
        nameView.text = "Name: ${user.name}"
        contactView.text = "Contact: ${user.contact}"
        println("is user verifyed ${user.verified}")


        AlertDialog.Builder(requireContext())
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val isNowVerified = checkbox.isChecked

                val updatedUser = mapOf(
                    "verified" to isNowVerified
                )

                val userRef = Firebase.firestore.collection("users").document(user.email)

                userRef.update(updatedUser)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "User updated", Toast.LENGTH_SHORT).show()

                        // ✅ Reward sponsor only if user is now verified
                        if (isNowVerified) {
                            Firebase.firestore.collection("users")
                                .whereEqualTo("myReferralId", user.sponsorId)
                                .get()
                                .addOnSuccessListener { sponsorQuery ->
                                    val sponsorDoc = sponsorQuery.documents.firstOrNull()
                                    if (sponsorDoc != null) {
                                        val sponsorWalletId = sponsorDoc.getString("walletId")
                                        if (!sponsorWalletId.isNullOrEmpty()) {
                                            val walletRef = Firebase.firestore.collection("wallets").document(sponsorWalletId)
                                            Firebase.firestore.runTransaction { txn ->
                                                val walletSnapshot = txn.get(walletRef)
                                                val current = walletSnapshot.getLong("referBalance") ?: 0
                                                txn.update(walletRef, "referBalance", current + 50)
                                            }.addOnSuccessListener {
                                                Toast.makeText(requireContext(), "Referral bonus added", Toast.LENGTH_SHORT).show()
                                            }.addOnFailureListener {
                                                Toast.makeText(requireContext(), "Failed to update sponsor wallet", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
