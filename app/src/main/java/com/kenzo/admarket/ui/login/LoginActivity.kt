package com.kenzo.admarket.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.kenzo.admarket.databinding.ActivityLoginBinding
import com.kenzo.admarket.ui.admin.AdminActivity
import com.kenzo.admarket.ui.developer.DeveloperActivity
import com.kenzo.admarket.ui.user.UserActivity



class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Check if user already logged in
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = prefs.getString("user_id", null)
        val role = prefs.getString("user_role", null)

        if (userId != null && role != null) {
            when (role) {
                "admin" -> {
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                }
                "user" -> {
                    startActivity(Intent(this, UserActivity::class.java))
                    finish()
                }
                "developer" -> {
                    startActivity(Intent(this, DeveloperActivity::class.java))
                    finish()
                }
            }
            return // Skip rest of the login page
        }

        // Setup login page
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        binding.loginButton.setOnClickListener {
            val username = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔍 Query Firestore
            firestore.collection("users")
                .whereEqualTo("name", username)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userDoc = querySnapshot.documents[0]
                        val storedPassword = userDoc.getString("password") ?: ""
                        val role = userDoc.getString("role") ?: ""
                        val name=userDoc.getString("name")?:""
                        val isverifyed=userDoc.getBoolean("isVerified")

                        if (password == storedPassword) {
                            // ✅ Save session
                            val editor = getSharedPreferences("user_session", MODE_PRIVATE).edit()
                            editor.putString("user_id", userDoc.id)
                            editor.putString("user_role", role)
                            editor.putString("user_name",name)
                            editor.apply()

                            // ✅ Navigate
                            when (role) {
                                "admin" -> {
                                    startActivity(Intent(this, AdminActivity::class.java))
                                    finish()
                                }
                                "user" -> {
                                    startActivity(Intent(this, UserActivity::class.java))
                                    finish()
                                }
                                "developer" -> {
                                startActivity(Intent(this, DeveloperActivity::class.java))
                                finish()
                            }
                                else -> {
                                    Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        binding.tvCreateUserButton.setOnClickListener {
            val intent= Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
