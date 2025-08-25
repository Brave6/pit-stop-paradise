package com.seth.pitstopparadise

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.seth.pitstopparadise.di.TestNetworkModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private lateinit var mockWebServer: MockWebServer

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // Set the TestNetworkModule URL to MockWebServer
        TestNetworkModule.mockBaseUrl = mockWebServer.url("/").toString()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun login_withValidCredentials_showsSuccessToast() {
        val response = """
            {
                "_id": "123",
                "username": "TestUser",
                "email": "test@email.com"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(response).setResponseCode(200))

        onView(withId(R.id.loginUsername))
            .perform(typeText("test@email.com"), closeSoftKeyboard())
        onView(withId(R.id.loginPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginBtnLogin)).perform(click())

        onView(withText("Login success!"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }
}
