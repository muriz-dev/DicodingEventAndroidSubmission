package com.example.dicodingeventandroidsubmission.ui.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.dicodingeventandroidsubmission.data.preferences.SettingPreferences
import com.example.dicodingeventandroidsubmission.data.preferences.dataStore
import com.example.dicodingeventandroidsubmission.databinding.ActivitySettingBinding
import com.example.dicodingeventandroidsubmission.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var workManager: WorkManager
    private val settingViewModel: SettingViewModel by lazy {
        val pref = SettingPreferences.getInstance(application.dataStore)
        ViewModelProvider(this, SettingViewModelFactory(pref))[SettingViewModel::class.java]
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startDailyReminder()
            return@registerForActivityResult
        }

        val message = if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            "Izin ditolak permanen. Aktifkan di Pengaturan."
        } else {
            "Izin notifikasi ditolak."
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Reset switch karena izin ditolak
        setDailyReminderSwitchState(false)
        settingViewModel.saveDailyReminderSetting(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workManager = WorkManager.getInstance(this)

        settingViewModel.getThemeSettings().observe(this) { isDarkMode ->
            switchDarkMode(isDarkMode)
            setDarkModeSwitchState(isDarkMode)
        }

        settingViewModel.getDailyReminderSettings().observe(this) { isDailyReminder ->
            setDailyReminderSwitchState(isDailyReminder)
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        binding.dailyReminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            handleDailyReminder(isChecked)
            settingViewModel.saveDailyReminderSetting(isChecked)
        }
    }


    private fun handleDailyReminder(isChecked: Boolean) {
        if (!isChecked) {
            cancelDailyReminder()
            return
        }

        if (Build.VERSION.SDK_INT >= 33) {
            checkAndRequestNotificationPermission()
        } else {
            startDailyReminder()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted) {
            startDailyReminder()
            return
        }

        // Jika butuh penjelasan (Rationale)
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Toast.makeText(this, "Izin notifikasi diperlukan untuk update harian.", Toast.LENGTH_SHORT).show()
        }

        // Minta izin
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun startDailyReminder() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            DailyReminderWorker::class.java,
            24, TimeUnit.HOURS // Interval 24 jam
        )
            .setConstraints(constraints)
            .addTag(REMINDER_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_VALUE,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    private fun cancelDailyReminder() {
        workManager.cancelUniqueWork(REMINDER_VALUE)
    }

    private fun setDarkModeSwitchState(isDarkMode: Boolean) {
        binding.darkModeSwitch.isChecked = isDarkMode
    }

    private fun setDailyReminderSwitchState(isDailyReminder: Boolean) {
        binding.dailyReminderSwitch.isChecked = isDailyReminder
    }

    companion object {
        const val REMINDER_VALUE = "daily_reminder_work"
        const val REMINDER_TAG = "daily_reminder_tag"

        fun switchDarkMode(isDarkMode: Boolean) {
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}