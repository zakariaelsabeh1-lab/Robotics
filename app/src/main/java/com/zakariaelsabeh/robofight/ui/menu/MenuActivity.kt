package com.zakariaelsabeh.robofight.ui.menu

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.databinding.ActivityMenuBinding
import com.zakariaelsabeh.robofight.ui.city.CityMapActivity
import com.zakariaelsabeh.robofight.ui.setup.PlayerSetupActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val app get() = application as GameApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        refreshStats()
    }

    private fun setupUI() {
        binding.btnPlay.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, CityMapActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnChangeRobot.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, PlayerSetupActivity::class.java)
                .putExtra("edit_mode", true))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnSettings.setOnClickListener {
            app.soundManager.playButton()
            showSettings()
        }

        binding.btnExit.setOnClickListener {
            finishAffinity()
        }

        binding.ivMenuRobot.setOnClickListener {
            val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
            binding.ivMenuRobot.startAnimation(bounce)
            app.soundManager.playButton()
        }

        pulsePlayButton()
    }

    private fun refreshStats() {
        val profile = app.currentProfile
        val robot = GameData.getRobotById(profile.selectedRobotId)
        binding.tvPlayerName.text = "⚡ ${profile.playerName}"
        binding.tvRobotName.text = "Robot: ${robot.name}"
        binding.tvCoins.text = "🪙 ${profile.coins}"
        binding.tvStars.text = "⭐ ${profile.totalStars}"
        binding.tvLevel.text = "Level ${profile.currentLevel}"
        binding.tvWins.text = "Wins: ${profile.battlesWon}"
        binding.ivMenuRobot.setRobotId(profile.selectedRobotId)
    }

    private fun animateEntrance() {
        listOf(binding.tvPlayerName, binding.tvRobotName, binding.cardStats,
            binding.btnPlay, binding.btnChangeRobot, binding.btnSettings).forEachIndexed { i, v ->
            v.alpha = 0f
            v.translationY = 50f
            v.animate().alpha(1f).translationY(0f).setStartDelay(i * 80L).setDuration(400).start()
        }
    }

    private fun pulsePlayButton() {
        ObjectAnimator.ofFloat(binding.btnPlay, "scaleX", 1f, 1.04f, 1f).apply {
            duration = 1000; repeatCount = ObjectAnimator.INFINITE; start()
        }
        ObjectAnimator.ofFloat(binding.btnPlay, "scaleY", 1f, 1.04f, 1f).apply {
            duration = 1000; repeatCount = ObjectAnimator.INFINITE; start()
        }
    }

    private fun showSettings() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("⚙️ Settings")
            .setMessage("Sound & Music settings\n\n(Toggle coming soon!)")
            .setPositiveButton("OK", null)
            .setNeutralButton("New Game") { _, _ ->
                app.prefManager.clearAll()
                startActivity(Intent(this, PlayerSetupActivity::class.java))
                finish()
            }
            .create()
        dialog.show()
    }
}
