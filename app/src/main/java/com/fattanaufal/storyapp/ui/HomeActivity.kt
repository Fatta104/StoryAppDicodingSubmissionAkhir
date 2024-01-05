package com.fattanaufal.storyapp.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fattanaufal.storyapp.animation.AnimationPackage
import com.fattanaufal.storyapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var homeBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)

        playAnimation()

        homeBinding.signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        homeBinding.signUpButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        // Set animation view
        // bannerRegister
        val bannerFadeIn = AnimationPackage.fadeIn(homeBinding.bannerRegister, 700)
        val bannerMoveY = AnimationPackage.translateY(homeBinding.bannerRegister, 700, -30f, 0f)

        // titleTextView
        val titleFadeIn = AnimationPackage.fadeIn(homeBinding.titleTextView, 700)
        val titleMoveX = AnimationPackage.translateX(homeBinding.titleTextView, 700, -30f, 0f)


        // button
        val signInFadeIn = AnimationPackage.fadeIn(homeBinding.signInButton, 500)
        val signInMoveY = AnimationPackage.translateY(homeBinding.signInButton, 700, 30f, 0f)
        val signUpFadeIn = AnimationPackage.fadeIn(homeBinding.signUpButton, 500)
        val signUpMoveY = AnimationPackage.translateY(homeBinding.signUpButton, 700, 30f, 0f)

        val bannerAnim = AnimatorSet().apply {
            play(bannerFadeIn).with(bannerMoveY)
        }
        val titleAnim = AnimatorSet().apply {
            play(titleFadeIn).with(titleMoveX)
        }

        val buttonAnim = AnimatorSet().apply {
            play(signInFadeIn).with(signInMoveY)
            play(signUpFadeIn).with(signUpMoveY)
        }


        // Memulai siklus animasi untuk halaman welcome
        AnimatorSet().apply {
            playSequentially(
                bannerAnim, titleAnim, buttonAnim
            )
            start()
        }
    }
}
