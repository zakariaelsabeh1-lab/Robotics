package com.zakariaelsabeh.robofight.ui.customize

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.databinding.ActivityCustomizeBinding
import com.zakariaelsabeh.robofight.ui.menu.MenuActivity

class CustomizeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeBinding
    private val app get() = application as GameApplication
    private lateinit var robot: Robot
    private var selectedColor = 0

    private val colorOptions = listOf(
        Color.parseColor("#FF4500"),
        Color.parseColor("#1E90FF"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#8A2BE2"),
        Color.parseColor("#FF69B4"),
        Color.parseColor("#FFD700"),
        Color.parseColor("#00CED1"),
        Color.parseColor("#FF1493"),
        Color.parseColor("#00FF7F"),
        Color.parseColor("#FF6347")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        robot = GameData.getRobotById(app.currentProfile.selectedRobotId)
        selectedColor = if (app.currentProfile.customColor != 0)
            app.currentProfile.customColor else robot.primaryColor

        setupColorPicker()
        setupPreview()
        setupButtons()
        animateEntrance()
    }

    private fun setupColorPicker() {
        val colorViews = listOf(
            binding.color1, binding.color2, binding.color3, binding.color4, binding.color5,
            binding.color6, binding.color7, binding.color8, binding.color9, binding.color10
        )

        colorViews.forEachIndexed { i, view ->
            view.setBackgroundColor(colorOptions[i])
            view.setOnClickListener {
                selectedColor = colorOptions[i]
                updatePreview()
                highlightSelected(colorViews, view)
                app.soundManager.playButton()
            }
        }
        highlightSelected(colorViews, colorViews[colorOptions.indexOf(selectedColor).coerceAtLeast(0)])
    }

    private fun highlightSelected(all: List<View>, selected: View) {
        all.forEach { it.scaleX = 1f; it.scaleY = 1f; it.elevation = 2f }
        selected.scaleX = 1.3f
        selected.scaleY = 1.3f
        selected.elevation = 8f
    }

    private fun setupPreview() {
        binding.tvRobotName.text = robot.name
        binding.tvSpecialPower.text = "⚡ ${robot.specialPower.displayName}"
        updatePreview()
    }

    private fun updatePreview() {
        val previewRobot = robot.copy(customColor = selectedColor)
        binding.ivPreviewRobot.setRobot(previewRobot)
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.ivPreviewRobot.startAnimation(bounce)
    }

    private fun setupButtons() {
        binding.btnReset.setOnClickListener {
            selectedColor = robot.primaryColor
            updatePreview()
            app.soundManager.playButton()
        }

        binding.btnConfirm.setOnClickListener {
            app.currentProfile.customColor = selectedColor
            app.saveProfile()
            app.soundManager.playButton()

            val anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
            binding.btnConfirm.startAnimation(anim)

            startActivity(Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun animateEntrance() {
        listOf(binding.tvTitle, binding.cardPreview, binding.tvColorLabel,
            binding.gridColors, binding.btnReset, binding.btnConfirm)
            .forEachIndexed { i, v ->
                v.alpha = 0f
                v.translationY = 30f
                v.animate().alpha(1f).translationY(0f).setStartDelay(i * 60L).setDuration(300).start()
            }
    }
}
