package com.example.ss3_app_new

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ss3_app_new.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var weekCalendarAdapter: WeekCalendarAdapter

    companion object {
        const val CHANNEL_ID = "task_notifications_channel"
        const val EDIT_TASK_REQUEST_CODE = 1
    }

    // ✅ Notification permission request launcher
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }

    // ✅ Apply saved theme
    private fun applySavedTheme() {
        val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        when (prefs.getString("selected_theme", "African Sunset")) {
            "Light Mode" -> setTheme(R.style.LightTheme)
            "Dark Mode" -> setTheme(R.style.DarkTheme)
            else -> setTheme(R.style.SunsetTheme)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermissionIfNeeded()
        createNotificationChannel()

        // Task list setup
        taskAdapter = TaskAdapter(
            TaskStorage.getTasksSortedByDate().toMutableList(),
            onEditClick = { taskToEdit ->
                val intent = Intent(this, AddTaskActivity::class.java)
                intent.putExtra("extra_task", taskToEdit)
                startActivityForResult(intent, EDIT_TASK_REQUEST_CODE)
            },
            onDeleteClick = { taskToDelete ->
                TaskStorage.deleteTask(taskToDelete)
                taskAdapter.updateTasks(TaskStorage.getTasksSortedByDate().toMutableList())
            }
        )
        binding.taskRecycler.layoutManager = LinearLayoutManager(this)
        binding.taskRecycler.adapter = taskAdapter

        // Week calendar setup
        val (monthYear, weekDays) = getCurrentWeekWithHeader()
        weekCalendarAdapter = WeekCalendarAdapter(monthYear, weekDays)
        binding.weekCalendarRecycler.layoutManager = LinearLayoutManager(this)
        binding.weekCalendarRecycler.adapter = weekCalendarAdapter

        binding.weekCalendarRecycler.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.menu.findItem(R.id.nav_home).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_home_selected)
        binding.bottomNav.menu.findItem(R.id.nav_add).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_add)
        binding.bottomNav.menu.findItem(R.id.nav_settings).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_settings)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTaskActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> true
            }
        }
    }

    private fun getCurrentWeekWithHeader(): Pair<String, List<Int>> {
        val calendar = Calendar.getInstance()
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthYear = monthYearFormat.format(calendar.time)

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        val days = mutableListOf<Int>()
        for (i in 0 until 7) {
            days.add(calendar.get(Calendar.DAY_OF_MONTH))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return Pair(monthYear, days)
    }

    override fun onResume() {
        super.onResume()
        taskAdapter.updateTasks(TaskStorage.getTasksSortedByDate().toMutableList())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_TASK_REQUEST_CODE && resultCode == RESULT_OK) {
            taskAdapter.updateTasks(TaskStorage.getTasksSortedByDate().toMutableList())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Notifications"
            val descriptionText = "Notifications for newly created tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // ✅ Ask for notification permission on first launch if needed
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val prefs = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            val askedBefore = prefs.getBoolean("asked_notification_permission", false)

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED && !askedBefore
            ) {
                notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                prefs.edit().putBoolean("asked_notification_permission", true).apply()
            }
        }
    }
}
