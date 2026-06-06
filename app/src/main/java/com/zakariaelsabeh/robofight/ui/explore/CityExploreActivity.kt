package com.zakariaelsabeh.robofight.ui.explore

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.databinding.ActivityCityExploreBinding

class CityExploreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityExploreBinding
    private val app get() = application as GameApplication
    private var itemsCollected = 0
    private var coinsEarned = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profile = app.currentProfile
        val robot = GameData.getRobotById(profile.selectedRobotId)

        binding.cityView.setPlayerColor(
            if (profile.customColor != 0) profile.customColor else robot.primaryColor
        )

        binding.cityView.onItemCollected = { name, value ->
            itemsCollected++
            coinsEarned += value
            app.soundManager.playCollect()
            app.soundManager.vibrateShort()

            // Add parts to inventory
            if (name == "Metal Rod" || name == "Crystal Shard") {
                if (!profile.inventoryParts.contains(name)) {
                    profile.inventoryParts.add(name)
                }
            }
            profile.coins += value
            app.saveProfile()

            updateStats()
            Toast.makeText(this, "Got $name! +$value coins", Toast.LENGTH_SHORT).show()
        }

        updateStats()

        binding.btnBack.setOnClickListener {
            app.soundManager.playButton()
            finish()
        }

        binding.btnRespawn.setOnClickListener {
            binding.cityView.spawnCollectibles()
            Toast.makeText(this, "New items appeared!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStats() {
        val profile = app.currentProfile
        binding.tvStats.text = "🪙 ${profile.coins}  Collected: $itemsCollected"
    }
}
