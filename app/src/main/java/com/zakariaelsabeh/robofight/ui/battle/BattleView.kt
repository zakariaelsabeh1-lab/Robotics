package com.zakariaelsabeh.robofight.ui.battle

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.zakariaelsabeh.robofight.data.models.Robot
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class BattleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var playerRobot: Robot? = null
    var enemyRobot: Robot? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animTick = 0f
    private val particles = mutableListOf<Particle>()
    private val shockwaves = mutableListOf<Shockwave>()

    private var playerShake = 0f
    private var enemyShake = 0f
    private var playerScale = 1f
    private var enemyScale = 1f
    private var flashColor: Int = Color.TRANSPARENT
    private var flashAlpha = 0

    private var backgroundAnimator: ValueAnimator? = null
    private var bgPhase = 0f

    init { startBackgroundAnimation() }

    private fun startBackgroundAnimation() {
        backgroundAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 6000; repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                bgPhase = it.animatedValue as Float
                animTick += 0.04f
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    fun animatePlayerAttack(onHit: () -> Unit) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            addUpdateListener { v ->
                val t = v.animatedFraction
                playerShake = if (t < 0.5f) t * 65f else 0f
                if (t > 0.45f && t < 0.55f) {
                    val hx = width * 0.7f; val hy = height * 0.35f
                    spawnImpactParticles(hx, hy, Color.parseColor("#FF6600"))
                    spawnShockwave(hx, hy, Color.parseColor("#FF6600"))
                    onHit()
                }
                invalidate()
            }
            start()
        }
    }

    fun animateEnemyAttack(onHit: () -> Unit) {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            addUpdateListener { v ->
                val t = v.animatedFraction
                enemyShake = if (t < 0.5f) -t * 55f else 0f
                if (t > 0.45f && t < 0.55f) {
                    val hx = width * 0.3f; val hy = height * 0.5f
                    spawnImpactParticles(hx, hy, Color.parseColor("#FF0033"))
                    spawnShockwave(hx, hy, Color.parseColor("#FF0033"))
                    onHit()
                }
                invalidate()
            }
            start()
        }
    }

    fun animateSpecialAttack(isPlayer: Boolean, color: Int, onHit: () -> Unit) {
        flashColor = color
        ValueAnimator.ofInt(0, 160, 0).apply {
            duration = 600
            addUpdateListener { v ->
                flashAlpha = v.animatedValue as Int
                val t = v.animatedFraction
                if (isPlayer) playerShake = sin(t * Math.PI * 4).toFloat() * 22f
                else enemyShake = sin(t * Math.PI * 4).toFloat() * 22f
                if (t > 0.4f && t < 0.6f) {
                    val hx = if (isPlayer) width * 0.7f else width * 0.3f
                    val hy = height * 0.4f
                    spawnSpecialParticles(hx, hy, color)
                    spawnShockwave(hx, hy, color)
                    onHit()
                }
                invalidate()
            }
            start()
        }
    }

    fun animatePlayerHit() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350
            addUpdateListener { v ->
                playerShake = sin(v.animatedFraction * Math.PI * 8).toFloat() * 18f
                invalidate()
            }
            start()
        }
    }

    fun animateEnemyHit() {
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350
            addUpdateListener { v ->
                enemyShake = sin(v.animatedFraction * Math.PI * 8).toFloat() * 18f
                invalidate()
            }
            start()
        }
    }

    fun animateVictory() {
        ValueAnimator.ofFloat(1f, 1.5f, 1f).apply {
            duration = 700; repeatCount = 3
            interpolator = OvershootInterpolator()
            addUpdateListener { v ->
                playerScale = v.animatedValue as Float
                spawnStarParticles(width * 0.3f, height * 0.5f)
                invalidate()
            }
            start()
        }
    }

    private fun spawnShockwave(x: Float, y: Float, color: Int) {
        shockwaves.add(Shockwave(x, y, 10f, 1f, color))
    }

    private fun spawnImpactParticles(x: Float, y: Float, color: Int) {
        repeat(14) {
            particles.add(Particle(
                x + Random.nextFloat() * 40 - 20, y + Random.nextFloat() * 40 - 20,
                (Random.nextFloat() - 0.5f) * 10f, (Random.nextFloat() - 0.8f) * 10f,
                color, Random.nextFloat() * 12 + 5, 1f, ParticleType.IMPACT
            ))
        }
    }

    private fun spawnSpecialParticles(x: Float, y: Float, color: Int) {
        repeat(24) {
            val angle = Random.nextFloat() * Math.PI * 2
            val speed = Random.nextFloat() * 14 + 4
            particles.add(Particle(
                x, y, (cos(angle) * speed).toFloat(), (sin(angle) * speed).toFloat(),
                color, Random.nextFloat() * 16 + 6, 1f, ParticleType.SPECIAL
            ))
        }
    }

    private fun spawnStarParticles(x: Float, y: Float) {
        if (Random.nextFloat() > 0.3f) return
        particles.add(Particle(
            Random.nextFloat() * width, Random.nextFloat() * height * 0.6f,
            (Random.nextFloat() - 0.5f) * 4f, -Random.nextFloat() * 6 - 2,
            Color.parseColor("#FFD700"), Random.nextFloat() * 12 + 8, 1f, ParticleType.STAR
        ))
    }

    private fun updateParticles() {
        val pi = particles.iterator()
        while (pi.hasNext()) {
            val p = pi.next(); p.x += p.vx; p.y += p.vy; p.vy += 0.3f; p.life -= 0.025f
            if (p.life <= 0) pi.remove()
        }
        val si = shockwaves.iterator()
        while (si.hasNext()) {
            val s = si.next(); s.radius += 10f; s.life -= 0.07f
            if (s.life <= 0) si.remove()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat(); val h = height.toFloat()
        drawCitySkyBackground(canvas, w, h)
        drawArenaFloor(canvas, w, h)

        playerRobot?.let { robot ->
            val cx = w * 0.27f; val cy = h * 0.56f
            canvas.save(); canvas.scale(playerScale, playerScale, cx, cy)
            RobotRenderer.drawRobot(canvas, robot, cx, cy, scale = 1.15f,
                mirrored = false, shake = playerShake)
            canvas.restore()
            RobotRenderer.drawHealthBar(canvas, cx, h * 0.8f, w * 0.38f,
                robot.hpPercent(), robot.displayColor(), robot.name)
        }

        enemyRobot?.let { robot ->
            val cx = w * 0.73f; val cy = h * 0.46f
            canvas.save(); canvas.scale(enemyScale, enemyScale, cx, cy)
            RobotRenderer.drawRobot(canvas, robot, cx, cy, scale = 1.0f,
                mirrored = true, shake = enemyShake)
            canvas.restore()
            RobotRenderer.drawHealthBar(canvas, cx, h * 0.74f, w * 0.38f,
                robot.hpPercent(), robot.displayColor(), robot.name)
        }

        drawParticles(canvas)

        if (flashAlpha > 0) {
            paint.color = Color.argb(flashAlpha, Color.red(flashColor),
                Color.green(flashColor), Color.blue(flashColor))
            canvas.drawRect(0f, 0f, w, h, paint)
        }
    }

    private fun drawCitySkyBackground(canvas: Canvas, w: Float, h: Float) {
        // Night sky gradient
        val skyGrad = LinearGradient(0f, 0f, 0f, h * 0.65f,
            Color.parseColor("#020208"), Color.parseColor("#0A0A1E"), Shader.TileMode.CLAMP)
        paint.shader = skyGrad; canvas.drawRect(0f, 0f, w, h, paint); paint.shader = null

        // Stars
        val sRng = Random(42)
        repeat(80) {
            val sx = sRng.nextFloat() * w; val sy = sRng.nextFloat() * h * 0.55f
            val br = 0.3f + 0.7f * abs(sin((animTick * 0.8f + it * 0.7f).toDouble())).toFloat()
            paint.color = Color.WHITE; paint.alpha = (br * 220).toInt()
            canvas.drawCircle(sx, sy, 0.8f + sRng.nextFloat() * 2f, paint)
        }
        paint.alpha = 255

        val horizonY = h * 0.62f

        // Far buildings (darkest silhouette)
        paint.color = Color.parseColor("#060612")
        val farB = listOf(
            floatArrayOf(0f, 55f, 110f), floatArrayOf(45f, 35f, 150f), floatArrayOf(72f, 70f, 90f),
            floatArrayOf(135f, 45f, 170f), floatArrayOf(172f, 65f, 120f), floatArrayOf(230f, 40f, 190f),
            floatArrayOf(262f, 55f, 105f), floatArrayOf(310f, 75f, 140f),
            floatArrayOf(w - 300f, 65f, 160f), floatArrayOf(w - 240f, 50f, 130f),
            floatArrayOf(w - 190f, 85f, 180f), floatArrayOf(w - 115f, 55f, 120f),
            floatArrayOf(w - 68f, 45f, 150f), floatArrayOf(w - 38f, 42f, 95f)
        )
        for (b in farB) canvas.drawRect(b[0], horizonY - b[2], b[0] + b[1], horizonY, paint)

        // Mid buildings with lit windows
        val midB = listOf(
            floatArrayOf(0f, 75f, 85f), floatArrayOf(65f, 55f, 130f), floatArrayOf(112f, 90f, 75f),
            floatArrayOf(195f, 65f, 110f), floatArrayOf(252f, 50f, 150f),
            floatArrayOf(w - 320f, 60f, 100f), floatArrayOf(w - 265f, 85f, 135f),
            floatArrayOf(w - 185f, 65f, 90f), floatArrayOf(w - 118f, 75f, 122f), floatArrayOf(w - 52f, 55f, 80f)
        )
        paint.color = Color.parseColor("#0C0C1C")
        for (b in midB) {
            canvas.drawRect(b[0], horizonY - b[2], b[0] + b[1], horizonY, paint)
            val rows = (b[2] / 20).toInt().coerceAtMost(6)
            val cols = (b[1] / 18).toInt().coerceAtMost(4)
            for (r in 1..rows) {
                for (col in 0 until cols) {
                    val wx = b[0] + 4 + col * 18f; val wy = horizonY - b[2] + r * 18f
                    if (wy + 12 < horizonY - 4) {
                        val lit = ((r + col + (animTick * 0.06f).toInt()) % 5) != 0
                        paint.color = if (lit) Color.argb(110, 255, 255, 180)
                        else Color.argb(25, 60, 60, 120)
                        canvas.drawRect(wx, wy, wx + 10f, wy + 12f, paint)
                    }
                }
            }
        }

        // Neon signs on buildings
        val neonCols = listOf(
            Color.parseColor("#FF0044"), Color.parseColor("#00FFCC"),
            Color.parseColor("#FF6600"), Color.parseColor("#CC00FF"), Color.parseColor("#00AAFF")
        )
        val nRng = Random(77)
        repeat(8) {
            val nx = nRng.nextFloat() * w; val ny = horizonY - 38f - nRng.nextFloat() * 90f
            val nc = neonCols[it % neonCols.size]
            val pulse = abs(sin((animTick * 0.5f + it).toDouble())).toFloat()
            paint.color = Color.argb((60 + (80 * pulse).toInt()),
                Color.red(nc), Color.green(nc), Color.blue(nc))
            canvas.drawRect(nx - 18f, ny - 5f, nx + 18f, ny + 5f, paint)
            paint.color = nc; paint.style = Paint.Style.STROKE; paint.strokeWidth = 1f
            canvas.drawRect(nx - 18f, ny - 5f, nx + 18f, ny + 5f, paint)
            paint.style = Paint.Style.FILL
        }
        paint.alpha = 255

        // Fog/haze at horizon
        val fogGrad = LinearGradient(0f, horizonY - 30f, 0f, horizonY + 20f,
            Color.argb(0, 20, 20, 60), Color.argb(90, 10, 10, 40), Shader.TileMode.CLAMP)
        paint.shader = fogGrad
        canvas.drawRect(0f, horizonY - 30f, w, horizonY + 20f, paint)
        paint.shader = null
    }

    private fun drawArenaFloor(canvas: Canvas, w: Float, h: Float) {
        val floorY = h * 0.62f
        // Dark concrete floor
        val floorGrad = LinearGradient(0f, floorY, 0f, h,
            Color.parseColor("#111118"), Color.parseColor("#070710"), Shader.TileMode.CLAMP)
        paint.shader = floorGrad; canvas.drawRect(0f, floorY, w, h, paint); paint.shader = null

        // Perspective grid lines
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 1f
        for (i in 0..8) {
            val t = i / 8f; val lineY = floorY + t * (h - floorY)
            val alpha = (12 + (t * 35).toInt())
            paint.color = Color.argb(alpha, 80, 100, 255)
            canvas.drawLine(0f, lineY, w, lineY, paint)
        }
        val vpX = w / 2
        for (i in -8..8) {
            val endX = vpX + i * w / 7
            val alpha = (18 - Math.abs(i) * 2).coerceAtLeast(2)
            paint.color = Color.argb(alpha, 80, 100, 255)
            canvas.drawLine(vpX + i * 15f, floorY, endX, h, paint)
        }
        paint.style = Paint.Style.FILL

        // Arena energy oval
        paint.color = Color.argb(35, 100, 130, 255)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2.5f
        canvas.drawOval(w * 0.06f, h * 0.72f, w * 0.94f, h * 0.9f, paint)
        // Rotating energy dots on the oval
        for (i in 0..7) {
            val angle = Math.toRadians((bgPhase.toDouble() + i * 45.0))
            val rx = w * 0.44f; val ry = h * 0.09f
            val px = (vpX + rx * cos(angle)).toFloat()
            val py = (h * 0.81f + ry * sin(angle)).toFloat()
            paint.style = Paint.Style.FILL
            paint.color = Color.argb(120, 100, 150, 255)
            canvas.drawCircle(px, py, 4.5f, paint)
        }
        paint.style = Paint.Style.FILL
    }

    private fun drawParticles(canvas: Canvas) {
        for (s in shockwaves) {
            paint.alpha = (s.life * 200).toInt()
            paint.color = s.color
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 4f * s.life
            canvas.drawCircle(s.x, s.y, s.radius, paint)
            paint.style = Paint.Style.FILL
        }
        for (p in particles) {
            paint.alpha = (p.life * 255).toInt(); paint.color = p.color
            when (p.type) {
                ParticleType.IMPACT -> canvas.drawCircle(p.x, p.y, p.size * p.life, paint)
                ParticleType.SPECIAL -> {
                    canvas.drawCircle(p.x, p.y, p.size * p.life, paint)
                    paint.color = Color.WHITE; paint.alpha = (p.life * 140).toInt()
                    canvas.drawCircle(p.x, p.y, p.size * p.life * 0.35f, paint)
                }
                ParticleType.STAR -> {
                    val path = Path(); val s = p.size * p.life
                    path.moveTo(p.x, p.y - s)
                    for (i in 1..10) {
                        val r = if (i % 2 == 0) s else s * 0.4f
                        val a = (i * 36 - 90) * Math.PI / 180
                        path.lineTo((p.x + r * cos(a)).toFloat(), (p.y + r * sin(a)).toFloat())
                    }
                    path.close(); canvas.drawPath(path, paint)
                }
            }
        }
        paint.alpha = 255
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        backgroundAnimator?.cancel()
    }

    private data class Particle(
        var x: Float, var y: Float, var vx: Float, var vy: Float,
        val color: Int, val size: Float, var life: Float, val type: ParticleType
    )
    private data class Shockwave(var x: Float, var y: Float, var radius: Float, var life: Float, val color: Int)
    private enum class ParticleType { IMPACT, SPECIAL, STAR }
}
