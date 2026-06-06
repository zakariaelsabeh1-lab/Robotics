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

    private var comboCount = 0
    private var lastWasCrit = false

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
        showToast("FIGHT!")
    }

    // ── Combat math ─────────────────────────────────────────────────────────────

    private fun isCritical() = Random.nextFloat() < 0.15f

    private fun comboMultiplier(): Float = when {
        comboCount >= 6 -> 2.0f
        comboCount >= 4 -> 1.5f
        comboCount >= 2 -> 1.25f
        else -> 1.0f
    }

    private fun applyPlayerDamage(baseDmg: Int): Int {
        val crit = isCritical()
        lastWasCrit = crit
        val combo = comboMultiplier()
        val rawDmg = (baseDmg * combo * (if (crit) 2f else 1f) + Random.nextInt(-3, 6)).toInt()
        return enemyRobot.takeDamage(rawDmg.coerceAtLeast(1))
    }

    // ── Player actions ───────────────────────────────────────────────────────────

    private fun performPlayerAttack() {
        isAnimating = true; setButtonsEnabled(false)
        app.soundManager.playAttack()

        binding.battleView.animatePlayerAttack {
            comboCount++
            val actual = applyPlayerDamage(playerRobot.attack + Random.nextInt(-3, 6))
            showDamageNumber(actual, isPlayer = false)
            binding.battleView.animateEnemyHit()
            app.soundManager.vibrateShort()
            if (lastWasCrit) showCritical()
            updateComboDisplay()
        }

        handler.postDelayed({ checkBattleState { enemyTurn() } }, 750)
    }

    private fun performPlayerSpecial() {
        isAnimating = true; setButtonsEnabled(false)
        app.soundManager.playSpecial()
        specialCooldown = playerRobot.specialPower.cooldownTurns

        val color = playerRobot.displayColor()
        binding.battleView.animateSpecialAttack(true, color) {
            comboCount++
            val base = playerRobot.specialPower.damage + Random.nextInt(-5, 10)
            val actual = applyPlayerDamage(base)
            showDamageNumber(actual, isPlayer = false)
            app.soundManager.vibrateMedium()
            showToast("${playerRobot.specialPower.displayName}!")
            if (lastWasCrit) showCritical()
            updateComboDisplay()
        }

        handler.postDelayed({
            updateSpecialButton()
            checkBattleState { enemyTurn() }
        }, 950)
    }

    private fun performPlayerBlock() {
        isAnimating = true; setButtonsEnabled(false)
        comboCount = 0; updateComboDisplay()
        showToast("${playerRobot.name} braces for impact!")
        app.soundManager.playButton()

        handler.postDelayed({
            val enemyDmg = calculateEnemyDamage()
            val reducedDmg = maxOf(1, enemyDmg / 3)
            playerRobot.takeDamage(reducedDmg)
            showDamageNumber(reducedDmg, isPlayer = true)
            binding.battleView.animatePlayerHit()
            app.soundManager.vibrateShort()
            handler.postDelayed({ checkBattleState { nextPlayerTurn() } }, 500)
        }, 300)
    }

    // ── Enemy actions ─────────────────────────────────────────────────────────

    private fun enemyTurn() {
        handler.postDelayed({
            if (Random.nextInt(3) < 2) performEnemyAttack() else performEnemySpecial()
        }, 420)
    }

    private fun performEnemyAttack() {
        showToast("${enemyRobot.name} strikes!")
        app.soundManager.playAttack()

        binding.battleView.animateEnemyAttack {
            val dmg = calculateEnemyDamage()
            val actual = playerRobot.takeDamage(dmg)
            comboCount = 0; updateComboDisplay()
            showDamageNumber(actual, isPlayer = true)
            binding.battleView.animatePlayerHit()
            app.soundManager.vibrateShort()
        }

        handler.postDelayed({ checkBattleState { nextPlayerTurn() } }, 750)
    }

    private fun performEnemySpecial() {
        showToast("${enemyRobot.name}: ${enemyRobot.specialPower.displayName}!")
        app.soundManager.playSpecial()

        binding.battleView.animateSpecialAttack(false, enemyRobot.displayColor()) {
            val dmg = (enemyRobot.specialPower.damage * 0.85f).toInt() + Random.nextInt(-5, 5)
            val actual = playerRobot.takeDamage(dmg)
            comboCount = 0; updateComboDisplay()
            showDamageNumber(actual, isPlayer = true)
            app.soundManager.vibrateMedium()
        }

        handler.postDelayed({ checkBattleState { nextPlayerTurn() } }, 950)
    }

    private fun calculateEnemyDamage(): Int =
        (enemyRobot.attack * (0.8f + Random.nextFloat() * 0.4f)).toInt().coerceAtLeast(1)

    // ── State management ─────────────────────────────────────────────────────

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
        updateSpecialButton(); updateTurnLabel()
        isAnimating = false; setButtonsEnabled(true)
        if (turn % 5 == 0) {
            playerRobot.heal(8)
            showToast("+8 HP regenerated")
        }
    }

    private fun endBattle(playerWon: Boolean) {
        setButtonsEnabled(false)
        if (playerWon) {
            binding.battleView.animateVictory()
            app.soundManager.playVictory(); app.soundManager.vibrateLong()
        } else {
            app.soundManager.playDefeat()
        }

        val stars = calculateStars(playerWon)
        val coinsEarned = if (playerWon) 25 + stars * 15 else 5

        app.currentProfile.apply {
            if (playerWon) {
                battlesWon++; totalStars += stars; coins += coinsEarned
                if (currentLevel < levelId + 1) currentLevel = levelId + 1
                if (battlesWon >= 2 && !unlockedRobotIds.contains(2)) unlockedRobotIds.add(2)
                if (battlesWon >= 5 && !unlockedRobotIds.contains(3)) unlockedRobotIds.add(3)
                if (battlesWon >= 8 && !unlockedRobotIds.contains(4)) unlockedRobotIds.add(4)
                if (battlesWon >= 12 && !unlockedRobotIds.contains(5)) unlockedRobotIds.add(5)
            } else {
                battlesLost++; coins += coinsEarned
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

    // ── UI helpers ────────────────────────────────────────────────────────────

    private fun showDamageNumber(dmg: Int, isPlayer: Boolean) {
        val label = if (isPlayer) binding.tvPlayerDamage else binding.tvEnemyDamage
        label.text = "-$dmg"
        label.visibility = View.VISIBLE; label.alpha = 1f; label.translationY = 0f
        label.animate().translationYBy(-70f).alpha(0f).setDuration(900).withEndAction {
            label.visibility = View.INVISIBLE; label.translationY = 0f; label.alpha = 1f
        }.start()
    }

    private fun showCritical() {
        binding.tvCritical.visibility = View.VISIBLE
        binding.tvCritical.alpha = 1f; binding.tvCritical.scaleX = 0.5f; binding.tvCritical.scaleY = 0.5f
        binding.tvCritical.animate()
            .scaleX(1.3f).scaleY(1.3f).setDuration(200)
            .withEndAction {
                binding.tvCritical.animate().alpha(0f).setDuration(600).withEndAction {
                    binding.tvCritical.visibility = View.GONE
                    binding.tvCritical.alpha = 1f
                }.start()
            }.start()
    }

    private fun updateComboDisplay() {
        if (comboCount >= 2) {
            val mult = comboMultiplier()
            binding.tvCombo.text = "COMBO x$comboCount  (${mult}x DMG)"
            binding.tvCombo.visibility = View.VISIBLE
        } else {
            binding.tvCombo.visibility = View.GONE
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnAttack.isEnabled = enabled
        binding.btnSpecial.isEnabled = enabled && specialCooldown == 0
        binding.btnBlock.isEnabled = enabled
    }

    private fun updateSpecialButton() {
        if (specialCooldown == 0) {
            binding.btnSpecial.text = "SPECIAL!"
            binding.btnSpecial.setBackgroundColor(Color.parseColor("#CC6600"))
        } else {
            binding.btnSpecial.text = "SPECIAL ($specialCooldown)"
            binding.btnSpecial.setBackgroundColor(Color.parseColor("#444444"))
        }
    }

    private fun updateTurnLabel() { binding.tvTurn.text = "Turn ${turn + 1}" }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onBackPressed() { showToast("Finish the battle first!") }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object { const val EXTRA_LEVEL_ID = "level_id" }
}
