package com.seth.pitstopparadise

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.hamcrest.Matcher

fun clickBottomNavItem(@IdRes itemId: Int): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(BottomNavigationView::class.java)

        override fun getDescription(): String = "Click BottomNavigationView menu item with ID $itemId"

        override fun perform(uiController: UiController?, view: View?) {
            val bottomNav = view as BottomNavigationView
            bottomNav.selectedItemId = itemId
            uiController?.loopMainThreadUntilIdle()
        }
    }
}
