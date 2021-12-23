package com.posse.kotlin1.calendar

import android.graphics.Color
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.posse.kotlin1.calendar.view.deleteConfirmation.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogFragmentTest {

    private lateinit var scenario: FragmentScenario<DeleteFragmentDialog>

    @Before
    fun setup() {
        val bundleOf = bundleOf(
            ARG_DIALOG_TEXT to "Test Text",
            ARG_CONFIRM_TEXT to "Delete",
            ARG_CONFIRM_COLOR to Color.RED,
            ARG_BLOCK_BOX to true
        )
        scenario = launchFragmentInContainer(bundleOf, themeResId = R.style.Theme_AlcoCalendar)
    }

    @Test
    fun fragment_testBundle() {
        onView(withId(R.id.dialogText)).check(matches(withText("Test Text")))
    }

    @Test
    fun fragment_testSetBlocked() {
        var isBlocked: Boolean? = null
        scenario.onFragment { fragment ->
            fragment.setListener { isBlocked = it }
        }
        onView(withId(R.id.blockBtn)).perform(click())
        onView(withId(R.id.confirmButton)).perform(click())
        Assert.assertEquals(true, isBlocked)
    }

    @After
    fun end() {
        scenario.close()
    }
}