package com.kenzo.admarket.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class WalletManager(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val userId = prefs.getString("user_id", null)

    companion object {
        const val TYPE_STATUS = "statusBalance"
        const val TYPE_REFERRAL = "referBalance"
        const val TYPE_PURCHASE = "purchaseBalance"
    }

    fun addBalance(
        type: String,
        amount: Long,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val walletId = userDoc.getString("walletId")
                if (walletId.isNullOrEmpty()) {
                    Toast.makeText(context, "Wallet not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val walletRef = db.collection("wallets").document(walletId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(walletRef)
                    val current = snapshot.getLong(type) ?: 0
                    transaction.update(walletRef, type, current + amount)
                }.addOnSuccessListener { onSuccess?.invoke() }
                    .addOnFailureListener { onFailure?.invoke(it) }
            }
            .addOnFailureListener { onFailure?.invoke(it) }
    }

    fun deductBalance(
        type: String,
        amount: Long,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        addBalance(type, -amount, onSuccess, onFailure)
    }

    fun getBalance(
        type: String,
        callback: (Long?) -> Unit
    ) {
        if (userId == null) {
            callback(null)
            return
        }

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val walletId = userDoc.getString("walletId") ?: return@addOnSuccessListener callback(null)
                db.collection("wallets").document(walletId)
                    .get()
                    .addOnSuccessListener { walletDoc ->
                        val balance = walletDoc.getLong(type)
                        callback(balance)
                    }
                    .addOnFailureListener { callback(null) }
            }
            .addOnFailureListener { callback(null) }
    }

    fun getTotalBalance(callback: (Long?) -> Unit) {
        if (userId == null) {
            callback(null)
            return
        }

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val walletId = userDoc.getString("walletId") ?: return@addOnSuccessListener callback(null)
                db.collection("wallets").document(walletId)
                    .get()
                    .addOnSuccessListener { walletDoc ->
                        val status = walletDoc.getLong(TYPE_STATUS) ?: 0L
                        val referral = walletDoc.getLong(TYPE_REFERRAL) ?: 0L
                        val purchase = walletDoc.getLong(TYPE_PURCHASE) ?: 0L
                        callback(status + referral + purchase)
                    }
                    .addOnFailureListener { callback(null) }
            }
            .addOnFailureListener { callback(null) }
    }
}
