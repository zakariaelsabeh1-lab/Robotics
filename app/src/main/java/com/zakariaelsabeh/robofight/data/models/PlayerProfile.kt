package com.zakariaelsabeh.robofight.data.models

data class PlayerProfile(
    var playerName: String = "",
    var selectedRobotId: Int = 0,
    var customColor: Int = 0,
    var coins: Int = 100,
    var totalStars: Int = 0,
    var currentLevel: Int = 1,
    var unlockedRobotIds: MutableList<Int> = mutableListOf(0, 1),
    var craftedWeaponIds: MutableList<Int> = mutableListOf(),
    var equippedWeaponId: Int = -1,
    var collectedTreasureIds: MutableList<Int> = mutableListOf(),
    var inventoryParts: MutableList<String> = mutableListOf(),
    var battlesWon: Int = 0,
    var battlesLost: Int = 0
)
