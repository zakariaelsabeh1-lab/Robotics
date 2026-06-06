package com.zakariaelsabeh.robofight.data.models

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Robot(
    val id: Int,
    val name: String,
    val tagline: String,
    val primaryColor: Int,
    val secondaryColor: Int,
    val accentColor: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val specialPower: SpecialPower,
    val robotType: RobotType,
    var isUnlocked: Boolean = false,
    var currentLevel: Int = 1,
    var customColor: Int = 0
) : Parcelable {

    @IgnoredOnParcel
    var currentHp: Int = maxHp

    fun isAlive() = currentHp > 0

    fun takeDamage(rawDamage: Int): Int {
        val actual = maxOf(3, rawDamage - defense / 3)
        currentHp = maxOf(0, currentHp - actual)
        return actual
    }

    fun heal(amount: Int) {
        currentHp = minOf(maxHp, currentHp + amount)
    }

    fun reset() {
        currentHp = maxHp
    }

    fun hpPercent(): Float = currentHp.toFloat() / maxHp.toFloat()

    fun displayColor(): Int = if (customColor != 0) customColor else primaryColor
}

enum class RobotType { FIRE, THUNDER, CRUSHER, SPARK, ROCKET, NOVA }

@Parcelize
enum class SpecialPower(
    val displayName: String,
    val flavour: String,
    val damage: Int,
    val cooldownTurns: Int
) : Parcelable {
    FIRE_BLAST("Fire Blast", "Unleashes a massive fireball!", 60, 3),
    LIGHTNING_STRIKE("Lightning Strike", "Zaps the enemy with lightning!", 55, 3),
    MEGA_PUNCH("Mega Punch", "Deals crushing damage!", 70, 4),
    ELECTRIC_STORM("Electric Storm", "Creates an electric explosion!", 65, 3),
    MISSILE_BARRAGE("Missile Barrage", "Fires multiple missiles!", 75, 4),
    STARLIGHT_BURST("Starlight Burst", "Unleashes star power!", 80, 5)
}
