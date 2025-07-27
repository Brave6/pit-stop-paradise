package com.seth.pitstopparadise

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.seth.pitstopparadise.data.RegisterRequest
import com.seth.pitstopparadise.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlin.getValue

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var username: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var registerButton: MaterialButton

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.etUsername)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        confirmPassword = findViewById(R.id.etConfirmPassword)
        registerButton = findViewById(R.id.btnRegister)

        registerButton.setOnClickListener {
            if (validateInputs()) {
                val user = RegisterRequest(
                    username = username.text.toString().trim(),
                    email = email.text.toString().trim(),
                    password = password.text.toString()
                )
                viewModel.registerUser(user)
            }
        }

        // 🔁 Observe result from ViewModel
        lifecycleScope.launchWhenStarted {
            viewModel.registerState.collectLatest { result ->
                if (result.isNotBlank()) {
                    showToast(result)
                    if (result.contains("success", true)) finish()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val user = username.text.toString().trim()
        val mail = email.text.toString().trim()
        val pass = password.text.toString()
        val confirm = confirmPassword.text.toString()

        return when {
            user.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirm.isEmpty() -> {
                showToast("Please fill in all fields")
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(mail).matches() -> {
                showToast("Invalid email format")
                false
            }
            pass.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            pass != confirm -> {
                showToast("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
