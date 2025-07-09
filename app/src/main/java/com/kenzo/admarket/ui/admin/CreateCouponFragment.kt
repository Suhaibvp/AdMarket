package com.kenzo.admarket.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.adapters.CouponAdapter
import com.kenzo.admarket.databinding.FragmentCreateCouponBinding

class CreateCouponFragment : Fragment() {

    private var _binding: FragmentCreateCouponBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCouponBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amountOptions = listOf(50, 60, 70, 80, 90, 100)
        val spinner = binding.spinnerAmount
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, amountOptions)
        loadCoupons()
        binding.btnGenerateCoupon.setOnClickListener {
            val selectedAmount = amountOptions[spinner.selectedItemPosition]
            val couponCode = "CPN" + (100000..999999).random()  // e.g., CPN123456

            val coupon = hashMapOf(
                "code" to couponCode,
                "amount" to selectedAmount,
                "status" to "available",
                "createdAt" to FieldValue.serverTimestamp()
            )

            Firebase.firestore.collection("coupons")
                .document(couponCode)
                .set(coupon)
                .addOnSuccessListener {
                    binding.tvGeneratedCoupon.text = couponCode
                    binding.btnCopyCoupon.isEnabled = true
                    binding.btnShareCoupon.isEnabled = true
                    Toast.makeText(requireContext(), "Coupon generated!", Toast.LENGTH_SHORT).show()
                    loadCoupons()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to generate coupon", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnCopyCoupon.setOnClickListener {
            val couponCode = binding.tvGeneratedCoupon.text.toString().trim()
            if (couponCode.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Coupon Code", couponCode)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Coupon code copied!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No coupon to copy.", Toast.LENGTH_SHORT).show()
            }
        }

        // Share button
        binding.btnShareCoupon.setOnClickListener {
            val couponCode = binding.tvGeneratedCoupon.text.toString().trim()
            if (couponCode.isNotEmpty()) {
                val message = "🎉 Congratulations! You got 10% cashback.\n\nRedeem this coupon to add it to your wallet:\n\n$couponCode"

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                     // specifically for WhatsApp
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No coupon to share.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCoupons() {
        val recycler = binding.recyclerCoupons
        recycler.layoutManager = LinearLayoutManager(requireContext())

        Firebase.firestore.collection("coupons")
            .orderBy("createdAt", Query.Direction.DESCENDING) // Most recent first
            .get()
            .addOnSuccessListener { snapshot ->
                val adapter = CouponAdapter(snapshot.documents) { doc ->
                    showEditStatusDialog(doc)
                }
                recycler.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load coupons", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showEditStatusDialog(doc: DocumentSnapshot) {
        val options = arrayOf("available", "claimed", "revoked")
        val currentStatus = doc.getString("status") ?: "available"
        val index = options.indexOf(currentStatus)

        AlertDialog.Builder(requireContext())
            .setTitle("Change Status")
            .setSingleChoiceItems(options, index) { dialog, which ->
                val newStatus = options[which]
                doc.reference.update("status", newStatus)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Status updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


}