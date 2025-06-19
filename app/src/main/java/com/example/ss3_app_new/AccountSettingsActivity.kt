package com.example.ss3_app_new

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ss3_app_new.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "User")
        binding.profileName.text = userName

        // Back button
        binding.backArrow.setOnClickListener {
            finish()
        }

        // Logout
        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Change Name
        binding.changeNameButton.setOnClickListener {
            showUpdateDialog("Change Name", "Enter new name:", "user_name") { newName ->
                binding.profileName.text = newName
            }
        }

        // Change Email
        binding.changeEmailButton.setOnClickListener {
            showUpdateDialog("Change Email", "Enter new email:", "user_email")
        }

        // Change Password
        binding.changePasswordButton.setOnClickListener {
            showUpdateDialog("Change Password", "Enter new password:", "user_password", true)
        }
    }

    private fun showUpdateDialog(
        title: String,
        hint: String,
        prefKey: String,
        isPassword: Boolean = false,
        onSuccess: ((String) -> Unit)? = null
    ) {
        val editText = EditText(this)
        editText.hint = hint
        if (isPassword) editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        else if (prefKey == "user_email") editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val newValue = editText.text.toString().trim()
                if (newValue.isNotEmpty()) {
                    val editor = getSharedPreferences("UserData", MODE_PRIVATE).edit()
                    editor.putString(prefKey, newValue)
                    editor.apply()
                    Toast.makeText(this, "$title updated", Toast.LENGTH_SHORT).show()
                    onSuccess?.invoke(newValue)
                } else {
                    Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
