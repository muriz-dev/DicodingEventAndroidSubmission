package com.example.dicodingeventandroidsubmission

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.dicodingeventandroidsubmission.data.preferences.SettingPreferences
import com.example.dicodingeventandroidsubmission.data.preferences.dataStore
import com.example.dicodingeventandroidsubmission.databinding.ActivityMainBinding
import com.example.dicodingeventandroidsubmission.ui.search.SearchActivity
import com.example.dicodingeventandroidsubmission.ui.setting.SettingActivity
import com.example.dicodingeventandroidsubmission.ui.setting.SettingViewModel
import com.example.dicodingeventandroidsubmission.ui.setting.SettingViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val settingViewModel: SettingViewModel by lazy {
        val pref = SettingPreferences.getInstance(application.dataStore)
        ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        settingViewModel.getThemeSettings().observe(this) { isDarkMode ->
            SettingActivity.switchDarkMode(isDarkMode)
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_search -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}