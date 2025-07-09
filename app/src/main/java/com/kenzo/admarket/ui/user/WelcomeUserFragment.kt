package com.kenzo.admarket.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kenzo.admarket.databinding.FragmentWelcomeNoteBinding
import com.kenzo.admarket.ui.login.LoginActivity

class WelcomeUserActivity : AppCompatActivity() {

    private lateinit var binding: FragmentWelcomeNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentWelcomeNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val contact = intent.getStringExtra("contact") ?: ""
        val sponsorId = intent.getStringExtra("sponsorId") ?: ""
        val date = intent.getStringExtra("date") ?: ""

        binding.tvName.text = name
        binding.tvEmail.text = "Email Id: $email"
        binding.tvReferral.text = sponsorId
        binding.tvPassword.text = contact // using mobile number as password
        binding.tvDoj.text = date
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }
    }
}
