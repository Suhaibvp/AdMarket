package com.kenzo.admarket.ui.user

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kenzo.admarket.R
import com.kenzo.admarket.databinding.ActivityAdminBinding
import com.kenzo.admarket.databinding.ActivityUserBinding
import com.kenzo.admarket.ui.admin.AdminDashboardFragment
import com.kenzo.admarket.ui.admin.ManageUsersFragment

class UserActivity:AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
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
            .replace(R.id.fragmentContainer, UserDashboardFragment())
            .commit()

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.nav_dashboard -> switchFragment(UserDashboardFragment())
                R.id.nav_clubMembers->switchFragment(MyClubMemberFragment())
                R.id.nav_wallet->switchFragment(WalletFragment())
                R.id.nav_our_company -> switchFragment(MyCompanyFragment())

                R.id.nav_redeem_coupon -> switchFragment(RedeemCouponFragment())

                //R.id.nav_settings -> switchFragment(UserSettingsFragment())
            }
            true
        }
        binding.profileIcon.setOnClickListener {
            switchFragment(UserSettingsFragment())
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}