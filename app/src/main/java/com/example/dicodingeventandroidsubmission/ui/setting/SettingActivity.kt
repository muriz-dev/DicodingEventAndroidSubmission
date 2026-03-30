package com.example.dicodingeventandroidsubmission.ui.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventandroidsubmission.data.preferences.SettingPreferences
import com.example.dicodingeventandroidsubmission.data.preferences.dataStore
import com.example.dicodingeventandroidsubmission.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by lazy {
        val pref = SettingPreferences.getInstance(application.dataStore)
        ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingViewModel.getThemeSettings().observe(this) { isDarkMode ->
            switchDarkMode(isDarkMode)
            setSwitchState(isDarkMode)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setSwitchState(isDarkMode: Boolean) {
        binding.darkModeSwitch.isChecked = isDarkMode
    }

    companion object {
        fun switchDarkMode(isDarkMode: Boolean) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}