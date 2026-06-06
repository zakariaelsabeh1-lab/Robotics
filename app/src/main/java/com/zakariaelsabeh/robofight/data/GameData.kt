package com.zakariaelsabeh.robofight.data

import android.graphics.Color
import com.zakariaelsabeh.robofight.data.models.*

object GameData {

    val robots: List<Robot> = listOf(
        Robot(
            id = 0, name = "Blaze", tagline = "Fire warrior who never gives up!",
            primaryColor = Color.parseColor("#FF4500"),
            secondaryColor = Color.parseColor("#FF8C00"),
            accentColor = Color.parseColor("#FFD700"),
            maxHp = 120, attack = 35, defense = 15, speed = 4,
            specialPower = SpecialPower.FIRE_BLAST,
            robotType = RobotType.FIRE, isUnlocked = true
        ),
        Robot(
            id = 1, name = "Thunder", tagline = "Fastest robot with lightning powers!",
            primaryColor = Color.parseColor("#1E90FF"),
            secondaryColor = Color.parseColor("#00BFFF"),
            accentColor = Color.parseColor("#FFFF00"),
            maxHp = 100, attack = 30, defense = 12, speed = 5,
            specialPower = SpecialPower.LIGHTNING_STRIKE,
            robotType = RobotType.THUNDER, isUnlocked = true
        ),
        Robot(
            id = 2, name = "Crusher", tagline = "Toughest armor in the universe!",
            primaryColor = Color.parseColor("#4CAF50"),
            secondaryColor = Color.parseColor("#81C784"),
            accentColor = Color.parseColor("#C0C0C0"),
            maxHp = 150, attack = 25, defense = 25, speed = 2,
            specialPower = SpecialPower.MEGA_PUNCH,
            robotType = RobotType.CRUSHER, isUnlocked = false
        ),
        Robot(
            id = 3, name = "Spark", tagline = "Master of electricity and magic!",
            primaryColor = Color.parseColor("#8A2BE2"),
            secondaryColor = Color.parseColor("#BA55D3"),
            accentColor = Color.parseColor("#00FFFF"),
            maxHp = 110, attack = 32, defense = 18, speed = 3,
            specialPower = SpecialPower.ELECTRIC_STORM,
            robotType = RobotType.SPARK, isUnlocked = false
        ),
        Robot(
            id = 4, name = "Rocket", tagline = "Fires missiles and flies the sky!",
            primaryColor = Color.parseColor("#78909C"),
            secondaryColor = Color.parseColor("#4169E1"),
            accentColor = Color.parseColor("#FF1744"),
            maxHp = 130, attack = 38, defense = 20, speed = 4,
            specialPower = SpecialPower.MISSILE_BARRAGE,
            robotType = RobotType.ROCKET, isUnlocked = false
        ),
        Robot(
            id = 5, name = "Nova", tagline = "Most powerful robot from outer space!",
            primaryColor = Color.parseColor("#FF69B4"),
            secondaryColor = Color.parseColor("#FFB6C1"),
            accentColor = Color.parseColor("#FFFFFF"),
            maxHp = 140, attack = 40, defense = 22, speed = 4,
            specialPower = SpecialPower.STARLIGHT_BURST,
            robotType = RobotType.NOVA, isUnlocked = false
        )
    )

    val weapons: List<Weapon> = listOf(
        Weapon(0, "Laser Sword", WeaponType.SWORD, 25,
            listOf("Crystal Shard", "Metal Rod"), Color.parseColor("#00FFFF"), isCrafted = true),
        Weapon(1, "Thunder Cannon", WeaponType.CANNON, 40,
            listOf("Power Core", "Steel Barrel", "Energy Cell"), Color.parseColor("#FFFF00")),
        Weapon(2, "Rocket Fists", WeaponType.FIST, 30,
            listOf("Rocket Part", "Iron Glove"), Color.parseColor("#FF4500")),
        Weapon(3, "Shield Blaster", WeaponType.SHIELD, 20,
            listOf("Shield Plate", "Blaster Module"), Color.parseColor("#4169E1")),
        Weapon(4, "Freeze Ray", WeaponType.RAY, 35,
            listOf("Ice Crystal", "Ray Gun", "Cooling Unit"), Color.parseColor("#87CEEB")),
        Weapon(5, "Fire Whip", WeaponType.WHIP, 45,
            listOf("Fire Core", "Flexible Cable", "Energy Crystal"), Color.parseColor("#FF6347"))
    )

    val levels: List<Level> = listOf(
        Level(1, "Robot City", "Defend the city from bad robots!", LevelDifficulty.EASY,
            1, listOf(1), listOf(0, 1), isUnlocked = true),
        Level(2, "Thunder District", "Fight through the storm!", LevelDifficulty.EASY,
            2, listOf(1, 2), listOf(2, 3)),
        Level(3, "Metal Factory", "Dangerous machines everywhere!", LevelDifficulty.MEDIUM,
            3, listOf(2, 3), listOf(0, 4)),
        Level(4, "Crystal Caves", "Dark caves full of secrets!", LevelDifficulty.MEDIUM,
            4, listOf(3, 4), listOf(1, 5)),
        Level(5, "Space Station BOSS", "The final battle in space!", LevelDifficulty.BOSS,
            5, listOf(4, 5), listOf(4, 5))
    )

    val treasures: List<Treasure> = listOf(
        Treasure(0, "Golden Gear", "An ancient robot gear!", TreasureType.GEAR, 50, Color.parseColor("#FFD700")),
        Treasure(1, "Crystal Heart", "Glowing with power!", TreasureType.CRYSTAL, 75, Color.parseColor("#FF69B4")),
        Treasure(2, "Power Battery", "Gives extra energy!", TreasureType.BATTERY, 60, Color.parseColor("#00FF7F")),
        Treasure(3, "Ancient Map", "Shows hidden secrets!", TreasureType.MAP, 100, Color.parseColor("#DEB887")),
        Treasure(4, "Robot Crown", "Worn by the champion!", TreasureType.CROWN, 150, Color.parseColor("#FFD700")),
        Treasure(5, "Star Fragment", "A piece of a fallen star!", TreasureType.FRAGMENT, 80, Color.parseColor("#FFFACD"))
    )

    val craftingParts: List<String> = listOf(
        "Crystal Shard", "Metal Rod", "Power Core", "Steel Barrel", "Energy Cell",
        "Rocket Part", "Iron Glove", "Shield Plate", "Blaster Module",
        "Ice Crystal", "Ray Gun", "Cooling Unit", "Fire Core", "Flexible Cable", "Energy Crystal"
    )

    fun getRobotById(id: Int): Robot = robots.first { it.id == id }

    fun getWeaponById(id: Int): Weapon = weapons.first { it.id == id }

    fun getLevelById(id: Int): Level = levels.first { it.id == id }

    fun getTreasureById(id: Int): Treasure = treasures.first { it.id == id }

    fun getEnemyRobotForLevel(levelId: Int): Robot {
        val level = getLevelById(levelId)
        val enemyId = level.enemyRobotIds.random()
        val enemy = getRobotById(enemyId).copy()
        val mult = level.difficulty.multiplier
        return enemy.copy(
            maxHp = (enemy.maxHp * mult).toInt(),
            attack = (enemy.attack * mult).toInt()
        )
    }

    fun getRandomPart(): String = craftingParts.random()
}
