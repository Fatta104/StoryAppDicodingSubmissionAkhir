package com.fattanaufal.storyapp.ui

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fattanaufal.storyapp.utils.EspressoIdlingResource
import com.fattanaufal.storyapp.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {
    private val email = "test9020@gmail.com"
    private val password = "123123123"
    private val username = "testname"


    @get:Rule
    val activityScenario = ActivityScenarioRule(LoginActivity::class.java)


    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_success() {

        // Cek keberadaan halaman login
        Espresso.onView(withId(R.id.banner_register))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

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
        Espresso.onView(withId(R.id.tv_username))
            .check(ViewAssertions.matches(ViewMatchers.withText(username)))
        Espresso.onView(withId(R.id.rv_stories))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun logout_success() {

        // Melakukan login terlebih dahulu
        login_success()

        // cek tombol logout
        Espresso.onView(withId(R.id.btn_logout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_logout)).perform(ViewActions.click())

        // cek tampilan dialog
        Espresso.onView(withId(R.id.tv_info_registrasi))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_yes))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.btn_yes)).perform(ViewActions.click())

        // cek halaman home
        Espresso.onView(withId(R.id.titleTextView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }
}