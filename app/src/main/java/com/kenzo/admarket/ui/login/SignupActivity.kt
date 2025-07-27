package com.kenzo.admarket.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kenzo.admarket.databinding.ActivityLoginBinding
import com.kenzo.admarket.databinding.ActivitySignupBinding
import com.kenzo.admarket.model.User
import com.kenzo.admarket.ui.user.WelcomeUserActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString()
            val contact = binding.contactNumber.text.toString()
            val state = binding.state.text.toString()
            val district = binding.district.text.toString()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString()
            val sponsorId = binding.sponsorId.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty() ||
                state.isEmpty() || district.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields except Sponsor ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // STEP 1: Find last referral ID
// 1. Query the highest referralNumber
            db.collection("users")
                .orderBy("referralNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { snapshot ->
                    val lastNumber = snapshot.documents.firstOrNull()?.getLong("referralNumber") ?: 0L
                    val nextNumber = (lastNumber + 1).toInt()
                    val myReferralId = "AM" + String.format("%06d", nextNumber)

                    // 2. Create wallet
                    val walletRef = db.collection("wallets").document()
                    val walletData = hashMapOf(
                        "userId" to email,
                        "statusBalance" to 0,
                        "referBalance" to 0,
                        "purchaseBalance" to 0,
                        "lastUpdated" to FieldValue.serverTimestamp()
                    )

                    walletRef.set(walletData)
                        .addOnSuccessListener {
                            // 3. Create user object with referralNumber
                            val user = User(
                                name = name,
                                email = email,
                                contact = contact,
                                state = state,
                                district = district,
                                sponsorId = if (sponsorId.isEmpty()) null else sponsorId,
                                myReferralId = myReferralId,
                                referralNumber = nextNumber,    // <<-- Save it
                                verified = false,
                                role = "user",
                                password = password,
                                walletId = walletRef.id,
                                createdAt = Timestamp.now()
                            )

                            db.collection("users")
                                .document(email)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Registered! Contact admin for verification.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(this, WelcomeUserActivity::class.java).apply {
                                        putExtra("name", name)
                                        putExtra("email", email)
                                        putExtra("contact", contact)
                                        putExtra("sponsorId", sponsorId)
                                        putExtra(
                                            "date",
                                            SimpleDateFormat(
                                                "dd-MMM-yyyy hh:mm a",
                                                Locale.getDefault()
                                            ).format(Date())
                                        )
                                    }
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "User creation failed: ${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Wallet creation failed: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to generate referral ID",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }




        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
