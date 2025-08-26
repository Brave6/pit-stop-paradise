package com.seth.pitstopparadise

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.*
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        hiltRule.inject()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun bottomNavigation_switchesFragments() {
        // Check Home fragment is displayed by default
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()))

        // Click Offers tab
        onView(withId(R.id.bottom_nav))
            .perform(clickBottomNavItem(R.id.nav_offers))
        onView(withId(R.id.nav_offers)).check(matches(isDisplayed()))

        // Click Profile tab
        onView(withId(R.id.bottom_nav))
            .perform(clickBottomNavItem(R.id.nav_profile))
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed()))
    }

}
