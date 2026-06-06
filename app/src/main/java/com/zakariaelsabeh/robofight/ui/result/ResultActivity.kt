package com.zakariaelsabeh.robofight.ui.result

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.databinding.ActivityResultBinding
import com.zakariaelsabeh.robofight.ui.city.CityMapActivity
import com.zakariaelsabeh.robofight.ui.menu.MenuActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val app get() = application as GameApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerWon = intent.getBooleanExtra(EXTRA_PLAYER_WON, false)
        val stars = intent.getIntExtra(EXTRA_STARS, 0)
        val coinsEarned = intent.getIntExtra(EXTRA_COINS_EARNED, 0)
        val levelId = intent.getIntExtra(EXTRA_LEVEL_ID, 1)
        val robotId = intent.getIntExtra(EXTRA_ROBOT_ID, 0)
        val enemyName = intent.getStringExtra(EXTRA_ENEMY_NAME) ?: "Enemy"

        setupResult(playerWon, stars, coinsEarned, levelId, robotId, enemyName)
        setupButtons(playerWon, levelId)
    }

    private fun setupResult(won: Boolean, stars: Int, coins: Int, levelId: Int, robotId: Int, enemyName: String) {
        val profile = app.currentProfile
        val robot = GameData.getRobotById(robotId)

        if (won) {
            binding.tvResultTitle.text = "VICTORY! 🎉"
            binding.tvResultTitle.setTextColor(resources.getColor(R.color.gold, null))
            binding.ivResultRobot.setRobot(robot)
            binding.tvResultMessage.text = "Amazing! You defeated $enemyName!\n${robot.name} is a champion!"
            binding.bgConfetti.visibility = View.VISIBLE
            animateConfetti()
        } else {
            binding.tvResultTitle.text = "Keep Trying! 💪"
            binding.tvResultTitle.setTextColor(resources.getColor(R.color.colorSecondary, null))
            binding.ivResultRobot.setRobot(robot)
            binding.tvResultMessage.text = "Don't give up! ${robot.name} will come back stronger!\nPractice in the city first!"
        }

        binding.tvCoinsEarned.text = "+$coins 🪙"
        binding.tvTotalCoins.text = "Total coins: ${profile.coins}"
        binding.tvTotalStars.text = "⭐ ${profile.totalStars} stars total"
        binding.tvWins.text = "🏆 ${profile.battlesWon} wins"

        // Star display
        val starViews = listOf(binding.star1, binding.star2, binding.star3)
        starViews.forEachIndexed { i, v ->
            v.alpha = 0f
            if (i < stars) {
                Handler(Looper.getMainLooper()).postDelayed({
                    v.animate().alpha(1f).scaleX(1.2f).scaleY(1.2f).setDuration(300)
                        .withEndAction { v.animate().scaleX(1f).scaleY(1f).setDuration(150).start() }
                        .start()
                }, 300L + i * 200L)
            } else {
                v.alpha = 0.3f
            }
        }

        // Show unlock notification
        val unlockedRobots = profile.unlockedRobotIds
        if (won) {
            val unlockMsg = when {
                profile.battlesWon == 2 -> "🔓 Crusher is now unlocked!"
                profile.battlesWon == 5 -> "🔓 Spark is now unlocked!"
                profile.battlesWon == 8 -> "🔓 Rocket is now unlocked!"
                profile.battlesWon == 12 -> "🔓 Nova is now unlocked!"
                else -> null
            }
            if (unlockMsg != null) {
                binding.tvUnlockNotice.text = unlockMsg
                binding.tvUnlockNotice.visibility = View.VISIBLE
                binding.tvUnlockNotice.animate().alpha(1f).scaleX(1.1f).scaleY(1.1f)
                    .setDuration(500).withEndAction {
                        binding.tvUnlockNotice.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                    }.start()
            }
        }

        // Entrance animation
        animateEntrance(won)
    }

    private fun animateEntrance(won: Boolean) {
        binding.tvResultTitle.alpha = 0f
        binding.tvResultTitle.scaleX = 0.5f
        binding.tvResultTitle.scaleY = 0.5f

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvResultTitle, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(binding.tvResultTitle, "scaleX", 0.5f, 1.1f, 1f),
                ObjectAnimator.ofFloat(binding.tvResultTitle, "scaleY", 0.5f, 1.1f, 1f)
            )
            duration = 600
            interpolator = OvershootInterpolator()
            start()
        }

        listOf(binding.ivResultRobot, binding.tvResultMessage, binding.cardStats,
            binding.llStars, binding.btnPlayAgain, binding.btnCityMap, binding.btnMenu)
            .forEachIndexed { i, v ->
                v.alpha = 0f
                v.translationY = 40f
                v.animate().alpha(1f).translationY(0f).setStartDelay(200L + i * 80L).setDuration(400).start()
            }
    }

    private fun animateConfetti() {
        ObjectAnimator.ofFloat(binding.bgConfetti, "alpha", 0f, 1f, 0.8f).apply {
            duration = 1500; repeatCount = 2; start()
        }
    }

    private fun setupButtons(won: Boolean, levelId: Int) {
        binding.btnPlayAgain.setOnClickListener {
            app.soundManager.playButton()
            val intent = Intent(this, com.zakariaelsabeh.robofight.ui.battle.BattleActivity::class.java)
                .putExtra(com.zakariaelsabeh.robofight.ui.battle.BattleActivity.EXTRA_LEVEL_ID, levelId)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnCityMap.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, CityMapActivity::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnMenu.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    companion object {
        const val EXTRA_PLAYER_WON = "player_won"
        const val EXTRA_STARS = "stars"
        const val EXTRA_COINS_EARNED = "coins_earned"
        const val EXTRA_LEVEL_ID = "level_id"
        const val EXTRA_ROBOT_ID = "robot_id"
        const val EXTRA_ENEMY_NAME = "enemy_name"
    }
}
