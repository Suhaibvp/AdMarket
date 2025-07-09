package com.kenzo.admarket.ui.user

import android.content.ClipData
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.databinding.FragmentWalletBinding
import com.kenzo.admarket.utils.WalletManager
import android.content.ClipboardManager

class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!

    private lateinit var walletManager: WalletManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        walletManager = WalletManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchWalletBalances()
        val prefs = requireContext().getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)
        if (userId!=null){
            Firebase.firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    val myReferralId = doc.getString("myReferralId")
                    binding.tvReferralId.text = myReferralId ?: "Not available"
                }
        }
        binding.btnShareReferral.setOnClickListener {
            val referralId = binding.tvReferralId.text.toString()

            Firebase.firestore.collection("ApplicationLink")
                .limit(1) // Assuming you only store one latest link
                .get()
                .addOnSuccessListener { snapshot ->
                    val doc = snapshot.documents.firstOrNull()
                    val appLink = doc?.getString("Link") ?: "https://your-default-link.com"

                    val message = """
                🚀 Join this app and earn rewards!
                📥 Download here: $appLink
                🆔 Use my referral ID: $referralId
            """.trimIndent()

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }

                    startActivity(Intent.createChooser(intent, "Share via"))
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch app link", Toast.LENGTH_SHORT).show()
                }
        }
        binding.btnCopyReferral.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral ID", binding.tvReferralId.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Referral ID copied to clipboard", Toast.LENGTH_SHORT).show()
        }



    }

    private fun fetchWalletBalances() {
        walletManager.getBalance(WalletManager.TYPE_STATUS) { status ->
            binding.tvStatusBalance.text = "₹${status ?: 0}"
        }

        walletManager.getBalance(WalletManager.TYPE_REFERRAL) { referral ->
            binding.tvReferralBalance.text = "₹${referral ?: 0}"
        }

        walletManager.getBalance(WalletManager.TYPE_PURCHASE) { purchase ->
            binding.tvPurchaseBalance.text = "₹${purchase ?: 0}"
        }

        walletManager.getTotalBalance { total ->
            binding.tvTotalBalance.text = "₹${total ?: 0}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
