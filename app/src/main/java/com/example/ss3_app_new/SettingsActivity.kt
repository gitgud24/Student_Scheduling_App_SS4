package com.example.ss3_app_new

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ss3_app_new.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val PREFS_NAME = "app_preferences"
    private val NOTIFICATIONS_KEY = "notifications_enabled"
    private val THEME_KEY = "selected_theme"

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (isGranted) {
            sharedPrefs.edit().putBoolean(NOTIFICATIONS_KEY, true).apply()
            binding.notificationSwitch.isChecked = true
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
        } else {
            sharedPrefs.edit().putBoolean(NOTIFICATIONS_KEY, false).apply()
            binding.notificationSwitch.isChecked = false
            Toast.makeText(this, "Notifications permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val notificationsEnabled = sharedPrefs.getBoolean(NOTIFICATIONS_KEY, true)
        binding.notificationSwitch.isChecked = notificationsEnabled

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isChecked) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        sharedPrefs.edit().putBoolean(NOTIFICATIONS_KEY, true).apply()
                        Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    sharedPrefs.edit().putBoolean(NOTIFICATIONS_KEY, false).apply()
                    Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
                }
            } else {
                sharedPrefs.edit().putBoolean(NOTIFICATIONS_KEY, isChecked).apply()
                Toast.makeText(
                    this,
                    if (isChecked) "Notifications enabled" else "Notifications disabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.accountSettingsButton.setOnClickListener {
            startActivity(Intent(this, AccountSettingsActivity::class.java))
        }

        binding.submitFeedbackButton.setOnClickListener {
            val rating = binding.ratingBar.rating
            val comment = binding.feedbackInput.text.toString().trim()
            if (comment.isNotEmpty()) {
                val feedbackMsg = "Rating: $rating\nComment: $comment"
                Toast.makeText(this, "Feedback Submitted\n$feedbackMsg", Toast.LENGTH_LONG).show()
                binding.feedbackInput.text?.clear()
                binding.ratingBar.rating = 0f
            } else {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
            }
        }

        val languages = resources.getStringArray(R.array.language_selection_array)
        val languageAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_selection_array,
            R.layout.spinner_item
        )
        languageAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.languageSpinner.adapter = languageAdapter

        val themes = resources.getStringArray(R.array.theme_selection_array)
        val themeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.theme_selection_array,
            R.layout.spinner_item
        )
        themeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.themeSpinner.adapter = themeAdapter

        val savedTheme = sharedPrefs.getString(THEME_KEY, "African Sunset")
        val selectedIndex = themes.indexOf(savedTheme)
        if (selectedIndex != -1) binding.themeSpinner.setSelection(selectedIndex)

        binding.themeSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val selectedTheme = themes[position]
                val currentTheme = sharedPrefs.getString(THEME_KEY, "African Sunset")
                if (selectedTheme != currentTheme) {
                    sharedPrefs.edit().putString(THEME_KEY, selectedTheme).apply()
                    recreate()
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        setupBottomNavigation()
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        when (prefs.getString(THEME_KEY, "African Sunset")) {
            "Light Mode" -> setTheme(R.style.LightTheme)
            "Dark Mode" -> setTheme(R.style.DarkTheme)
            else -> setTheme(R.style.SunsetTheme)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.menu.findItem(R.id.nav_home).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_home)
        binding.bottomNav.menu.findItem(R.id.nav_add).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_add)
        binding.bottomNav.menu.findItem(R.id.nav_settings).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_settings_selected)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTaskActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }
    }
}
