package com.example.vasudevkutumbhakam.ui.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vasudevkutumbhakam.R
import com.example.vasudevkutumbhakam.databinding.ActivityMainBinding
import com.example.vasudevkutumbhakam.databinding.CustomBottomNavBinding
import com.example.vasudevkutumbhakam.model.NavItem
import com.example.vasudevkutumbhakam.ui.fragment.AssistFragment
import com.example.vasudevkutumbhakam.ui.fragment.HomeFragment
import com.example.vasudevkutumbhakam.ui.fragment.LoansFragment
import com.example.vasudevkutumbhakam.ui.fragment.PlayEarnFragment
import com.example.vasudevkutumbhakam.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingBottomNavItem: CustomBottomNavBinding
    private lateinit var navItems: Map<String, NavItem>
    private var currentSelected = "home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(16, systemBars.top, 16, systemBars.bottom)
            insets
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        // Bind custom bottom nav
//        bindingBottomNavItem = CustomBottomNavBinding.bind(binding.bottomNav)

        // 2. Bind the included view using its ID and pass it as a View
        val bottomNavView = findViewById<View>(R.id.bottom_nav)
        bindingBottomNavItem = CustomBottomNavBinding.bind(bottomNavView)

        // Add modern back press callback
        onBackPressedDispatcher.addCallback(this) {
            if (currentSelected != "home") {
                updateNavSelection("home")
            } else {
                finish()
            }
        }

        // Setup nav items
        navItems = mapOf(
            "home" to NavItem(
                findViewById(R.id.home_nav),
                findViewById(R.id.home_nav_iv),
                findViewById(R.id.home_nav_tv),
                R.drawable.home,
                R.drawable.selected_home
            ),
            "play_earn" to NavItem(
                findViewById(R.id.play_earn_nav),
                findViewById(R.id.play_earn_nav_iv),
                findViewById(R.id.play_earn_nav_tv),
                R.drawable.play_earn,
                R.drawable.selected_play_earn
            ),
            "loans" to NavItem(
                findViewById(R.id.loans_nav),
                findViewById(R.id.loans_nav_iv),
                findViewById(R.id.loans_nav_tv),
                R.drawable.loans,
                R.drawable.selected_loans
            ),
            "assist" to NavItem(
                findViewById(R.id.assist_nav),
                findViewById(R.id.assist_nav_iv),
                findViewById(R.id.assist_nav_tv),
                R.drawable.assist,
                R.drawable.selected_assist
            ),
            "profile" to NavItem(
                findViewById(R.id.profile_nav),
                findViewById(R.id.profile_nav_iv),
                findViewById(R.id.profile_nav_tv),
                R.drawable.profile,
                R.drawable.selected_profile
            )
        )

        // Set click listeners and default selected
        navItems.forEach { (key, navItem) ->
            navItem.container.setOnClickListener {
                if (key != currentSelected) {
                    updateNavSelection(key)
                }
            }
        }

        updateNavSelection(currentSelected) // Initially select "home"

    }

    private fun updateNavSelection(selectedKey: String) {
        navItems.forEach { (key, item) ->
            val isSelected = key == selectedKey
            item.icon.setImageResource(if (isSelected) item.selectedIcon else item.icons) // placeholder for unselected
            item.text.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            item.text.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (isSelected) R.color.colorPrimary else R.color.textColorPrimary
                )
            )
        }
        // Replace fragment
        val fragment = when (selectedKey) {
            "home" -> HomeFragment()
            "play_earn" -> PlayEarnFragment()
            "loans" -> LoansFragment()
            "assist" -> AssistFragment()
            "profile" -> ProfileFragment()
            else -> HomeFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        currentSelected = selectedKey
    }

}