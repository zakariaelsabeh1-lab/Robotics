package com.zakariaelsabeh.robofight.ui.treasure

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.databinding.ActivityTreasureBinding

class TreasureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTreasureBinding
    private val app get() = application as GameApplication
    private var totalFound = 0
    private var totalValue = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTreasureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.treasureView.onTreasureFound = { name, value ->
            totalFound++
            totalValue += value
            app.soundManager.playCollect()
            app.soundManager.vibrateMedium()

            app.currentProfile.coins += value
            if (!app.currentProfile.collectedTreasureIds.contains(totalFound)) {
                app.currentProfile.collectedTreasureIds.add(totalFound)
            }
            app.saveProfile()

            updateStats()
            Toast.makeText(this, "🎉 Found: $name! +$value coins!", Toast.LENGTH_SHORT).show()
        }

        updateStats()

        binding.btnBack.setOnClickListener {
            app.soundManager.playButton()
            finish()
        }

        binding.btnRefresh.setOnClickListener {
            binding.treasureView.refreshChests()
            Toast.makeText(this, "New chests appeared!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStats() {
        binding.tvStats.text = "Found: $totalFound  🪙 +$totalValue  Total: ${app.currentProfile.coins}"
    }
}
