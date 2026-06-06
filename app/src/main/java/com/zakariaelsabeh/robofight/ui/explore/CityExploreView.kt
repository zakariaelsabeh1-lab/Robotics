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
    private var playerX = 0f; private var playerY = 0f
    private var playerTargetX = 0f; private var playerTargetY = 0f
    private var playerColor = Color.parseColor("#FF4500")
    private var animTick = 0f; private var walkAnim = 0f

    private val buildings = mutableListOf<Building>()
    private val collectibles = mutableListOf<Collectible>()
    private val collectibleTypes = listOf(
        Triple("Coin", "Robot Coin", 10),
        Triple("Energy", "Energy Cell", 15),
        Triple("Part", "Metal Rod", 20),
        Triple("Gem", "Crystal Shard", 25)
    )
    private val collectibleColors = listOf(
        Color.parseColor("#FFD700"), Color.parseColor("#00FFCC"),
        Color.parseColor("#FF6600"), Color.parseColor("#CC00FF")
    )

    private var frameAnimator: ValueAnimator? = null
    private val rng = Random(System.currentTimeMillis())

    // Neon sign data (x, y, label, color)
    private val neonSigns = mutableListOf<NeonSign>()

    init {
        post {
            playerX = width / 2f; playerY = height * 0.72f
            playerTargetX = playerX; playerTargetY = playerY
            generateCity(); startGameLoop()
        }
    }

    fun setPlayerColor(color: Int) { playerColor = color }

    private fun generateCity() {
        val w = width.toFloat(); val h = height.toFloat()
        buildings.clear(); neonSigns.clear()

        // Left district
        buildings.add(Building(0f,    h * 0.28f, 90f,  h * 0.52f, Color.parseColor("#131320"), Color.parseColor("#0066CC"), "ARENA"))
        buildings.add(Building(85f,   h * 0.14f, 85f,  h * 0.66f, Color.parseColor("#0F0F1E"), Color.parseColor("#FF4400"), "FORGE"))
        buildings.add(Building(5f,    h * 0.52f, 88f,  h * 0.28f, Color.parseColor("#181828"), Color.parseColor("#009900"), "SHOP"))
        buildings.add(Building(162f,  h * 0.22f, 80f,  h * 0.58f, Color.parseColor("#101022"), Color.parseColor("#CC00FF"), "LAB"))

        // Right district
        buildings.add(Building(w-90f,  h * 0.20f, 90f, h * 0.60f, Color.parseColor("#131320"), Color.parseColor("#FF0044"), "TOWER"))
        buildings.add(Building(w-175f, h * 0.10f, 88f, h * 0.70f, Color.parseColor("#0F0F1E"), Color.parseColor("#00DDFF"), "BANK"))
        buildings.add(Building(w-90f,  h * 0.52f, 90f, h * 0.28f, Color.parseColor("#181828"), Color.parseColor("#FFAA00"), "STORE"))
        buildings.add(Building(w-255f, h * 0.24f, 82f, h * 0.56f, Color.parseColor("#101022"), Color.parseColor("#44FF66"), "DOJO"))

        // Neon signs on buildings
        for (b in buildings) {
            neonSigns.add(NeonSign(b.x + b.w / 2, b.y + 8f, b.label, b.accentColor))
        }

        spawnCollectibles()
    }

    fun spawnCollectibles() {
        collectibles.clear()
        val w = width.toFloat(); val h = height.toFloat()
        repeat(9) {
            val cx = 180f + rng.nextFloat() * (w - 360f)
            val cy = h * 0.42f + rng.nextFloat() * (h * 0.32f)
            val typeIdx = rng.nextInt(collectibleTypes.size)
            val (symbol, name, value) = collectibleTypes[typeIdx]
            collectibles.add(Collectible(cx, cy, symbol, name, value, collectibleColors[typeIdx]))
        }
    }

    private fun startGameLoop() {
        frameAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16; repeatCount = ValueAnimator.INFINITE
            addUpdateListener { updateGame(); invalidate() }
            start()
        }
    }

    private fun updateGame() {
        animTick += 0.04f
        val dx = playerTargetX - playerX; val dy = playerTargetY - playerY
        val dist = sqrt(dx * dx + dy * dy)
        if (dist > 5f) {
            val speed = 5.5f; playerX += dx / dist * speed; playerY += dy / dist * speed
            walkAnim += 0.28f
        } else { walkAnim = 0f }

        val it = collectibles.iterator()
        while (it.hasNext()) {
            val c = it.next()
            val cdx = c.x - playerX; val cdy = c.y - playerY
            if (sqrt(cdx * cdx + cdy * cdy) < 42f) {
                onItemCollected?.invoke(c.name, c.value); it.remove()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            playerTargetX = event.x
            playerTargetY = event.y.coerceIn(height * 0.32f, height * 0.9f)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat(); val h = height.toFloat()
        drawNightSky(canvas, w, h)
        drawFarSkyline(canvas, w, h)
        drawBuildings(canvas, h)
        drawStreet(canvas, w, h)
        drawCollectibles(canvas)
        drawPlayer(canvas)
        drawHUD(canvas, w, h)
    }

    private fun drawNightSky(canvas: Canvas, w: Float, h: Float) {
        val skyGrad = LinearGradient(0f, 0f, 0f, h * 0.62f,
            Color.parseColor("#020208"), Color.parseColor("#0A0A22"), Shader.TileMode.CLAMP)
        paint.shader = skyGrad; canvas.drawRect(0f, 0f, w, h * 0.62f, paint); paint.shader = null

        // Animated stars
        val sRng = Random(55)
        repeat(50) {
            val sx = sRng.nextFloat() * w; val sy = sRng.nextFloat() * h * 0.5f
            val br = 0.3f + 0.7f * abs(sin((animTick * 0.6f + it * 0.9f).toDouble())).toFloat()
            paint.color = Color.WHITE; paint.alpha = (br * 180).toInt()
            canvas.drawCircle(sx, sy, 0.8f + sRng.nextFloat() * 1.8f, paint)
        }
        paint.alpha = 255
    }

    private fun drawFarSkyline(canvas: Canvas, w: Float, h: Float) {
        val horizonY = h * 0.62f
        // Silhouette buildings in the far background
        paint.color = Color.parseColor("#070712")
        val farB = listOf(
            floatArrayOf(170f, 60f, 130f), floatArrayOf(225f, 80f, 100f),
            floatArrayOf(295f, 55f, 160f), floatArrayOf(345f, 70f, 115f),
            floatArrayOf(w/2 - 40f, 50f, 140f), floatArrayOf(w/2 + 30f, 65f, 120f)
        )
        for (b in farB) canvas.drawRect(b[0], horizonY - b[2], b[0] + b[1], horizonY, paint)
        // Antenna lights on tall buildings
        paint.color = Color.parseColor("#FF2200")
        repeat(4) {
            val bx = 230f + it * 90f; paint.alpha = (150 + 100 * sin((animTick + it).toDouble())).toInt().coerceIn(50, 255)
            canvas.drawCircle(bx, horizonY - 135f + it * 15f, 3f, paint)
        }
        paint.alpha = 255
    }

    private fun drawBuildings(canvas: Canvas, h: Float) {
        val horizonY = h * 0.62f
        buildings.forEach { b ->
            // Shadow
            paint.color = Color.argb(60, 0, 0, 20)
            canvas.drawRect(b.x + 6, b.y + 6, b.x + b.w + 6, b.y + b.bh + 6, paint)
            // Body gradient
            val bGrad = LinearGradient(b.x, b.y, b.x + b.w, b.y,
                Color.argb(255, Color.red(b.color)+20, Color.green(b.color)+20, Color.blue(b.color)+20),
                b.color, Shader.TileMode.CLAMP)
            paint.shader = bGrad; canvas.drawRect(b.x, b.y, b.x + b.w, b.y + b.bh, paint)
            paint.shader = null
            // Top accent stripe
            paint.color = b.accentColor; paint.alpha = 180
            canvas.drawRect(b.x, b.y, b.x + b.w, b.y + 6f, paint)
            paint.alpha = 255
            // Windows (animated)
            val rows = ((b.bh / 22).toInt()).coerceAtMost(9)
            val cols = ((b.w / 18).toInt()).coerceAtMost(4)
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val wx = b.x + 5 + c * 20f; val wy = b.y + 14 + r * 22f
                    if (wy + 14 < b.y + b.bh - 8) {
                        val lit = ((r * 3 + c * 7 + (animTick * 0.08f).toInt()) % 6) != 0
                        if (lit) {
                            // Warm window glow
                            paint.color = Color.argb(40, 255, 240, 150)
                            canvas.drawRect(wx - 2, wy - 2, wx + 14f, wy + 16f, paint)
                            paint.color = Color.argb(200, 255, 240, 150)
                            canvas.drawRect(wx, wy, wx + 12f, wy + 14f, paint)
                        } else {
                            paint.color = Color.argb(60, 30, 30, 60)
                            canvas.drawRect(wx, wy, wx + 12f, wy + 14f, paint)
                        }
                    }
                }
            }
            // Metal door
            paint.color = Color.parseColor("#1A1A2E")
            val dw = b.w * 0.28f
            canvas.drawRect(b.x + (b.w - dw) / 2, b.y + b.bh - 30, b.x + (b.w + dw) / 2, b.y + b.bh, paint)
            paint.color = b.accentColor
            canvas.drawRect(b.x + (b.w - dw) / 2 + 2, b.y + b.bh - 28, b.x + b.w / 2, b.y + b.bh - 2, paint)
        }

        // Neon signs
        neonSigns.forEach { sign ->
            val pulse = abs(sin((animTick * 0.7f + sign.x * 0.01f).toDouble())).toFloat()
            // Glow halo
            paint.color = Color.argb((40 + (40 * pulse).toInt()),
                Color.red(sign.color), Color.green(sign.color), Color.blue(sign.color))
            canvas.drawRoundRect(sign.x - 32f, sign.y - 8f, sign.x + 32f, sign.y + 12f, 4f, 4f, paint)
            // Sign face
            paint.color = Color.argb((180 + (75 * pulse).toInt()),
                Color.red(sign.color), Color.green(sign.color), Color.blue(sign.color))
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 1.5f
            canvas.drawRoundRect(sign.x - 30f, sign.y - 7f, sign.x + 30f, sign.y + 11f, 3f, 3f, paint)
            paint.style = Paint.Style.FILL
            // Sign text
            paint.color = sign.color; paint.textSize = 14f
            paint.textAlign = Paint.Align.CENTER; paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(sign.label, sign.x, sign.y + 7f, paint)
        }
    }

    private fun drawStreet(canvas: Canvas, w: Float, h: Float) {
        val horizonY = h * 0.62f
        // Street gradient (dark asphalt)
        val streetGrad = LinearGradient(0f, horizonY, 0f, h,
            Color.parseColor("#141420"), Color.parseColor("#08080F"), Shader.TileMode.CLAMP)
        paint.shader = streetGrad; canvas.drawRect(0f, horizonY, w, h, paint); paint.shader = null

        // Road markings
        paint.color = Color.parseColor("#333340"); paint.style = Paint.Style.FILL
        canvas.drawRect(170f, horizonY, w - 180f, h * 0.68f, paint)

        // Animated dashed center line
        paint.color = Color.parseColor("#555500"); paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        val dash = (animTick * 3f) % 50f; var x = 170f - dash
        while (x < w - 180f) {
            canvas.drawLine(x, h * 0.65f, x + 24f, h * 0.65f, paint); x += 50f
        }
        paint.style = Paint.Style.FILL

        // Sidewalk lines
        paint.color = Color.parseColor("#1C1C2C")
        canvas.drawRect(0f, h * 0.68f, 170f, h * 0.72f, paint)
        canvas.drawRect(w - 180f, h * 0.68f, w, h * 0.72f, paint)

        // Streetlights
        drawStreetlight(canvas, 162f, h * 0.62f, h)
        drawStreetlight(canvas, w - 162f, h * 0.62f, h)

        // Neon ground reflections
        paint.color = Color.argb(15, 0, 150, 255)
        canvas.drawRect(0f, h * 0.72f, w, h, paint)
    }

    private fun drawStreetlight(canvas: Canvas, x: Float, topY: Float, h: Float) {
        paint.color = Color.parseColor("#2A2A3A"); paint.strokeWidth = 4f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(x, topY, x, h * 0.74f, paint)
        paint.style = Paint.Style.FILL
        // Arm
        paint.color = Color.parseColor("#2A2A3A")
        canvas.drawRoundRect(x - 16f, topY - 2f, x, topY + 4f, 3f, 3f, paint)
        // Light bulb with glow
        val glowAlpha = (100 + 80 * abs(sin((animTick * 0.3f).toDouble()))).toInt().coerceIn(60, 255)
        paint.color = Color.argb(glowAlpha, 255, 220, 100)
        canvas.drawCircle(x - 18f, topY + 2f, 10f, paint)
        paint.color = Color.argb((glowAlpha / 3), 255, 220, 100)
        canvas.drawCircle(x - 18f, topY + 2f, 22f, paint)
    }

    private fun drawCollectibles(canvas: Canvas) {
        collectibles.forEach { c ->
            val bobY = sin(animTick + c.x * 0.05f).toFloat() * 6f
            // Outer glow
            paint.color = Color.argb(50, Color.red(c.color), Color.green(c.color), Color.blue(c.color))
            canvas.drawCircle(c.x, c.y + bobY, 30f, paint)
            paint.color = Color.argb(100, Color.red(c.color), Color.green(c.color), Color.blue(c.color))
            canvas.drawCircle(c.x, c.y + bobY, 22f, paint)
            // Inner filled circle
            paint.color = c.color; canvas.drawCircle(c.x, c.y + bobY, 16f, paint)
            // Symbol
            paint.color = Color.WHITE; paint.textSize = 20f
            paint.textAlign = Paint.Align.CENTER; paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(c.symbol, c.x, c.y + bobY + 7f, paint)
            // Value label
            paint.textSize = 13f; paint.color = c.color
            canvas.drawText("+${c.value}", c.x, c.y + bobY + 32f + 4f, paint)
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        val legOff = sin(walkAnim).toFloat() * 9f
        val py = playerY
        // Shadow
        paint.color = Color.argb(70, 0, 0, 0)
        canvas.drawOval(playerX - 18, py + 30, playerX + 18, py + 40, paint)
        // Legs
        paint.color = Color.parseColor("#1A1A2E")
        canvas.drawRoundRect(playerX - 12, py + 16 + legOff, playerX - 3, py + 32, 4f, 4f, paint)
        canvas.drawRoundRect(playerX + 3, py + 16 - legOff, playerX + 12, py + 32, 4f, 4f, paint)
        // Feet
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 15, py + 29, playerX - 1, py + 36, 4f, 4f, paint)
        canvas.drawRoundRect(playerX + 1, py + 29, playerX + 15, py + 36, 4f, 4f, paint)
        // Body
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 17, py - 5, playerX + 17, py + 19, 6f, 6f, paint)
        // Chest accent
        paint.color = Color.argb(180, 255, 255, 255)
        canvas.drawRoundRect(playerX - 8, py + 2, playerX + 8, py + 10, 3f, 3f, paint)
        // Arms
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 25, py - 3 + legOff, playerX - 15, py + 15, 4f, 4f, paint)
        canvas.drawRoundRect(playerX + 15, py - 3 - legOff, playerX + 25, py + 15, 4f, 4f, paint)
        // Head
        paint.color = playerColor
        canvas.drawRoundRect(playerX - 15, py - 26, playerX + 15, py - 1, 7f, 7f, paint)
        // Visor
        paint.color = Color.parseColor("#080820")
        canvas.drawRoundRect(playerX - 12, py - 22, playerX + 12, py - 10, 4f, 4f, paint)
        // Eyes
        paint.color = Color.parseColor("#00DDFF")
        canvas.drawCircle(playerX - 5, py - 16, 3.5f, paint)
        canvas.drawCircle(playerX + 5, py - 16, 3.5f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(playerX - 4, py - 17, 1.5f, paint)
        canvas.drawCircle(playerX + 6, py - 17, 1.5f, paint)
        // Antenna
        paint.color = playerColor; paint.strokeWidth = 2f; paint.style = Paint.Style.STROKE
        canvas.drawLine(playerX, py - 26, playerX, py - 34, paint)
        paint.style = Paint.Style.FILL; paint.color = Color.parseColor("#00DDFF")
        canvas.drawCircle(playerX, py - 35, 3f, paint)
    }

    private fun drawHUD(canvas: Canvas, w: Float, h: Float) {
        paint.textAlign = Paint.Align.CENTER; paint.typeface = Typeface.DEFAULT_BOLD
        if (collectibles.isEmpty()) {
            paint.color = Color.parseColor("#FFD700"); paint.textSize = 26f
            canvas.drawText("All items collected! Level complete!", w / 2, h * 0.96f, paint)
        } else {
            paint.color = Color.argb(160, 180, 200, 255); paint.textSize = 20f
            canvas.drawText("Tap to move  •  ${collectibles.size} items remaining", w / 2, h * 0.97f, paint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow(); frameAnimator?.cancel()
    }

    private data class Building(val x: Float, val y: Float, val w: Float, val bh: Float,
                                val color: Int, val accentColor: Int, val label: String)
    private data class Collectible(var x: Float, var y: Float, val symbol: String,
                                   val name: String, val value: Int, val color: Int)
    private data class NeonSign(val x: Float, val y: Float, val label: String, val color: Int)
}
