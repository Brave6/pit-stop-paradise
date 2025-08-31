package com.seth.pitstopparadise

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.seth.pitstopparadise.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private val splashDelay = 1000L // 1 second

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        lifecycleScope.launch {
            delay(splashDelay)

            val token = sessionManager.token.first()

            val intent = if (token.isNullOrEmpty()) {
                // No session → show GetStartedFragment first
                Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
                    putExtra("SHOW_GET_STARTED", true)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                // Already logged in → go straight to main content
                Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
}
