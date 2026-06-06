package com.zakariaelsabeh.robofight.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Treasure(
    val id: Int,
    val name: String,
    val description: String,
    val type: TreasureType,
    val value: Int,
    val color: Int,
    var isFound: Boolean = false
) : Parcelable

@Parcelize
enum class TreasureType(val displayName: String) : Parcelable {
    GEAR("Golden Gear"),
    CRYSTAL("Magic Crystal"),
    BATTERY("Power Battery"),
    MAP("Ancient Map"),
    CROWN("Robot Crown"),
    FRAGMENT("Star Fragment"),
    PART("Weapon Part"),
    COIN("Robot Coin")
}
