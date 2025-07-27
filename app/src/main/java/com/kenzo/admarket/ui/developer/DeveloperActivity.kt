package com.kenzo.admarket.ui.developer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kenzo.admarket.R
import com.kenzo.admarket.databinding.ActivityAdminBinding
import com.kenzo.admarket.databinding.ActivityDeveloperBinding
import com.kenzo.admarket.ui.admin.AdminSettingsFragment
import com.kenzo.admarket.ui.admin.CreateCouponFragment
import com.kenzo.admarket.ui.admin.ManageUsersFragment
import com.kenzo.admarket.ui.admin.UserDetailsFragment
import com.kenzo.admarket.ui.login.LoginActivity

class DeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeveloperBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, FragmentDashboard())
            .commit()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_dashboard -> switchFragment(FragmentDashboard())
                R.id.nav_logout -> {
                    val prefs = this.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
//                R.id.create_coupon ->switchFragment(CreateCouponFragment())
//                R.id.nav_settings -> switchFragment(AdminSettingsFragment())
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
