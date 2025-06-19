package com.example.ss3_app_new

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ss3_app_new.databinding.ActivityAddTaskBinding
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private var calendar = Calendar.getInstance()

    private var editingTask: Task? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the Task from intent safely
        editingTask = intent.getParcelableExtra("extra_task")

        if (editingTask != null) {
            // Pre-fill fields for editing
            binding.titleInput.setText(editingTask!!.title)
            binding.descriptionInput.setText(editingTask!!.description)
            calendar.timeInMillis = editingTask!!.dateTime

            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            binding.dateButton.text = String.format("%02d/%02d/%04d • %02d:%02d", day, month, year, hour, minute)
        } else {
            // Set default date button text if adding new task
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            binding.dateButton.text = String.format("%02d/%02d/%04d • %02d:%02d", day, month, year, hour, minute)
        }

        binding.dateButton.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(
                        this,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            binding.dateButton.text = String.format(
                                "%02d/%02d/%04d • %02d:%02d",
                                dayOfMonth,
                                month + 1,
                                year,
                                hourOfDay,
                                minute
                            )
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleInput.text.toString().trim()
            val description = binding.descriptionInput.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskDateTime = calendar.timeInMillis

            if (editingTask != null) {
                // Update existing task: delete old, add new with updated info
                TaskStorage.deleteTask(editingTask!!)
                val updatedTask = Task(title, description, taskDateTime)
                TaskStorage.addTask(updatedTask)
                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show()
            } else {
                // Add new task
                val newTask = Task(title, description, taskDateTime)
                TaskStorage.addTask(newTask)
                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
            }

            // Return to MainActivity without app closing unexpectedly
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        setupBottomNavigation()
    }

    private fun sendTaskCreatedNotification(taskTitle: String) {
        // Your existing notification permission & sending code here (omitted for brevity)
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.itemIconTintList = null

        binding.bottomNav.menu.findItem(R.id.nav_home).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_home)
        binding.bottomNav.menu.findItem(R.id.nav_add).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_add_selected)
        binding.bottomNav.menu.findItem(R.id.nav_settings).icon =
            ContextCompat.getDrawable(this, R.drawable.ic_settings)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
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
}
