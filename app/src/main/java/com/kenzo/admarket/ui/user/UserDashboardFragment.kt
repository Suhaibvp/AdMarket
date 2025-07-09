package com.kenzo.admarket.ui.user

import android.app.AlertDialog
import android.content.Context
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.firestore
import com.kenzo.admarket.R
import com.kenzo.admarket.adapters.WithdrawalAdapter
import com.kenzo.admarket.databinding.FragmentAdminDashboardBinding
import com.kenzo.admarket.databinding.FragmentUserDashboard2Binding
import com.kenzo.admarket.databinding.FragmentUserDashboardBinding
import com.kenzo.admarket.model.Withdrawal
import com.kenzo.admarket.utils.WalletManager
import com.kenzo.admarket.utils.WalletManager.Companion.TYPE_STATUS


class UserDashboardFragment: Fragment() {

    private var _binding: FragmentUserDashboard2Binding? = null
    private val binding get() = _binding!!
    private var isVerified: Boolean = false
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.uploadImageFragment.tvFileName.text=it.path
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDashboard2Binding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch user ID (email) from SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)  // You must have saved this during login

        if (userId.isNullOrEmpty()) {
            binding.statusIncomeText.text = "User not found"
            return
        }

        Firebase.firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val walletId = userDoc.getString("walletId") ?: return@addOnSuccessListener

                    // ✅ Safe assignment to class-level variable
                    isVerified = userDoc.getBoolean("verified") ?: false

                    // ✅ Set text and color
                    if (isVerified) {
                        binding.tvWithdrawAvailability.text = "Available"
                        binding.tvWithdrawAvailability.setTextColor(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                        )
                        binding.icWithdraw.setImageResource(R.drawable.ic_unloack)
                        binding.icWithdraw.setColorFilter(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark),
                            PorterDuff.Mode.SRC_IN
                        )
                        binding.btnWithdraw.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                        )


                    } else {
                        binding.tvWithdrawAvailability.text = "Not Available"
                        binding.tvWithdrawAvailability.setTextColor(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                        )
                        binding.icWithdraw.setImageResource(android.R.drawable.ic_lock_lock)
                        binding.icWithdraw.setColorFilter(
                            ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark),
                            PorterDuff.Mode.SRC_IN
                        )
                        binding.btnWithdraw.setBackgroundColor(
                            ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                        )
                    }

//                    Firebase.firestore.collection("wallets").document(walletId)
//                        .get()
//                        .addOnSuccessListener { walletDoc ->
//                            val balance = walletDoc.getLong("balance") ?: 0
//                            binding.statusIncomeText.text = "Status Income: ₹ $balance"
//                        }
//                        .addOnFailureListener {
//                            binding.statusIncomeText.text = "Error fetching wallet"
//                        }
                }
            }


        updateWithdrawel(userId)
        binding.btnWithdraw.setOnClickListener {
            if (isVerified){
                showWithdrawDialog()
            }

        }

        binding.uploadImageFragment.btnChooseFile.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.uploadImageFragment.btnSubmitImage.setOnClickListener {
            uploadStatusScreenshot()
        }

    }
    private fun uploadStatusScreenshot() {
        val userId = requireContext()
            .getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
            .getString("user_id", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val imageBytes = uriToByteArray(selectedImageUri!!) ?: return

        val statusData = hashMapOf(
            "userId" to userId,
            "timestamp" to System.currentTimeMillis(),
            "image" to Blob.fromBytes(imageBytes) // ← Fix here
        )


        Firebase.firestore.collection("status_uploads")
            .add(statusData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Status uploaded", Toast.LENGTH_SHORT).show()
                selectedImageUri = null
                binding.uploadImageFragment.tvFileName.text="No File Choosen" // or set a placeholder image
                // Add ₹6 to wallet
                WalletManager(requireContext()).addBalance(TYPE_STATUS,6L)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return requireContext().contentResolver.openInputStream(uri)?.use {
            it.readBytes()
        }
    }
    private fun updateWithdrawel(userId: String) {
        if (userId.isNotEmpty()) {
            Firebase.firestore.collection("withdrawals")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val withdrawalList = documents.mapNotNull { doc ->
                        val amount = doc.getLong("amount")
                        val status = doc.getString("status")
                        val timestamp = doc.getLong("timestamp")
                        if (amount != null && status != null && timestamp != null) {
                            Withdrawal(amount, status, timestamp)
                        } else null
                    }.sortedByDescending { it.timestamp }  // 🔁 Sort by timestamp DESC

                    binding.withdrawalRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.withdrawalRecyclerView.adapter = WithdrawalAdapter(withdrawalList)

                    val walletManager = WalletManager(requireContext())
                    walletManager.getTotalBalance { balance ->
                        if (balance != null) {
                            binding.statusIncomeText.text = "My Income: ₹$balance"
                        } else {
                            Toast.makeText(requireContext(), "Failed to get wallet balance", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load withdrawals", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User ID is empty", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showWithdrawDialog() {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 24, 32, 0)

        val spinner = Spinner(requireContext())
        val balanceTypes = listOf("Status Balance", "Referral Balance", "Purchase Balance")
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, balanceTypes)

        val editText = EditText(requireContext())
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        editText.hint = "Enter amount"

        layout.addView(spinner)
        layout.addView(editText)

        AlertDialog.Builder(requireContext())
            .setTitle("Request Withdrawal")
            .setView(layout)
            .setPositiveButton("Submit") { _, _ ->
                val amount = editText.text.toString().toLongOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedType = when (spinner.selectedItem.toString()) {
                    "Status Balance" -> WalletManager.TYPE_STATUS
                    "Referral Balance" -> WalletManager.TYPE_REFERRAL
                    "Purchase Balance" -> WalletManager.TYPE_PURCHASE
                    else -> null
                }

                if (selectedType == null) {
                    Toast.makeText(requireContext(), "Invalid balance type", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // 💡 Check balance before deducting
                WalletManager(requireContext()).getBalance(selectedType) { currentBalance ->
                    if (currentBalance == null) {
                        Toast.makeText(requireContext(), "Failed to fetch balance", Toast.LENGTH_SHORT).show()
                        return@getBalance
                    }

                    if (amount > currentBalance) {
                        Toast.makeText(requireContext(), "Not enough balance", Toast.LENGTH_SHORT).show()
                        return@getBalance
                    }

                    WalletManager(requireContext()).deductBalance(
                        selectedType,
                        amount,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Withdrawal successful", Toast.LENGTH_SHORT).show()
                                                val userId = requireContext()
                        .getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        .getString("user_id", null)
                                                val withdrawal = hashMapOf(
                        "userId" to userId,
                        "amount" to amount,
                        "status" to "pending",
                        "timestamp" to System.currentTimeMillis()
                    )

                    Firebase.firestore.collection("withdrawals")
                        .add(withdrawal)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Withdrawal request submitted", Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                updateWithdrawel(userId.toString())
                            }, 500)
                            //loadWithdrawals() // if you're showing list
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to log withdrawal", Toast.LENGTH_SHORT).show()
                        }
                        },
                        onFailure = {
                            Toast.makeText(requireContext(), "Withdrawal failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


//    private fun processWithdrawal(amount: Long) {
//        val walletManager = WalletManager(requireContext())
//
//        walletManager.getTotalBalance { balance ->
//            if (balance == null) {
//                Toast.makeText(requireContext(), "Could not fetch balance", Toast.LENGTH_SHORT).show()
//                return@getTotalBalance
//            }
//
//            if (amount > balance) {
//                Toast.makeText(requireContext(), "Amount exceeds wallet balance", Toast.LENGTH_SHORT).show()
//                return@getTotalBalance
//            }
//
//            // Deduct first
//            walletManager.deductBalance(amount,
//                onSuccess = {
//                    // Then log withdrawal in Firestore
//                    val userId = requireContext()
//                        .getSharedPreferences("user_session", Context.MODE_PRIVATE)
//                        .getString("user_id", null)
//
//                    if (userId == null) {
//                        Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
//                        return@deductFromWallet
//                    }
//
//                    val withdrawal = hashMapOf(
//                        "userId" to userId,
//                        "amount" to amount,
//                        "status" to "pending",
//                        "timestamp" to System.currentTimeMillis()
//                    )
//
//                    Firebase.firestore.collection("withdrawals")
//                        .add(withdrawal)
//                        .addOnSuccessListener {
//                            Toast.makeText(requireContext(), "Withdrawal request submitted", Toast.LENGTH_SHORT).show()
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                updateWithdrawel(userId)
//                            }, 500)
//                            //loadWithdrawals() // if you're showing list
//                        }
//                        .addOnFailureListener {
//                            Toast.makeText(requireContext(), "Failed to log withdrawal", Toast.LENGTH_SHORT).show()
//                        }
//                },
//                onFailure = {
//                    Toast.makeText(requireContext(), "Wallet deduction failed", Toast.LENGTH_SHORT).show()
//                }
//            )
//        }
//    }





}