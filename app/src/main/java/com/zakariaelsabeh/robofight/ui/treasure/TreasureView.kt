package com.zakariaelsabeh.robofight.ui.treasure

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*
import kotlin.random.Random

class TreasureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var onTreasureFound: ((String, Int) -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val chests = mutableListOf<TreasureChest>()
    private val particles = mutableListOf<TreasureParticle>()
    private var animTick = 0f
    private var frameAnimator: ValueAnimator? = null
    private val rng = Random(System.currentTimeMillis())

    private val treasureNames = listOf(
        "Golden Gear" to 80 to Color.parseColor("#FFD700"),
        "Crystal Heart" to 100 to Color.parseColor("#FF69B4"),
        "Power Battery" to 70 to Color.parseColor("#00FF7F"),
        "Ancient Map" to 120 to Color.parseColor("#DEB887"),
        "Robot Crown" to 200 to Color.parseColor("#FFD700"),
        "Star Fragment" to 90 to Color.parseColor("#FFFACD")
    )

    init {
        post {
            generateChests()
            startLoop()
        }
    }

    fun refreshChests() {
        chests.clear()
        generateChests()
    }

    private fun generateChests() {
        val w = width.toFloat()
        val h = height.toFloat()
        chests.clear()
        val positions = listOf(
            w * 0.2f to h * 0.3f,
            w * 0.5f to h * 0.2f,
            w * 0.8f to h * 0.35f,
            w * 0.15f to h * 0.6f,
            w * 0.5f to h * 0.55f,
            w * 0.85f to h * 0.65f
        )
        positions.shuffled().take(rng.nextInt(3) + 3).forEachIndexed { i, (cx, cy) ->
            val idx = rng.nextInt(treasureNames.size)
            val (pair, color) = treasureNames[idx]
            val (name, value) = pair
            chests.add(TreasureChest(cx, cy, name, value, color, rng.nextFloat() * 0.3f))
        }
    }

    private fun startLoop() {
        frameAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 16
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                animTick += 0.04f
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val tx = event.x; val ty = event.y
            val it = chests.iterator()
            while (it.hasNext()) {
                val chest = it.next()
                if (!chest.opened) {
                    val dx = tx - chest.x; val dy = ty - chest.y
                    if (sqrt(dx * dx + dy * dy) < 55f) {
                        openChest(chest)
                        it.remove()
                        return true
                    }
                }
            }
        }
        return true
    }

    private fun openChest(chest: TreasureChest) {
        repeat(25) {
            val angle = rng.nextFloat() * Math.PI * 2
            val speed = rng.nextFloat() * 10 + 3
            particles.add(TreasureParticle(
                chest.x, chest.y,
                (cos(angle) * speed).toFloat(), (sin(angle) * speed).toFloat() - 5f,
                chest.color, rng.nextFloat() * 14 + 6, 1f
            ))
        }
        onTreasureFound?.invoke(chest.name, chest.value)
    }

    private fun updateParticles() {
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.x += p.vx; p.y += p.vy; p.vy += 0.35f; p.life -= 0.025f
            if (p.life <= 0) it.remove()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat(); val h = height.toFloat()
        drawBackground(canvas, w, h)
        drawChests(canvas)
        drawParticles(canvas)
        if (chests.isEmpty()) drawSuccessMessage(canvas, w, h)
        else drawHint(canvas, w, h)
    }

    private fun drawBackground(canvas: Canvas, w: Float, h: Float) {
        val bg = RadialGradient(w / 2, h / 2, maxOf(w, h) * 0.8f,
            Color.parseColor("#2D1B00"), Color.parseColor("#1A0D00"), Shader.TileMode.CLAMP)
        paint.shader = bg
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        // Cave stalactites
        paint.color = Color.parseColor("#1A0D00")
        for (i in 0..7) {
            val x = (i + 0.5f) * w / 8
            val h2 = 40f + rng.nextInt(60).toFloat() * abs(sin(i.toDouble())).toFloat()
            val path = Path().apply {
                moveTo(x - 20f, 0f); lineTo(x + 20f, 0f); lineTo(x, h2); close()
            }
            canvas.drawPath(path, paint)
        }

        // Glowing floor
        paint.color = Color.argb(60, 139, 90, 43)
        canvas.drawRect(0f, h * 0.85f, w, h, paint)

        // Sparkle dots on floor
        paint.color = Color.argb(100, 255, 215, 0)
        val dotRng = Random(77)
        repeat(30) {
            val dx = dotRng.nextFloat() * w
            val dy = h * 0.85f + dotRng.nextFloat() * h * 0.15f
            val glow = abs(sin((animTick + it * 0.4f).toDouble())).toFloat()
            paint.alpha = (glow * 150).toInt()
            canvas.drawCircle(dx, dy, 2f + dotRng.nextFloat() * 3f, paint)
        }
        paint.alpha = 255
    }

    private fun drawChests(canvas: Canvas) {
        chests.forEach { chest ->
            val bob = sin((animTick + chest.phase) * 2.0).toFloat() * 5f
            val cx = chest.x; val cy = chest.y + bob

            // Glow
            paint.color = Color.argb(60, Color.red(chest.color), Color.green(chest.color), Color.blue(chest.color))
            canvas.drawCircle(cx, cy, 60f, paint)

            // Shadow
            paint.color = Color.argb(80, 0, 0, 0)
            canvas.drawOval(cx - 38f, cy + 34f, cx + 38f, cy + 44f, paint)

            // Chest body
            paint.color = Color.parseColor("#8B4513")
            canvas.drawRoundRect(cx - 35f, cy - 5f, cx + 35f, cy + 35f, 6f, 6f, paint)

            // Metal bands
            paint.color = Color.parseColor("#DAA520")
            canvas.drawRect(cx - 35f, cy + 10f, cx + 35f, cy + 16f, paint)
            canvas.drawRect(cx - 4f, cy - 5f, cx + 4f, cy + 35f, paint)

            // Lock
            paint.color = Color.parseColor("#FFD700")
            canvas.drawCircle(cx, cy + 13f, 10f, paint)
            paint.color = Color.parseColor("#8B6914")
            canvas.drawCircle(cx, cy + 13f, 6f, paint)

            // Chest lid
            paint.color = Color.parseColor("#A0522D")
            canvas.drawRoundRect(cx - 35f, cy - 30f, cx + 35f, cy + 2f, 6f, 6f, paint)
            // Lid highlight
            paint.color = Color.parseColor("#CD853F")
            canvas.drawRoundRect(cx - 30f, cy - 27f, cx + 30f, cy - 18f, 4f, 4f, paint)

            // Sparkles around chest
            paint.color = chest.color
            for (i in 0..4) {
                val ang = animTick + i * Math.PI * 0.4f
                val sx = (cx + 50 * cos(ang)).toFloat()
                val sy = (cy + 50 * sin(ang)).toFloat()
                val sz = 3f + 2f * abs(sin((animTick * 3 + i).toDouble())).toFloat()
                paint.alpha = (abs(sin((animTick + i).toDouble())) * 200).toInt()
                canvas.drawCircle(sx, sy, sz, paint)
            }
            paint.alpha = 255

            // Question mark
            paint.color = Color.WHITE
            paint.textSize = 30f
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("?", cx, cy - 8f, paint)
        }
    }

    private fun drawParticles(canvas: Canvas) {
        particles.forEach { p ->
            paint.alpha = (p.life * 255).toInt()
            paint.color = p.color
            canvas.drawCircle(p.x, p.y, p.size * p.life, paint)
            paint.color = Color.WHITE
            paint.alpha = (p.life * 150).toInt()
            canvas.drawCircle(p.x, p.y, p.size * p.life * 0.3f, paint)
        }
        paint.alpha = 255
    }

    private fun drawSuccessMessage(canvas: Canvas, w: Float, h: Float) {
        paint.color = Color.parseColor("#FFD700")
        paint.textSize = 36f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("🎉 All treasures found!", w / 2, h / 2, paint)
    }

    private fun drawHint(canvas: Canvas, w: Float, h: Float) {
        paint.color = Color.argb(200, 255, 215, 0)
        paint.textSize = 22f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Tap the glowing chests to open them!", w / 2, h * 0.96f, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        frameAnimator?.cancel()
    }

    private data class TreasureChest(var x: Float, var y: Float, val name: String,
                                     val value: Int, val color: Int, val phase: Float,
                                     var opened: Boolean = false)
    private data class TreasureParticle(var x: Float, var y: Float, var vx: Float, var vy: Float,
                                        val color: Int, val size: Float, var life: Float)
}
