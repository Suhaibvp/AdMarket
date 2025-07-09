package com.kenzo.admarket.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.R
import com.kenzo.admarket.databinding.FragmentRedeemCouponBinding
import com.kenzo.admarket.utils.WalletManager

class RedeemCouponFragment : Fragment() {

    private var _binding: FragmentRedeemCouponBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRedeemCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCouponCode = view.findViewById<EditText>(R.id.etCouponCode)
        val btnRedeem = view.findViewById<Button>(R.id.btnRedeem)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)
        val walletManager = WalletManager(requireContext())
        btnRedeem.setOnClickListener {
            val code = etCouponCode.text.toString().trim()
            if (code.isEmpty()) {
                tvResult.text = "Please enter a coupon code."
                return@setOnClickListener
            }

            // Search Firestore
            Firebase.firestore.collection("coupons")
                .whereEqualTo("code", code)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.isEmpty) {
                        tvResult.text = "Invalid coupon code."
                    } else {
                        val doc = snapshot.documents.first()
                        val status = doc.getString("status") ?: "unknown"
                        val amount = doc.getLong("amount") ?: 0L

                        when (status) {
                            "claimed" -> {
                                tvResult.text = "This coupon has already been claimed."
                            }
                            "revoked" -> {
                                tvResult.text = "This coupon has expired."
                            }
                            "available" -> {
                                // Mark claimed
                                doc.reference.update("status", "claimed")
                                    .addOnSuccessListener {
                                        // Credit the user's wallet
                                        walletManager.addBalance(
                                            WalletManager.TYPE_PURCHASE,
                                            amount,
                                            onSuccess = {
                                                tvResult.text = "Coupon redeemed! ₹$amount added to your purchase wallet."
                                            },
                                            onFailure = {
                                                tvResult.text = "Coupon claimed, but failed to credit wallet."
                                            }
                                        )
                                    }
                                    .addOnFailureListener {
                                        tvResult.text = "Failed to update coupon status."
                                    }
                            }
                            else -> {
                                tvResult.text = "Wrong Coupon ID."
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    tvResult.text = "Error checking coupon. Try again."
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}