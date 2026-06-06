package com.zakariaelsabeh.robofight.ui.city

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.databinding.ActivityCityMapBinding
import com.zakariaelsabeh.robofight.ui.battle.BattleActivity
import com.zakariaelsabeh.robofight.ui.explore.CityExploreActivity
import com.zakariaelsabeh.robofight.ui.treasure.TreasureActivity
import com.zakariaelsabeh.robofight.ui.workshop.WorkshopActivity

class CityMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityMapBinding
    private val app get() = application as GameApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        refreshHeader()
    }

    private fun setupUI() {
        // Battle Arena
        binding.btnBattleArena.setOnClickListener {
            app.soundManager.playButton()
            showLevelSelect()
        }

        // City Explore
        binding.btnExplore.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, CityExploreActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Treasure Hunt
        binding.btnTreasure.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, TreasureActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Weapon Workshop
        binding.btnWorkshop.setOnClickListener {
            app.soundManager.playButton()
            startActivity(Intent(this, WorkshopActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnBack.setOnClickListener { finish() }

        // Pulse battle button
        ObjectAnimator.ofFloat(binding.btnBattleArena, "scaleX", 1f, 1.03f, 1f).apply {
            duration = 900; repeatCount = ValueAnimator.INFINITE; start()
        }
        ObjectAnimator.ofFloat(binding.btnBattleArena, "scaleY", 1f, 1.03f, 1f).apply {
            duration = 900; repeatCount = ValueAnimator.INFINITE; start()
        }
    }

    private fun refreshHeader() {
        val profile = app.currentProfile
        binding.tvPlayerInfo.text = "⚡ ${profile.playerName}  🪙 ${profile.coins}  ⭐ ${profile.totalStars}"
    }

    private fun showLevelSelect() {
        val profile = app.currentProfile
        val levels = GameData.levels
        val items = levels.map { level ->
            val lock = if (level.id > profile.currentLevel) "🔒 " else ""
            val stars = "⭐".repeat(level.starsEarned)
            "$lock${level.name} - ${level.difficulty.label} $stars"
        }.toTypedArray()

        android.app.AlertDialog.Builder(this)
            .setTitle("⚔️ Choose Your Battle!")
            .setItems(items) { _, which ->
                val level = levels[which]
                if (level.id <= profile.currentLevel) {
                    launchBattle(level.id)
                } else {
                    android.widget.Toast.makeText(this,
                        "Complete Level ${level.id - 1} first!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun launchBattle(levelId: Int) {
        startActivity(Intent(this, BattleActivity::class.java).apply {
            putExtra(BattleActivity.EXTRA_LEVEL_ID, levelId)
        })
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun animateEntrance() {
        listOf(binding.tvCityTitle, binding.tvSubtitle,
            binding.btnBattleArena, binding.btnExplore,
            binding.btnTreasure, binding.btnWorkshop)
            .forEachIndexed { i, v ->
                v.alpha = 0f
                v.translationY = 50f
                v.animate().alpha(1f).translationY(0f).setStartDelay(i * 90L).setDuration(400).start()
            }
    }
}
