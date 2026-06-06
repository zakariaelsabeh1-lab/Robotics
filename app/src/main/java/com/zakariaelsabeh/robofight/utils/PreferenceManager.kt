package com.zakariaelsabeh.robofight.utils

import android.content.Context
import android.content.SharedPreferences
import com.zakariaelsabeh.robofight.data.models.PlayerProfile
import org.json.JSONArray

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("robofight_prefs", Context.MODE_PRIVATE)

    fun saveProfile(profile: PlayerProfile) {
        prefs.edit().apply {
            putString("player_name", profile.playerName)
            putInt("selected_robot_id", profile.selectedRobotId)
            putInt("custom_color", profile.customColor)
            putInt("coins", profile.coins)
            putInt("total_stars", profile.totalStars)
            putInt("current_level", profile.currentLevel)
            putInt("battles_won", profile.battlesWon)
            putInt("battles_lost", profile.battlesLost)
            putInt("equipped_weapon", profile.equippedWeaponId)
            putString("unlocked_robots", profile.unlockedRobotIds.toJsonArray())
            putString("crafted_weapons", profile.craftedWeaponIds.toJsonArray())
            putString("collected_treasures", profile.collectedTreasureIds.toJsonArray())
            putString("inventory_parts", profile.inventoryParts.toJsonArray())
            apply()
        }
    }

    fun loadProfile(): PlayerProfile? {
        if (!prefs.contains("player_name")) return null
        return PlayerProfile(
            playerName = prefs.getString("player_name", "") ?: "",
            selectedRobotId = prefs.getInt("selected_robot_id", 0),
            customColor = prefs.getInt("custom_color", 0),
            coins = prefs.getInt("coins", 100),
            totalStars = prefs.getInt("total_stars", 0),
            currentLevel = prefs.getInt("current_level", 1),
            battlesWon = prefs.getInt("battles_won", 0),
            battlesLost = prefs.getInt("battles_lost", 0),
            equippedWeaponId = prefs.getInt("equipped_weapon", -1),
            unlockedRobotIds = prefs.getString("unlocked_robots", "")!!.fromJsonArrayInt(),
            craftedWeaponIds = prefs.getString("crafted_weapons", "")!!.fromJsonArrayInt(),
            collectedTreasureIds = prefs.getString("collected_treasures", "")!!.fromJsonArrayInt(),
            inventoryParts = prefs.getString("inventory_parts", "")!!.fromJsonArrayString()
        )
    }

    fun hasProfile(): Boolean = prefs.contains("player_name") &&
            prefs.getString("player_name", "").orEmpty().isNotBlank()

    fun clearAll() = prefs.edit().clear().apply()

    fun saveMusicEnabled(enabled: Boolean) = prefs.edit().putBoolean("music", enabled).apply()
    fun isMusicEnabled(): Boolean = prefs.getBoolean("music", true)

    fun saveSoundEnabled(enabled: Boolean) = prefs.edit().putBoolean("sound", enabled).apply()
    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound", true)

    private fun List<Int>.toJsonArray(): String = JSONArray(this).toString()
    private fun List<String>.toJsonArray(): String = JSONArray(this).toString()

    private fun String.fromJsonArrayInt(): MutableList<Int> {
        if (isBlank()) return mutableListOf(0, 1)
        val arr = JSONArray(this)
        return MutableList(arr.length()) { arr.getInt(it) }
    }

    private fun String.fromJsonArrayString(): MutableList<String> {
        if (isBlank()) return mutableListOf()
        val arr = JSONArray(this)
        return MutableList(arr.length()) { arr.getString(it) }
    }
}
