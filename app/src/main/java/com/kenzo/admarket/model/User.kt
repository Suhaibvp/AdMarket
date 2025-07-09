package com.kenzo.admarket.model

import com.google.firebase.Timestamp

data class User(
    val name: String = "",
    val email: String = "",
    val contact: String = "",
    val state: String = "",
    val district: String = "",
    val sponsorId: String? = null,
    val myReferralId: String = "",
    val referralNumber: Int = 0,           // <<-- NEW FIELD
    val verified: Boolean = false,
    val role: String = "",
    val password: String = "",
    val walletId: String = "",
    val createdAt: Timestamp? = null
)


