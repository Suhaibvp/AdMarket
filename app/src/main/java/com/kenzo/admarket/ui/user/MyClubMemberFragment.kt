package com.kenzo.admarket.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.adapters.MemberAdapter
import com.kenzo.admarket.adapters.UserAdapter
import com.kenzo.admarket.databinding.FragmentMyClubMembersBinding
import com.kenzo.admarket.databinding.FragmentUserDashboardBinding
import com.kenzo.admarket.model.Member
import com.kenzo.admarket.model.User

class MyClubMemberFragment : Fragment() {

    private var _binding: FragmentMyClubMembersBinding? = null
    private val binding get() = _binding!!

    private val level1Members = listOf(
        Member(1, "Jamsheena", "ADS8832", "7558054958"),
        Member(2, "Noorjahan", "ADS9197", "9037732766"),
        Member(3, "Preetha", "ADS9303", "6238768616")
    )

    private val level2Members = listOf(
        Member(1, "Nasiya", "ADS9604", "9847xxxxxx"),
        Member(2, "Ayisha Asna", "ADS9614", "8590xxxxxx"),
        Member(3, "Zeenath", "ADS10499", "9048xxxxxx"),
        Member(4, "Sreevidhya", "ADS9202", "9061xxxxxx"),
        Member(5, "Kunhumohammed", "ADS10744", "7994xxxxxx")
    )

    private val level3Members = listOf(
        Member(1, "Dummy1", "ADS9991", "9000xxxxxx"),
        Member(2, "Dummy2", "ADS9992", "9011xxxxxx")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyClubMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        // 1️⃣ Fetch current user's myReferralId
        Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { currentUserDoc ->
                val myReferralId = currentUserDoc.getString("myReferralId")

                if (myReferralId.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Referral ID not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 2️⃣ Get all users sponsored by this referral ID
                Firebase.firestore.collection("users")
                    .whereEqualTo("sponsorId", myReferralId)
                    .get()
                    .addOnSuccessListener { sponsorDocs ->
                        val allMembers = sponsorDocs.documents
                            .mapNotNull { doc ->
                                doc.toObject(User::class.java)
                            }
                            .sortedBy { it.createdAt } // sort by createdAt

                        val level1 = mutableListOf<User>()
                        val level2 = mutableListOf<User>()
                        val level3 = mutableListOf<User>()

                        // Assign levels based on position
                        allMembers.forEachIndexed { index, user ->
                            when {
                                index < 10 -> level1.add(user)
                                index < 20 -> level2.add(user)
                                else -> level3.add(user)
                            }
                        }

                        // Update RecyclerViews
                        binding.recyclerLevel1.apply {
                            adapter = MemberAdapter(level1)
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        binding.recyclerLevel2.apply {
                            adapter = MemberAdapter(level2)
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                        binding.recyclerLevel3.apply {
                            adapter = MemberAdapter(level3)
                            layoutManager = LinearLayoutManager(requireContext())
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to load members", Toast.LENGTH_SHORT).show()
                    }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
