package com.zakariaelsabeh.robofight.ui.workshop

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zakariaelsabeh.robofight.GameApplication
import com.zakariaelsabeh.robofight.R
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.data.models.Weapon
import com.zakariaelsabeh.robofight.databinding.ActivityWorkshopBinding
import com.zakariaelsabeh.robofight.databinding.ItemWeaponCardBinding

class WorkshopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkshopBinding
    private val app get() = application as GameApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkshopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        animateEntrance()
    }

    override fun onResume() {
        super.onResume()
        refreshInventory()
        setupWeaponList()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnGetParts.setOnClickListener {
            app.soundManager.playButton()
            collectRandomParts()
        }

        setupWeaponList()
        refreshInventory()
    }

    private fun collectRandomParts() {
        val profile = app.currentProfile
        if (profile.coins < 20) {
            Toast.makeText(this, "Need 20 coins to get parts!", Toast.LENGTH_SHORT).show()
            return
        }
        val newPart = GameData.getRandomPart()
        profile.coins -= 20
        if (!profile.inventoryParts.contains(newPart)) {
            profile.inventoryParts.add(newPart)
        }
        app.saveProfile()
        refreshInventory()
        setupWeaponList()
        app.soundManager.playCraft()
        Toast.makeText(this, "Got: $newPart! (-20 coins)", Toast.LENGTH_SHORT).show()
    }

    private fun setupWeaponList() {
        val profile = app.currentProfile
        val weapons = GameData.weapons

        binding.rvWeapons.layoutManager = LinearLayoutManager(this)
        binding.rvWeapons.adapter = WeaponAdapter(weapons, profile.craftedWeaponIds,
            profile.inventoryParts, profile.equippedWeaponId,
            onCraft = { weapon ->
                craftWeapon(weapon)
            },
            onEquip = { weapon ->
                equipWeapon(weapon)
            }
        )
    }

    private fun craftWeapon(weapon: Weapon) {
        val profile = app.currentProfile
        if (profile.craftedWeaponIds.contains(weapon.id)) {
            Toast.makeText(this, "${weapon.name} already crafted!", Toast.LENGTH_SHORT).show()
            return
        }

        val missing = weapon.requiredParts.filter { !profile.inventoryParts.contains(it) }
        if (missing.isNotEmpty()) {
            Toast.makeText(this, "Missing: ${missing.joinToString(", ")}", Toast.LENGTH_LONG).show()
            return
        }

        // Remove parts
        weapon.requiredParts.forEach { profile.inventoryParts.remove(it) }
        profile.craftedWeaponIds.add(weapon.id)
        app.saveProfile()

        app.soundManager.playCraft()
        app.soundManager.vibrateMedium()

        val anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
        binding.rvWeapons.startAnimation(anim)

        setupWeaponList()
        refreshInventory()
        Toast.makeText(this, "🔨 ${weapon.name} CRAFTED! Awesome!", Toast.LENGTH_SHORT).show()
    }

    private fun equipWeapon(weapon: Weapon) {
        val profile = app.currentProfile
        profile.equippedWeaponId = if (profile.equippedWeaponId == weapon.id) -1 else weapon.id
        app.saveProfile()
        setupWeaponList()
        val msg = if (profile.equippedWeaponId >= 0) "Equipped: ${weapon.name}!" else "Weapon unequipped"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        app.soundManager.playButton()
    }

    private fun refreshInventory() {
        val profile = app.currentProfile
        binding.tvCoins.text = "🪙 ${profile.coins}"
        val partsText = if (profile.inventoryParts.isEmpty()) "No parts yet"
        else profile.inventoryParts.groupBy { it }
            .map { "${it.value.size}x ${it.key}" }.joinToString(" • ")
        binding.tvInventory.text = "Parts: $partsText"
    }

    private fun animateEntrance() {
        listOf(binding.tvTitle, binding.cardInventory, binding.btnGetParts, binding.rvWeapons)
            .forEachIndexed { i, v ->
                v.alpha = 0f
                v.translationY = 30f
                v.animate().alpha(1f).translationY(0f).setStartDelay(i * 80L).setDuration(350).start()
            }
    }

    inner class WeaponAdapter(
        private val weapons: List<Weapon>,
        private val craftedIds: List<Int>,
        private val parts: List<String>,
        private val equippedId: Int,
        private val onCraft: (Weapon) -> Unit,
        private val onEquip: (Weapon) -> Unit
    ) : RecyclerView.Adapter<WeaponAdapter.VH>() {

        inner class VH(val b: ItemWeaponCardBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, vt: Int) =
            VH(ItemWeaponCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = weapons.size

        override fun onBindViewHolder(holder: VH, pos: Int) {
            val weapon = weapons[pos]
            val crafted = craftedIds.contains(weapon.id)
            val equipped = weapon.id == equippedId
            with(holder.b) {
                tvWeaponName.text = "${weapon.type.emoji} ${weapon.name}"
                tvWeaponName.setTextColor(weapon.color)
                tvDamage.text = "+${weapon.bonusDamage} ATK"

                val allParts = weapon.requiredParts
                val haveParts = allParts.filter { parts.contains(it) }
                tvParts.text = allParts.joinToString(", ") { part ->
                    if (parts.contains(part)) "✅ $part" else "❌ $part"
                }

                when {
                    crafted && equipped -> {
                        btnAction.text = "✅ EQUIPPED"
                        btnAction.setBackgroundColor(Color.parseColor("#1B5E20"))
                        btnAction.isEnabled = true
                        btnAction.setOnClickListener { onEquip(weapon) }
                    }
                    crafted -> {
                        btnAction.text = "EQUIP"
                        btnAction.setBackgroundColor(Color.parseColor("#1565C0"))
                        btnAction.isEnabled = true
                        btnAction.setOnClickListener { onEquip(weapon) }
                    }
                    else -> {
                        val canCraft = allParts.all { parts.contains(it) }
                        btnAction.text = if (canCraft) "🔨 CRAFT!" else "NEED PARTS"
                        btnAction.setBackgroundColor(
                            if (canCraft) Color.parseColor("#E65100") else Color.parseColor("#555555")
                        )
                        btnAction.isEnabled = canCraft
                        btnAction.setOnClickListener { onCraft(weapon) }
                    }
                }

                root.setStrokeColor(android.content.res.ColorStateList.valueOf(
                    if (equipped) weapon.color else Color.TRANSPARENT))
                root.strokeWidth = if (equipped) 3 else 0

                val progress = (haveParts.size.toFloat() / allParts.size * 100).toInt()
                progressCraft.progress = progress
            }
        }
    }
}
