package com.seth.pitstopparadise

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.seth.pitstopparadise.di.TestNetworkModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer
    private lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Point Retrofit to MockWebServer
        TestNetworkModule.mockWebServerUrl = mockWebServer.url("/").toString()
        hiltRule.inject()

        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
        mockWebServer.shutdown()
    }

    @Test
    fun login_withValidCredentials_navigatesToMainActivity() {
        // Mock successful login response
        mockWebServer.enqueue(
            MockResponse()
                .setBody(
                    """
                    {
                        "token": "mocked_jwt_token",
                        "email": "test@example.com"
                    }
                    """.trimIndent()
                )
                .setResponseCode(200)
        )

        // Fill login form
        onView(withId(R.id.loginUsername))
            .perform(scrollTo(), replaceText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.loginPassword))
            .perform(scrollTo(), replaceText("test123"), closeSoftKeyboard())
        onView(withId(R.id.loginBtnLogin))
            .perform(scrollTo(), click())

        // Wait for network call and state update
        onView(isRoot()).perform(waitFor(1500))

        // Check that MainActivity launched by verifying a view inside it
        // Replace R.id.main if your MainActivity root has a different ID
        onView(withId(R.id.main)).check(matches(isDisplayed()))
    }


    // Helper to wait for coroutines / network
    private fun waitFor(delay: Long) = object : androidx.test.espresso.ViewAction {
        override fun getConstraints() = isRoot()
        override fun getDescription() = "Wait for $delay milliseconds."
        override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View?) {
            uiController.loopMainThreadForAtLeast(delay)
        }
    }
}
