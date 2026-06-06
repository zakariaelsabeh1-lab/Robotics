package com.zakariaelsabeh.robofight.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weapon(
    val id: Int,
    val name: String,
    val type: WeaponType,
    val bonusDamage: Int,
    val requiredParts: List<String>,
    val color: Int,
    var isCrafted: Boolean = false,
    var isEquipped: Boolean = false
) : Parcelable

@Parcelize
enum class WeaponType(val displayName: String, val emoji: String) : Parcelable {
    SWORD("Laser Sword", "⚔️"),
    CANNON("Thunder Cannon", "💥"),
    FIST("Rocket Fists", "🤜"),
    SHIELD("Shield Blaster", "🛡"),
    RAY("Freeze Ray", "❄️"),
    WHIP("Fire Whip", "🔥")
}
