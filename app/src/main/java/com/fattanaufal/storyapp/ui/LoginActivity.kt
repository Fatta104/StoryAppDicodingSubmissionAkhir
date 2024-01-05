package com.fattanaufal.storyapp.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fattanaufal.storyapp.R
import com.fattanaufal.storyapp.animation.AnimationPackage
import com.fattanaufal.storyapp.auth.AuthViewModel
import com.fattanaufal.storyapp.auth.AuthViewModelFactory
import com.fattanaufal.storyapp.databinding.LoginActivityBinding
import com.fattanaufal.storyapp.preference.UserPrefViewModel
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.preference.UserPreferencesFactory
import com.fattanaufal.storyapp.preference.dataStore

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: LoginActivityBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userPrefViewModel: UserPrefViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        authViewModel = obtainViewModel(this@LoginActivity)

        val pref = UserPreferences.getInstance(application.dataStore)
        userPrefViewModel = ViewModelProvider(this, UserPreferencesFactory(pref))[UserPrefViewModel::class.java]
        playAnimation()

        loginBinding.signInButton.setOnClickListener {
            if (checkFillOrNot()){
                loginBinding.signInButton.text = ""
                loginBinding.lottieTest.visibility = View.VISIBLE
                authViewModel.getUser(loginBinding.emailEditText.text.toString(), loginBinding.passwordEditText.text.toString())

                authViewModel.errorLoginResponse.observe(this) {error ->
                    if (error != null){
                        showDialog()
                        authViewModel.restoreErrorLogin()
                        loginBinding.signInButton.text = getString(R.string.text_btn_register)
                        loginBinding.lottieTest.visibility = View.GONE
                    }
                }

                authViewModel.loginResponse.observe(this) {session ->
                    if (session != null){
                        userPrefViewModel.saveSessionUser(session)
                        val intentToMain = Intent(this, MainActivity::class.java)
                        intentToMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intentToMain)
                        finish()
                        authViewModel.restoreLoginResponse()
                    }

                }
            }
        }

        loginBinding.backToHome.setOnClickListener {
            val intentToMain = Intent(this, HomeActivity::class.java)
            intentToMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentToMain)
            finish()
        }
    }

    private fun obtainViewModel(appCompatActivity: AppCompatActivity): AuthViewModel {
        val factory = AuthViewModelFactory.getInstance(appCompatActivity.applicationContext)
        return ViewModelProvider(
            appCompatActivity, factory
        )[AuthViewModel::class.java]
    }

    private fun playAnimation() {
        // Set animation view
        // bannerRegister
        val bannerFadeIn = AnimationPackage.fadeIn(loginBinding.bannerRegister, 700)
        val bannerMoveY = AnimationPackage.translateY(loginBinding.bannerRegister, 700, -30f, 0f)

        // titleTextView
        val titleFadeIn = AnimationPackage.fadeIn(loginBinding.titleTextView, 700)
        val titleMoveX = AnimationPackage.translateX(loginBinding.titleTextView, 700, -30f, 0f)

        // inputComponent
        val emailFadeIn = AnimationPackage.fadeIn(loginBinding.emailTextView, 500)
        val emailInputFadeIn = AnimationPackage.fadeIn(loginBinding.emailEditTextLayout, 500)
        val passwordFadeIn = AnimationPackage.fadeIn(loginBinding.passwordTextView, 500)
        val passwordInputFadeIn =
            AnimationPackage.fadeIn(loginBinding.passwordEditTextLayout, 500)

        // button
        val buttonFadeIn = AnimationPackage.fadeIn(loginBinding.signInButton, 500)
        val buttonMoveY = AnimationPackage.translateY(loginBinding.signInButton, 700, 30f, 0f)
        val backFadeIn = AnimationPackage.fadeIn(loginBinding.backToHome, 500)
        val backMoveY = AnimationPackage.translateY(loginBinding.backToHome, 700, 30f, 0f)

        val bannerAnim = AnimatorSet().apply {
            play(bannerFadeIn).with(bannerMoveY)
        }
        val titleAnim = AnimatorSet().apply {
            play(titleFadeIn).with(titleMoveX)
        }
        val inputAnim = AnimatorSet().apply {
            playSequentially(
                emailFadeIn,
                emailInputFadeIn,
                passwordFadeIn,
                passwordInputFadeIn
            )
        }
        val buttonAnim = AnimatorSet().apply {
            play(buttonFadeIn).with(buttonMoveY)
            play(backFadeIn).with(backMoveY)
        }


        // Memulai siklus animasi untuk halaman welcome
        AnimatorSet().apply {
            playSequentially(
                bannerAnim, titleAnim, inputAnim, buttonAnim
            )
            start()
        }
    }

    private fun checkFillOrNot(): Boolean {

        if (loginBinding.emailEditText.text.toString().isEmpty()) {
            loginBinding.emailEditTextLayout.error = getString(R.string.not_fill)
            loginBinding.emailEditText.error = null
        } else if (loginBinding.emailEditText.error != null) {
            loginBinding.emailEditTextLayout.isErrorEnabled = false
            loginBinding.emailEditTextLayout.error = null
        } else {
            loginBinding.emailEditTextLayout.isErrorEnabled = false
            loginBinding.emailEditTextLayout.clearFocus()
            loginBinding.passwordEditTextLayout.requestFocus()
        }

        if (loginBinding.passwordEditText.text.toString().isEmpty()) {
            loginBinding.passwordEditTextLayout.error = getString(R.string.not_fill)
            loginBinding.passwordEditText.error = null
        } else if (loginBinding.passwordEditText.error != null) {
            loginBinding.passwordEditTextLayout.isErrorEnabled = false
            loginBinding.passwordEditTextLayout.error = null
        } else {

            loginBinding.passwordEditTextLayout.isErrorEnabled = false
            loginBinding.passwordEditTextLayout.clearFocus()
        }


        return !((loginBinding.emailEditTextLayout.error != null && loginBinding.emailEditText.error == null)
                || (loginBinding.passwordEditTextLayout.error != null || loginBinding.passwordEditText.error != null))
    }

    private fun showDialog(){
        val view = layoutInflater.inflate(R.layout.dialog_error_login, null)
        val builder = AlertDialog.Builder(this@LoginActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
        }
    }


}