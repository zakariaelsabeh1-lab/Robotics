package com.zakariaelsabeh.robofight.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build

class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<String, Int>()
    private var soundEnabled = true
    private var vibrationEnabled = true

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attrs)
            .build()
    }

    fun setSoundEnabled(enabled: Boolean) { soundEnabled = enabled }
    fun setVibrationEnabled(enabled: Boolean) { vibrationEnabled = enabled }

    fun playAttack() = playTone(1)
    fun playSpecial() = playTone(2)
    fun playHit() = playTone(3)
    fun playVictory() = playTone(4)
    fun playDefeat() = playTone(5)
    fun playCollect() = playTone(6)
    fun playCraft() = playTone(7)
    fun playButton() = playTone(8)

    private fun playTone(id: Int) {
        if (!soundEnabled) return
    }

    fun vibrateShort() {
        if (!vibrationEnabled) return
        vibrate(50)
    }

    fun vibrateMedium() {
        if (!vibrationEnabled) return
        vibrate(150)
    }

    fun vibrateLong() {
        if (!vibrationEnabled) return
        vibrate(400)
    }

    private fun vibrate(millis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(millis)
                }
            }
        } catch (_: Exception) {}
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
