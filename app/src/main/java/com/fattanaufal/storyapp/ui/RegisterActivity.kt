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
import com.fattanaufal.storyapp.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private var error: Boolean? = null
    private var message = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        playAnimation()
        authViewModel = obtainViewModel(this@RegisterActivity)


        registerBinding.signupButton.setOnClickListener {
            if (checkFillOrNot()) {
                registerBinding.signupButton.text = ""
                registerBinding.lottieTest.visibility = View.VISIBLE

                authViewModel.insertUser(
                    registerBinding.nameEditText.text.toString(),
                    registerBinding.emailEditText.text.toString(),
                    registerBinding.passwordEditText.text.toString()
                )

                authViewModel.registerResponse.observe(this@RegisterActivity) { response ->
                    error = response?.error
                    message = response?.message.toString()
                    if (error != null) {
                        if (error != true) {
                            showDialog("success")
                        } else {
                            showDialog("error")
                        }
                        authViewModel.restoreError(null)
                        registerBinding.signupButton.text = getString(R.string.text_btn_register)
                        registerBinding.lottieTest.visibility = View.GONE
                    }
                }
            }
        }

        registerBinding.backToHome.setOnClickListener {
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
        val bannerFadeIn = AnimationPackage.fadeIn(registerBinding.bannerRegister, 700)
        val bannerMoveY = AnimationPackage.translateY(registerBinding.bannerRegister, 700, -30f, 0f)

        // titleTextView
        val titleFadeIn = AnimationPackage.fadeIn(registerBinding.titleTextView, 700)
        val titleMoveX = AnimationPackage.translateX(registerBinding.titleTextView, 700, -30f, 0f)

        // inputComponent
        val nameFadeIn = AnimationPackage.fadeIn(registerBinding.nameTextView, 500)
        val nameInputFadeIn = AnimationPackage.fadeIn(registerBinding.nameEditTextLayout, 500)
        val emailFadeIn = AnimationPackage.fadeIn(registerBinding.emailTextView, 500)
        val emailInputFadeIn = AnimationPackage.fadeIn(registerBinding.emailEditTextLayout, 500)
        val passwordFadeIn = AnimationPackage.fadeIn(registerBinding.passwordTextView, 500)
        val passwordInputFadeIn =
            AnimationPackage.fadeIn(registerBinding.passwordEditTextLayout, 500)

        // button
        val buttonFadeIn = AnimationPackage.fadeIn(registerBinding.signupButton, 500)
        val buttonMoveY = AnimationPackage.translateY(registerBinding.signupButton, 700, 30f, 0f)
        val backFadeIn = AnimationPackage.fadeIn(registerBinding.backToHome, 500)
        val backMoveY = AnimationPackage.translateY(registerBinding.backToHome, 700, 30f, 0f)

        val bannerAnim = AnimatorSet().apply {
            play(bannerFadeIn).with(bannerMoveY)
        }
        val titleAnim = AnimatorSet().apply {
            play(titleFadeIn).with(titleMoveX)
        }
        val inputAnim = AnimatorSet().apply {
            playSequentially(
                nameFadeIn,
                nameInputFadeIn,
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
        if (registerBinding.nameEditText.text.toString().isEmpty()) {
            registerBinding.nameEditTextLayout.error = getString(R.string.not_fill)
        } else {
            registerBinding.nameEditTextLayout.error = null
            registerBinding.nameEditTextLayout.isErrorEnabled = false
            registerBinding.nameEditTextLayout.clearFocus()
            registerBinding.emailEditTextLayout.requestFocus()

        }

        if (registerBinding.emailEditText.text.toString().isEmpty()) {
            registerBinding.emailEditTextLayout.error = getString(R.string.not_fill)
            registerBinding.emailEditText.error = null
        } else if (registerBinding.emailEditText.error != null) {
            registerBinding.emailEditTextLayout.isErrorEnabled = false
            registerBinding.emailEditTextLayout.error = null
        } else {
            registerBinding.emailEditTextLayout.isErrorEnabled = false
            registerBinding.emailEditTextLayout.clearFocus()
            registerBinding.passwordEditTextLayout.requestFocus()
        }

        if (registerBinding.passwordEditText.text.toString().isEmpty()) {
            registerBinding.passwordEditTextLayout.error = getString(R.string.not_fill)
            registerBinding.passwordEditText.error = null
        } else if (registerBinding.passwordEditText.error != null) {
            registerBinding.passwordEditTextLayout.isErrorEnabled = false
            registerBinding.passwordEditTextLayout.error = null
        } else {

            registerBinding.passwordEditTextLayout.isErrorEnabled = false
            registerBinding.passwordEditTextLayout.clearFocus()
        }


        return !(registerBinding.nameEditTextLayout.error != null || (registerBinding.emailEditTextLayout.error != null && registerBinding.emailEditText.error == null) || (registerBinding.passwordEditTextLayout.error != null || registerBinding.passwordEditText.error != null))
    }


    private fun showDialog(type: String){

        val view = layoutInflater.inflate(if(type == "error") R.layout.dialog_error else R.layout.dialog_success, null)
        val builder = AlertDialog.Builder(this@RegisterActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            dialog.dismiss()
            if (type != "error") {
                val intentToLogin = Intent(this, LoginActivity::class.java)
                intentToLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentToLogin)
                finish()
            }
        }
    }


}