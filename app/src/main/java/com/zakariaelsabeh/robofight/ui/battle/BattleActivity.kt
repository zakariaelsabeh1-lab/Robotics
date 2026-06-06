package com.zakariaelsabeh.robofight.ui.battle

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.databinding.ActivityBattleBinding
import com.zakariaelsabeh.robofight.ui.result.ResultActivity
import kotlin.random.Random

class BattleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBattleBinding
    private lateinit var playerRobot: Robot
    private lateinit var enemyRobot: Robot
    private val app get() = application as GameApplication
    private val handler = Handler(Looper.getMainLooper())

    private var specialCooldown = 0
    private var turn = 0
    private var isAnimating = false
    private var levelId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        levelId = intent.getIntExtra(EXTRA_LEVEL_ID, 1)
        setupRobots()
        setupUI()
        startBattle()
    }

    private fun setupRobots() {
        val profile = app.currentProfile
        playerRobot = GameData.getRobotById(profile.selectedRobotId).copy()
        playerRobot.reset()

        // Apply weapon bonus
        if (profile.equippedWeaponId >= 0) {
            val weapon = GameData.getWeaponById(profile.equippedWeaponId)
            playerRobot = playerRobot.copy(attack = playerRobot.attack + weapon.bonusDamage)
        }

        enemyRobot = GameData.getEnemyRobotForLevel(levelId)
        enemyRobot.reset()

        binding.battleView.playerRobot = playerRobot
        binding.battleView.enemyRobot = enemyRobot
    }

    private fun setupUI() {
        binding.btnAttack.setOnClickListener {
            if (!isAnimating) performPlayerAttack()
        }
        binding.btnSpecial.setOnClickListener {
            if (!isAnimating && specialCooldown == 0) performPlayerSpecial()
            else if (specialCooldown > 0) showToast("Special ready in $specialCooldown turns!")
        }
        binding.btnBlock.setOnClickListener {
            if (!isAnimating) performPlayerBlock()
        }
        updateSpecialButton()
        updateTurnLabel()
    }

    private fun startBattle() {
        val anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        binding.battleView.startAnimation(anim)
        showToast("Battle Start! Fight!")
    }

    private fun performPlayerAttack() {
        isAnimating = true
        setButtonsEnabled(false)
        app.soundManager.playAttack()

        binding.battleView.animatePlayerAttack {
            val dmg = playerRobot.attack + Random.nextInt(-3, 6)
            val actual = enemyRobot.takeDamage(dmg)
            showDamageNumber(actual, isPlayer = false)
            binding.battleView.animateEnemyHit()
            app.soundManager.vibrateShort()
        }

        handler.postDelayed({
            checkBattleState { enemyTurn() }
        }, 700)
    }

    private fun performPlayerSpecial() {
        isAnimating = true
        setButtonsEnabled(false)
        app.soundManager.playSpecial()
        specialCooldown = playerRobot.specialPower.cooldownTurns

        val color = playerRobot.displayColor()
        binding.battleView.animateSpecialAttack(true, color) {
            val dmg = playerRobot.specialPower.damage + Random.nextInt(-5, 10)
            val actual = enemyRobot.takeDamage(dmg)
            showDamageNumber(actual, isPlayer = false)
            app.soundManager.vibrateMedium()
            showToast("${playerRobot.specialPower.displayName}!")
        }

        handler.postDelayed({
            updateSpecialButton()
            checkBattleState { enemyTurn() }
        }, 900)
    }

    private fun performPlayerBlock() {
        isAnimating = true
        setButtonsEnabled(false)
        showToast("${playerRobot.name} blocks!")
        app.soundManager.playButton()

        handler.postDelayed({
            val enemyDmg = calculateEnemyDamage()
            val reducedDmg = maxOf(1, enemyDmg / 3)
            playerRobot.takeDamage(reducedDmg)
            showDamageNumber(reducedDmg, isPlayer = true)
            binding.battleView.animatePlayerHit()
            app.soundManager.vibrateShort()
            handler.postDelayed({
                checkBattleState { nextPlayerTurn() }
            }, 500)
        }, 300)
    }

    private fun enemyTurn() {
        handler.postDelayed({
            val action = Random.nextInt(3)
            when {
                action < 2 -> performEnemyAttack()
                else -> performEnemySpecial()
            }
        }, 400)
    }

    private fun performEnemyAttack() {
        showToast("${enemyRobot.name} attacks!")
        app.soundManager.playAttack()

        binding.battleView.animateEnemyAttack {
            val dmg = calculateEnemyDamage()
            val actual = playerRobot.takeDamage(dmg)
            showDamageNumber(actual, isPlayer = true)
            binding.battleView.animatePlayerHit()
            app.soundManager.vibrateShort()
        }

        handler.postDelayed({
            checkBattleState { nextPlayerTurn() }
        }, 700)
    }

    private fun performEnemySpecial() {
        showToast("${enemyRobot.name} uses ${enemyRobot.specialPower.displayName}!")
        app.soundManager.playSpecial()

        binding.battleView.animateSpecialAttack(false, enemyRobot.displayColor()) {
            val dmg = (enemyRobot.specialPower.damage * 0.85f).toInt() + Random.nextInt(-5, 5)
            val actual = playerRobot.takeDamage(dmg)
            showDamageNumber(actual, isPlayer = true)
            app.soundManager.vibrateMedium()
        }

        handler.postDelayed({
            checkBattleState { nextPlayerTurn() }
        }, 900)
    }

    private fun calculateEnemyDamage(): Int =
        (enemyRobot.attack * Random.nextFloat() * 0.4f + enemyRobot.attack * 0.8f).toInt()

    private fun checkBattleState(onContinue: () -> Unit) {
        binding.battleView.invalidate()
        when {
            !enemyRobot.isAlive() -> endBattle(playerWon = true)
            !playerRobot.isAlive() -> endBattle(playerWon = false)
            else -> onContinue()
        }
    }

    private fun nextPlayerTurn() {
        turn++
        if (specialCooldown > 0) specialCooldown--
        updateSpecialButton()
        updateTurnLabel()
        isAnimating = false
        setButtonsEnabled(true)
        // Slight heal for fun feel
        if (turn % 5 == 0) {
            playerRobot.heal(8)
            showToast("Energy restored! +8 HP")
        }
    }

    private fun endBattle(playerWon: Boolean) {
        setButtonsEnabled(false)
        if (playerWon) {
            binding.battleView.animateVictory()
            app.soundManager.playVictory()
            app.soundManager.vibrateLong()
        } else {
            app.soundManager.playDefeat()
        }

        val stars = calculateStars(playerWon)
        val coinsEarned = if (playerWon) 25 + stars * 15 else 5

        app.currentProfile.apply {
            if (playerWon) {
                battlesWon++
                totalStars += stars
                coins += coinsEarned
                if (currentLevel < levelId + 1) currentLevel = levelId + 1
                // Unlock next robot if enough battles
                if (battlesWon >= 2 && !unlockedRobotIds.contains(2)) unlockedRobotIds.add(2)
                if (battlesWon >= 5 && !unlockedRobotIds.contains(3)) unlockedRobotIds.add(3)
                if (battlesWon >= 8 && !unlockedRobotIds.contains(4)) unlockedRobotIds.add(4)
                if (battlesWon >= 12 && !unlockedRobotIds.contains(5)) unlockedRobotIds.add(5)
            } else {
                battlesLost++
                coins += coinsEarned
            }
        }
        app.saveProfile()

        handler.postDelayed({
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_PLAYER_WON, playerWon)
                putExtra(ResultActivity.EXTRA_STARS, stars)
                putExtra(ResultActivity.EXTRA_COINS_EARNED, coinsEarned)
                putExtra(ResultActivity.EXTRA_LEVEL_ID, levelId)
                putExtra(ResultActivity.EXTRA_ROBOT_ID, playerRobot.id)
                putExtra(ResultActivity.EXTRA_ENEMY_NAME, enemyRobot.name)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }, 1500)
    }

    private fun calculateStars(won: Boolean): Int {
        if (!won) return 0
        return when {
            playerRobot.hpPercent() > 0.6f -> 3
            playerRobot.hpPercent() > 0.3f -> 2
            else -> 1
        }
    }

    private fun showDamageNumber(dmg: Int, isPlayer: Boolean) {
        val label = if (isPlayer) binding.tvPlayerDamage else binding.tvEnemyDamage
        val prefix = if (isPlayer) "-" else "-"
        label.text = "$prefix$dmg"
        label.visibility = View.VISIBLE
        label.alpha = 1f
        label.animate().translationYBy(-60f).alpha(0f).setDuration(800).withEndAction {
            label.visibility = View.INVISIBLE
            label.translationY = 0f
            label.alpha = 1f
        }.start()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnAttack.isEnabled = enabled
        binding.btnSpecial.isEnabled = enabled && specialCooldown == 0
        binding.btnBlock.isEnabled = enabled
    }

    private fun updateSpecialButton() {
        if (specialCooldown == 0) {
            binding.btnSpecial.text = "SPECIAL!"
            binding.btnSpecial.setBackgroundColor(Color.parseColor("#FF6F00"))
        } else {
            binding.btnSpecial.text = "SPECIAL ($specialCooldown)"
            binding.btnSpecial.setBackgroundColor(Color.parseColor("#555555"))
        }
    }

    private fun updateTurnLabel() {
        binding.tvTurn.text = "Turn ${turn + 1}"
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        // Prevent accidental back press during battle
        showToast("Finish the battle first! You can do it!")
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        const val EXTRA_LEVEL_ID = "level_id"
    }
}
