package com.zakariaelsabeh.robofight.ui.setup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.databinding.ActivityPlayerSetupBinding
import com.zakariaelsabeh.robofight.ui.robot.RobotSelectActivity

class PlayerSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerSetupBinding
    private val app get() = application as GameApplication
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isEditMode = intent.getBooleanExtra("edit_mode", false)
        setupUI()
        animateEntrance()
    }

    private fun setupUI() {
        if (isEditMode) {
            binding.tvTitle.text = "Change Your Name"
            binding.etPlayerName.setText(app.currentProfile.playerName)
            binding.btnContinue.text = "SAVE & CONTINUE"
        }

        val funNames = listOf("RoboKing", "IronHero", "BlazeBot", "StarFighter", "ThunderX")
        binding.etPlayerName.hint = funNames.random()

        binding.etPlayerName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.btnContinue.isEnabled = s?.toString()?.trim()?.isNotEmpty() == true
            }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
        })
        binding.btnContinue.isEnabled = false

        binding.btnContinue.setOnClickListener {
            val name = binding.etPlayerName.text.toString().trim()
            if (name.isNotEmpty()) {
                app.currentProfile.playerName = name
                app.saveProfile()
                app.soundManager.playButton()
                val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
                binding.btnContinue.startAnimation(bounce)
                startActivity(Intent(this, RobotSelectActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        // Fun name suggestion chips
        binding.chip1.setOnClickListener { binding.etPlayerName.setText(funNames[0]) }
        binding.chip2.setOnClickListener { binding.etPlayerName.setText(funNames[1]) }
        binding.chip3.setOnClickListener { binding.etPlayerName.setText(funNames[2]) }
        binding.chip4.setOnClickListener { binding.etPlayerName.setText(funNames[3]) }
    }

    private fun animateEntrance() {
        listOf(binding.tvTitle, binding.tvSubtitle, binding.tvNameLabel,
            binding.etPlayerName, binding.tvSuggestLabel,
            binding.llChips, binding.btnContinue).forEachIndexed { i, v ->
            v.alpha = 0f
            v.translationY = 40f
            v.animate().alpha(1f).translationY(0f).setStartDelay(i * 70L).setDuration(350).start()
        }
    }
}
