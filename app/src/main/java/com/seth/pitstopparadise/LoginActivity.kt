package com.seth.pitstopparadise

import android.animation.Animator
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
import android.view.animation.AnimationUtils
import android.view.animation.Animation



@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //Using repeatOnLifecycle for safer state collection
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.loginState.collectLatest { state ->
                    when (state) {
                        is LoginUiState.Idle -> {
                            showLoading(false)
                            binding.loginBtnLogin.isEnabled = true
                        }
                        is LoginUiState.Loading -> {
                            showLoading(true)
                            binding.loginBtnLogin.isEnabled = false
                        }
                        is LoginUiState.Success -> {
                            binding.loginBtnLogin.isEnabled = true
                            binding.loadingAnimation.repeatCount = 0
                            binding.loadingAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator) {}
                                override fun onAnimationEnd(animation: Animator) {
                                    binding.loadingAnimation.removeAnimatorListener(this)
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    showToast("Login successful")
                                    finish()
                                }
                                override fun onAnimationCancel(animation: Animator) {}
                                override fun onAnimationRepeat(animation: Animator) {}
                            })
                        }
                        is LoginUiState.Error -> {
                            showLoading(false)
                            binding.loginBtnLogin.isEnabled = true
                            showToast("Login failed: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        val overlay = binding.loadingOverlay
        val anim = binding.loadingAnimation

        if (show && overlay.visibility != View.VISIBLE) {
            anim.speed = 2.5f // faster animation
            anim.repeatCount = 0

            overlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            overlay.visibility = View.VISIBLE
            anim.playAnimation()
        } else if (!show && overlay.isVisible) {
            val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                    overlay.visibility = View.GONE
                    anim.pauseAnimation()
                }
                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            })
            overlay.startAnimation(fadeOut)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

