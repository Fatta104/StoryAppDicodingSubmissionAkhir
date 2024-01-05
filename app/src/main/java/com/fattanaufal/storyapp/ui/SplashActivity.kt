package com.fattanaufal.storyapp.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fattanaufal.storyapp.animation.AnimationPackage
import com.fattanaufal.storyapp.databinding.ActivitySplashBinding
import com.fattanaufal.storyapp.preference.UserPrefViewModel
import com.fattanaufal.storyapp.preference.UserPreferences
import com.fattanaufal.storyapp.preference.UserPreferencesFactory
import com.fattanaufal.storyapp.preference.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var splashBinding: ActivitySplashBinding
    private lateinit var userPrefViewModel: UserPrefViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)
        playAnimation()
        val pref = UserPreferences.getInstance(application.dataStore)
        userPrefViewModel =
            ViewModelProvider(this, UserPreferencesFactory(pref))[UserPrefViewModel::class.java]

        val intentToMain = Intent(this, MainActivity::class.java)
        val intentToHome = Intent(this, HomeActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            userPrefViewModel.getSessionUser().observe(this@SplashActivity) { session ->
                if (session != null) {
                    startActivity(intentToMain)
                    finish()
                } else {
                    startActivity(intentToHome)
                    finish()
                }
            }
        }
    }

    private fun playAnimation() {
        // Set animation view
        // element kanan dan kiri
        val kananMoveX = AnimationPackage.translateX(splashBinding.kanan, 1500, 350f, 0f)
        val kiriMoveX = AnimationPackage.translateX(splashBinding.kiri, 1500, -350f, 0f)


        // elemen logo
        val redFadeIn = AnimationPackage.fadeIn(splashBinding.boxAppName,700)
        val redMoveY = AnimationPackage.translateY(splashBinding.boxAppName, 500, 100f, 0f)

        val logoFadeIn = AnimationPackage.fadeIn(splashBinding.appName,700)
        val logoMoveX = AnimationPackage.translateX(splashBinding.appName, 500, -100f, 0f)

        // Memulai siklus animasi untuk halaman welcome
        AnimatorSet().apply {
            play(kananMoveX).with(kiriMoveX)
            play(kiriMoveX).before(redFadeIn)
            play(redFadeIn).with(redMoveY)
            play(redMoveY).before(logoFadeIn)
            play(logoFadeIn).with(logoMoveX)
            start()
        }
    }
}