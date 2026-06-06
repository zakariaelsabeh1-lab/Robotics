package com.zakariaelsabeh.robofight.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Level(
    val id: Int,
    val name: String,
    val description: String,
    val difficulty: LevelDifficulty,
    val worldNumber: Int,
    val enemyRobotIds: List<Int>,
    val treasureIds: List<Int> = emptyList(),
    var starsEarned: Int = 0,
    var isUnlocked: Boolean = false,
    var isCompleted: Boolean = false
) : Parcelable

@Parcelize
enum class LevelDifficulty(val label: String, val multiplier: Float) : Parcelable {
    EASY("Easy", 0.8f),
    MEDIUM("Medium", 1.0f),
    HARD("Hard", 1.3f),
    BOSS("BOSS!", 1.6f)
}
