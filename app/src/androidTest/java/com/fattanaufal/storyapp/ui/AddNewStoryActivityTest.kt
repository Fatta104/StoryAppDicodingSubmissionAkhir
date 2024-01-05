package com.fattanaufal.storyapp.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fattanaufal.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.fattanaufal.storyapp.R


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddNewStoryActivityTest {

    private val email = "test9020@gmail.com"
    private val password = "123123123"
    private val username = "testname"

    @get:Rule
    val activityScenario = ActivityScenarioRule(LoginActivity::class.java)


    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // Cek keberadaan halaman login
        Espresso.onView(withId(R.id.banner_register)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        // isi email dan password
        Espresso.onView(withId(R.id.emailEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.emailEditText))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(withId(R.id.passwordEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.passwordEditText))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        // Klik tombol login
        Espresso.onView(withId(R.id.signInButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.signInButton)).perform(ViewActions.click())

        // Cek keberadaan halaman list stories
        Espresso.onView(withId(R.id.tv_username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tv_username)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    username
                )
            )
        )
        Espresso.onView(withId(R.id.rv_stories))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun add_story_success() {

        // Pengguna menekan tombol fab
        Espresso.onView(withId(R.id.btn_story_add))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_story_add)).perform(ViewActions.click())


        Espresso.onView(withId(R.id.btnGallery))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btnGallery)).perform(ViewActions.click())

        // isi email dan password
        Espresso.onView(withId(R.id.edt_desc))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.edt_desc)).perform(
            ViewActions.typeText("test"),
            ViewActions.closeSoftKeyboard()
        )

        Espresso.onView(withId(R.id.switch_loc))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.switch_loc)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.btnSend))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btnSend)).perform(ViewActions.click())

        // cek tampilan dialog
        Espresso.onView(withId(R.id.tv_info_registrasi))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_yes))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_yes)).perform(ViewActions.click())

        // cek tampilan dialog
        Espresso.onView(withId(R.id.tv_info_registrasi))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_dismiss))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_dismiss)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.rv_stories))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                ViewActions.click()
            ))

        Espresso.onView(withId(R.id.tv_sender))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tv_sender))
            .check(ViewAssertions.matches(ViewMatchers.withText(username)))
    }
}