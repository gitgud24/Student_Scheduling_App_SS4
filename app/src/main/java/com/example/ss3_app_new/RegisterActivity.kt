package com.example.ss3_app_new

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ss3_app_new.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val name = binding.nameInput.text.toString().trim()

            //save user details for temporary authentication here - saved to android device app data
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                val editor = sharedPref.edit()

                editor.putString("user_email", email)
                editor.putString("user_password", password)
                editor.putString("user_name", name)

                editor.apply()

                Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }

        val text = "Already have an account? Log in"
        val spannableString = SpannableString(text)

        val loginClickable = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val start = text.indexOf("Log in")
        val end = start + "Log in".length
        spannableString.setSpan(loginClickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.loginPrompt.text = spannableString
        binding.loginPrompt.movementMethod = LinkMovementMethod.getInstance()
    }
}