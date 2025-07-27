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

    @Inject lateinit var sessionManager: SessionManager

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

            if (token.isNullOrEmpty()) {
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreenActivity, PitstopActivity::class.java))
            }

            overridePendingTransition(0, 0)
            finish()
        }
    }
}