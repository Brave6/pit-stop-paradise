package com.seth.pitstopparadise

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.seth.pitstopparadise.data.RegisterRequest
import com.seth.pitstopparadise.databinding.ActivityRegisterBinding
import com.seth.pitstopparadise.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup Toolbar with Back Button
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //Register button click
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                val user = RegisterRequest(
                    username = binding.etUsername.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    password = binding.etPassword.text.toString()
                )
                viewModel.registerUser(user)
            }
        }

        //Observe result from ViewModel
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
        val user = binding.etUsername.text.toString().trim()
        val mail = binding.etEmail.text.toString().trim()
        val pass = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

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
