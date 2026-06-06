package com.zakariaelsabeh.robofight.ui.robot

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.databinding.ActivityRobotSelectBinding
import com.zakariaelsabeh.robofight.databinding.ItemRobotCardBinding
import com.zakariaelsabeh.robofight.ui.customize.CustomizeActivity
import com.zakariaelsabeh.robofight.ui.views.RobotIconView

class RobotSelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRobotSelectBinding
    private val app get() = application as GameApplication
    private var selectedRobotId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRobotSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedRobotId = app.currentProfile.selectedRobotId
        setupRecycler()
        setupButtons()
        animateEntrance()
    }

    private fun setupRecycler() {
        val robots = GameData.robots
        val unlockedIds = app.currentProfile.unlockedRobotIds

        binding.rvRobots.layoutManager = GridLayoutManager(this, 2)
        binding.rvRobots.adapter = RobotAdapter(robots, unlockedIds, selectedRobotId) { robot ->
            if (robot.isUnlocked || unlockedIds.contains(robot.id)) {
                selectedRobotId = robot.id
                updatePreview(robot)
                app.soundManager.playButton()
            } else {
                showLockedMessage(robot)
            }
        }
        updatePreview(GameData.getRobotById(selectedRobotId))
    }

    private fun updatePreview(robot: Robot) {
        binding.ivPreviewRobot.setRobot(robot)
        binding.tvPreviewName.text = robot.name
        binding.tvPreviewTagline.text = robot.tagline
        binding.tvPreviewSpecial.text = "⚡ ${robot.specialPower.displayName}"

        binding.barHp.progress = robot.maxHp
        binding.barAttack.progress = robot.attack
        binding.barDefense.progress = robot.defense
        binding.barSpeed.progress = robot.speed * 20

        binding.tvStatHp.text = "HP: ${robot.maxHp}"
        binding.tvStatAttack.text = "ATK: ${robot.attack}"
        binding.tvStatDefense.text = "DEF: ${robot.defense}"
        binding.tvStatSpeed.text = "SPD: ${"★".repeat(robot.speed)}"

        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.ivPreviewRobot.startAnimation(bounce)
    }

    private fun showLockedMessage(robot: Robot) {
        val msgs = listOf(
            "Win more battles to unlock ${robot.name}!",
            "${robot.name} is waiting for a champion!",
            "Keep fighting to unlock ${robot.name}!"
        )
        android.widget.Toast.makeText(this, "🔒 ${msgs.random()}", android.widget.Toast.LENGTH_SHORT).show()
    }

    private fun setupButtons() {
        binding.btnSelect.setOnClickListener {
            app.currentProfile.selectedRobotId = selectedRobotId
            app.saveProfile()
            app.soundManager.playButton()
            startActivity(Intent(this, CustomizeActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun animateEntrance() {
        binding.tvTitle.alpha = 0f
        binding.tvTitle.animate().alpha(1f).setDuration(400).start()
    }

    inner class RobotAdapter(
        private val robots: List<Robot>,
        private val unlockedIds: List<Int>,
        private var selectedId: Int,
        private val onSelect: (Robot) -> Unit
    ) : RecyclerView.Adapter<RobotAdapter.VH>() {

        inner class VH(val b: ItemRobotCardBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, vt: Int) =
            VH(ItemRobotCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = robots.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val robot = robots[pos]
            val unlocked = robot.isUnlocked || unlockedIds.contains(robot.id)
            with(holder.b) {
                ivRobot.setRobot(robot)
                tvName.text = robot.name
                tvType.text = robot.robotType.name

                if (unlocked) {
                    tvLocked.visibility = View.GONE
                    root.alpha = 1f
                } else {
                    tvLocked.visibility = View.VISIBLE
                    ivRobot.alpha = 0.4f
                    root.alpha = 0.7f
                }

                val isSelected = robot.id == selectedId
                root.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    if (isSelected) robot.primaryColor else Color.TRANSPARENT))
                root.strokeWidth = if (isSelected) 4 else 0

                root.setOnClickListener {
                    selectedId = robot.id
                    notifyDataSetChanged()
                    onSelect(robot)
                }

                // Difficulty stars
                tvDifficulty.text = when (robot.id) {
                    0, 1 -> "★☆☆"
                    2, 3 -> "★★☆"
                    else -> "★★★"
                }
                tvDifficulty.setTextColor(robot.primaryColor)
            }
        }
    }
}
