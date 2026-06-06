package com.zakariaelsabeh.robofight.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.databinding.ActivitySplashBinding
import com.zakariaelsabeh.robofight.ui.menu.MenuActivity
import com.zakariaelsabeh.robofight.ui.setup.PlayerSetupActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animateLogo()

        Handler(Looper.getMainLooper()).postDelayed({
            val app = application as GameApplication
            val next = if (app.prefManager.hasProfile()) {
                Intent(this, MenuActivity::class.java)
            } else {
                Intent(this, PlayerSetupActivity::class.java)
            }
            startActivity(next)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2800)
    }

    private fun animateLogo() {
        binding.ivLogo.alpha = 0f
        binding.ivLogo.scaleX = 0.3f
        binding.ivLogo.scaleY = 0.3f

        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = 60f

        binding.tvTagline.alpha = 0f
        binding.tvTagline.translationY = 40f

        val logoScale = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 0.3f, 1.1f, 1f),
                ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 0.3f, 1.1f, 1f),
                ObjectAnimator.ofFloat(binding.ivLogo, "alpha", 0f, 1f)
            )
            duration = 700
            interpolator = OvershootInterpolator()
        }

        val titleAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvTitle, "translationY", 60f, 0f)
            )
            duration = 500
        }

        val taglineAnim = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvTagline, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvTagline, "translationY", 40f, 0f)
            )
            duration = 400
        }

        AnimatorSet().apply {
            play(logoScale)
            play(titleAnim).after(400)
            play(taglineAnim).after(700)
            start()
        }

        // Pulse the logo
        Handler(Looper.getMainLooper()).postDelayed({
            ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 1f, 1.05f, 1f).apply {
                duration = 800; repeatCount = ObjectAnimator.INFINITE; start()
            }
            ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 1f, 1.05f, 1f).apply {
                duration = 800; repeatCount = ObjectAnimator.INFINITE; start()
            }
        }, 900)
    }
}
