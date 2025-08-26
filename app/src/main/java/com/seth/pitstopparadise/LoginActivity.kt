package com.seth.pitstopparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.seth.pitstopparadise.databinding.ActivityLoginBinding
import com.seth.pitstopparadise.viewmodel.AuthViewModel
import com.seth.pitstopparadise.viewmodel.LoginUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        binding.loginBtnLogin.setOnClickListener {
            val email = binding.loginUsername.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill all fields")
            } else {
                authViewModel.login(email, password)
            }
        }

        binding.txtRegister?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // ✅ Using repeatOnLifecycle for safer state collection
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collectLatest { state ->
                    when (state) {
                        is LoginUiState.Idle -> {
                            binding.progressBar?.isVisible = false
                            binding.loginBtnLogin.isEnabled = true
                        }
                        is LoginUiState.Loading -> {
                            binding.progressBar?.isVisible = true
                            binding.loginBtnLogin.isEnabled = false
                        }
                        is LoginUiState.Success -> {
                            binding.progressBar?.isVisible = false
                            binding.loginBtnLogin.isEnabled = true
                            showToast("Login successful")
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                        is LoginUiState.Error -> {
                            binding.progressBar?.isVisible = false
                            binding.loginBtnLogin.isEnabled = true
                            showToast("Login failed: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

