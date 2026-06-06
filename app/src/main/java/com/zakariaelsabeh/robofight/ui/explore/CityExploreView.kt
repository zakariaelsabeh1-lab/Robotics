package com.zakariaelsabeh.robofight.ui.explore

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class CityExploreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var onItemCollected: ((String, Int) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var playerX = 0f
    private var playerY = 0f
    private var playerTargetX = 0f
    private var playerTargetY = 0f
    private var playerColor = Color.parseColor("#FF4500")
    private var animTick = 0f
    private var walkAnim = 0f

    private val buildings = mutableListOf<Building>()
    private val collectibles = mutableListOf<Collectible>()
    private val collectibleTypes = listOf(
        "🪙" to "Robot Coin" to 10,
        "⚡" to "Energy" to 15,
        "🔩" to "Metal Rod" to 20,
        "💎" to "Crystal Shard" to 25
    )

    private var frameAnimator: ValueAnimator? = null
    private val rng = Random(System.currentTimeMillis())

    init {
        post {
            playerX = width / 2f
            playerY = height * 0.7f
            playerTargetX = playerX
            playerTargetY = playerY
            generateCity()
            startGameLoop()
        }
    }

    fun setPlayerColor(color: Int) { playerColor = color }

    private fun generateCity() {
        val w = width.toFloat()
        val h = height.toFloat()

        buildings.clear()
        // Left buildings
        buildings.add(Building(20f, h * 0.3f, 100f, h * 0.5f, Color.parseColor("#2C3E50"), "SHOP"))
        buildings.add(Building(130f, h * 0.15f, 100f, h * 0.65f, Color.parseColor("#1A252F"), "ARENA"))
        buildings.add(Building(20f, h * 0.52f, 100f, h * 0.28f, Color.parseColor("#34495E"), "BANK"))
        // Right buildings
        buildings.add(Building(w - 130f, h * 0.22f, 100f, h * 0.58f, Color.parseColor("#2C3E50"), "LAB"))
        buildings.add(Building(w - 240f, h * 0.10f, 100f, h * 0.70f, Color.parseColor("#1A252F"), "TOWER"))
        buildings.add(Building(w - 130f, h * 0.52f, 100f, h * 0.28f, Color.parseColor("#34495E"), "STORE"))

        spawnCollectibles()
    }

    fun spawnCollectibles() {
        collectibles.clear()
        val w = width.toFloat()
        val h = height.toFloat()
        repeat(8) {
            val cx = 150f + rng.nextFloat() * (w - 300f)
            val cy = h * 0.4f + rng.nextFloat() * (h * 0.35f)
            val typeIdx = rng.nextInt(collectibleTypes.size)
            val (pair, value) = collectibleTypes[typeIdx]
            val (emoji, name) = pair
            collectibles.add(Collectible(cx, cy, emoji, name, value,
                Color.parseColor(listOf("#FFD700", "#00FF7F", "#00BFFF", "#FF69B4")[typeIdx])))
        }
    }

    private fun startGameLoop() {
        frameAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateGame()
                invalidate()
            }
            start()
        }
    }

    private fun updateGame() {
        animTick += 0.05f
        val dx = playerTargetX - playerX
        val dy = playerTargetY - playerY
        val dist = sqrt(dx * dx + dy * dy)
        if (dist > 4f) {
            val speed = 5f
            playerX += dx / dist * speed
            playerY += dy / dist * speed
            walkAnim += 0.25f
        } else {
            walkAnim = 0f
        }

        // Check collectible pickup
        val it = collectibles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            val cdx = c.x - playerX
            val cdy = c.y - playerY
            if (sqrt(cdx * cdx + cdy * cdy) < 40f) {
                onItemCollected?.invoke(c.name, c.value)
                it.remove()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            playerTargetX = event.x
            playerTargetY = event.y.coerceIn(height * 0.3f, height * 0.92f)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        drawSkyAndGround(canvas, w, h)
        drawBuildings(canvas, h)
        drawRoad(canvas, w, h)
        drawCollectibles(canvas)
        drawPlayer(canvas)
        drawHint(canvas, w, h)
    }

    private fun drawSkyAndGround(canvas: Canvas, w: Float, h: Float) {
        val sky = LinearGradient(0f, 0f, 0f, h * 0.65f,
            Color.parseColor("#1A1A4E"), Color.parseColor("#4A4A8A"), Shader.TileMode.CLAMP)
        paint.shader = sky
        canvas.drawRect(0f, 0f, w, h * 0.65f, paint)
        paint.shader = null

        // Stars
        paint.color = Color.WHITE
        val srng = Random(99)
        repeat(40) {
            val sx = srng.nextFloat() * w
            val sy = srng.nextFloat() * h * 0.5f
            val br = 0.3f + 0.7f * abs(sin((animTick * 0.5f + it).toDouble())).toFloat()
            paint.alpha = (br * 180).toInt()
            canvas.drawCircle(sx, sy, 1.5f, paint)
        }
        paint.alpha = 255

        // Ground
        val ground = LinearGradient(0f, h * 0.65f, 0f, h,
            Color.parseColor("#2D4A1E"), Color.parseColor("#1A2D10"), Shader.TileMode.CLAMP)
        paint.shader = ground
        canvas.drawRect(0f, h * 0.65f, w, h, paint)
        paint.shader = null
    }

    private fun drawBuildings(canvas: Canvas, h: Float) {
        buildings.forEach { b ->
            // Shadow
            paint.color = Color.argb(80, 0, 0, 0)
            canvas.drawRect(b.x + 5, b.y + 5, b.x + b.w + 5, b.y + b.bh + 5, paint)
            // Body
            paint.color = b.color
            canvas.drawRect(b.x, b.y, b.x + b.w, b.y + b.bh, paint)
            // Windows
            paint.color = Color.argb(180, 255, 255, 150)
            val rows = ((b.bh / 25).toInt()).coerceAtMost(8)
            val cols = ((b.w / 20).toInt()).coerceAtMost(4)
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val wx = b.x + 6 + c * 22f
                    val wy = b.y + 10 + r * 26f
                    if (wy + 15 < b.y + b.bh - 5) {
                        val lit = (r + c + (animTick * 0.3f).toInt()) % 4 != 0
                        paint.color = if (lit) Color.argb(180, 255, 255, 150)
                        else Color.argb(80, 50, 50, 80)
                        canvas.drawRect(wx, wy, wx + 14f, wy + 18f, paint)
                    }
                }
            }
            // Door
            paint.color = Color.parseColor("#8B4513")
            val dw = b.w * 0.25f
            canvas.drawRect(b.x + (b.w - dw) / 2, b.y + b.bh - 28, b.x + (b.w + dw) / 2, b.y + b.bh, paint)
            // Label
            paint.color = Color.parseColor("#FFD700")
            paint.textSize = 16f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(b.label, b.x + b.w / 2, b.y + 18, paint)
        }
    }

    private fun drawRoad(canvas: Canvas, w: Float, h: Float) {
        paint.color = Color.parseColor("#333333")
        canvas.drawRect(130f, h * 0.64f, w - 140f, h * 0.72f, paint)
        paint.color = Color.parseColor("#FFFF00")
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        val dash = (animTick * 2f) % 40f
        var x = 130f - dash
        while (x < w - 140f) {
            canvas.drawLine(x, h * 0.68f, x + 20f, h * 0.68f, paint)
            x += 40f
        }
        paint.style = Paint.Style.FILL
    }

    private fun drawCollectibles(canvas: Canvas) {
        collectibles.forEach { c ->
            val bobY = sin(animTick + c.x * 0.05f).toFloat() * 5f
            // Glow ring
            paint.color = Color.argb(60, Color.red(c.color), Color.green(c.color), Color.blue(c.color))
            canvas.drawCircle(c.x, c.y + bobY, 28f, paint)
            // Inner circle
            paint.color = c.color
            canvas.drawCircle(c.x, c.y + bobY, 20f, paint)
            // Emoji/symbol
            paint.color = Color.WHITE
            paint.textSize = 26f
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(c.emoji, c.x, c.y + bobY + 9f, paint)
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        val legOff = sin(walkAnim).toFloat() * 8f
        // Shadow
        paint.color = Color.argb(80, 0, 0, 0)
        canvas.drawOval(playerX - 18, playerY + 28, playerX + 18, playerY + 38, paint)
        // Legs
        paint.color = Color.parseColor("#2C3E50")
        canvas.drawRoundRect(playerX - 12, playerY + 14 + legOff, playerX - 2, playerY + 30, 4f, 4f, paint)
        canvas.drawRoundRect(playerX + 2, playerY + 14 - legOff, playerX + 12, playerY + 30, 4f, 4f, paint)
        // Body
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 16, playerY - 4, playerX + 16, playerY + 18, 6f, 6f, paint)
        // Arms (swing)
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 24, playerY - 2 + legOff, playerX - 14, playerY + 14, 4f, 4f, paint)
        canvas.drawRoundRect(playerX + 14, playerY - 2 - legOff, playerX + 24, playerY + 14, 4f, 4f, paint)
        // Head
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 14, playerY - 24, playerX + 14, playerY, 7f, 7f, paint)
        // Eyes
        paint.color = Color.WHITE
        canvas.drawCircle(playerX - 5, playerY - 14, 4f, paint)
        canvas.drawCircle(playerX + 5, playerY - 14, 4f, paint)
        paint.color = Color.parseColor("#001133")
        canvas.drawCircle(playerX - 4, playerY - 14, 2f, paint)
        canvas.drawCircle(playerX + 6, playerY - 14, 2f, paint)
    }

    private fun drawHint(canvas: Canvas, w: Float, h: Float) {
        if (collectibles.isEmpty()) {
            paint.color = Color.parseColor("#FFD700")
            paint.textSize = 28f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("All collected! Amazing!", w / 2, h * 0.95f, paint)
        } else {
            paint.color = Color.argb(180, 255, 255, 255)
            paint.textSize = 22f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT
            canvas.drawText("Tap to move! Collect the glowing items!", w / 2, h * 0.97f, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        frameAnimator?.cancel()
    }

    private data class Building(val x: Float, val y: Float, val w: Float, val bh: Float,
                                val color: Int, val label: String)
    private data class Collectible(var x: Float, var y: Float, val emoji: String,
                                   val name: String, val value: Int, val color: Int)
}
