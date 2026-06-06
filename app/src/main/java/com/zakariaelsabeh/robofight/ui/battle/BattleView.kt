package com.zakariaelsabeh.robofight.ui.battle

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import com.zakariaelsabeh.robofight.data.models.Robot
import kotlin.math.abs
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

    private var playerShake = 0f
    private var enemyShake = 0f
    private var playerScale = 1f
    private var enemyScale = 1f
    private var flashColor: Int = Color.TRANSPARENT
    private var flashAlpha = 0

    private var backgroundAnimator: ValueAnimator? = null
    private var bgPhase = 0f

    init {
        startBackgroundAnimation()
    }

    private fun startBackgroundAnimation() {
        backgroundAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 6000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                bgPhase = it.animatedValue as Float
                animTick += 0.05f
                updateParticles()
                invalidate()
            }
            start()
        }
    }

    fun animatePlayerAttack(onHit: () -> Unit) {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            addUpdateListener { v ->
                val t = v.animatedFraction
                playerShake = if (t < 0.5f) t * 60f else 0f
                if (t > 0.45f && t < 0.55f) {
                    spawnImpactParticles(width * 0.7f, height * 0.35f, Color.parseColor("#FF6600"))
                    onHit()
                }
                invalidate()
            }
        }
        anim.start()
    }

    fun animateEnemyAttack(onHit: () -> Unit) {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 400
            addUpdateListener { v ->
                val t = v.animatedFraction
                enemyShake = if (t < 0.5f) -t * 50f else 0f
                if (t > 0.45f && t < 0.55f) {
                    spawnImpactParticles(width * 0.3f, height * 0.55f, Color.parseColor("#FF0000"))
                    onHit()
                }
                invalidate()
            }
        }
        anim.start()
    }

    fun animateSpecialAttack(isPlayer: Boolean, color: Int, onHit: () -> Unit) {
        flashColor = color
        val anim = ValueAnimator.ofInt(0, 180, 0).apply {
            duration = 600
            addUpdateListener { v ->
                flashAlpha = v.animatedValue as Int
                val t = v.animatedFraction
                if (isPlayer) {
                    playerShake = sin(t * Math.PI * 4).toFloat() * 20f
                } else {
                    enemyShake = sin(t * Math.PI * 4).toFloat() * 20f
                }
                if (t > 0.4f && t < 0.6f) {
                    spawnSpecialParticles(if (isPlayer) width * 0.7f else width * 0.3f, height * 0.4f, color)
                    onHit()
                }
                invalidate()
            }
        }
        anim.start()
    }

    fun animatePlayerHit() {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350
            addUpdateListener { v ->
                val t = v.animatedFraction
                playerShake = sin(t * Math.PI * 8).toFloat() * 15f
                invalidate()
            }
        }
        anim.start()
    }

    fun animateEnemyHit() {
        val anim = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 350
            addUpdateListener { v ->
                val t = v.animatedFraction
                enemyShake = sin(t * Math.PI * 8).toFloat() * 15f
                invalidate()
            }
        }
        anim.start()
    }

    fun animateVictory() {
        val anim = ValueAnimator.ofFloat(1f, 1.4f, 1f).apply {
            duration = 800
            repeatCount = 3
            interpolator = OvershootInterpolator()
            addUpdateListener { v ->
                playerScale = v.animatedValue as Float
                spawnStarParticles(width * 0.3f, height * 0.5f)
                invalidate()
            }
        }
        anim.start()
    }

    private fun spawnImpactParticles(x: Float, y: Float, color: Int) {
        repeat(12) {
            particles.add(Particle(
                x = x + Random.nextFloat() * 40 - 20,
                y = y + Random.nextFloat() * 40 - 20,
                vx = (Random.nextFloat() - 0.5f) * 8,
                vy = (Random.nextFloat() - 0.8f) * 8,
                color = color,
                size = Random.nextFloat() * 10 + 5,
                life = 1f,
                type = ParticleType.IMPACT
            ))
        }
    }

    private fun spawnSpecialParticles(x: Float, y: Float, color: Int) {
        repeat(20) {
            val angle = Random.nextFloat() * Math.PI * 2
            val speed = Random.nextFloat() * 12 + 4
            particles.add(Particle(
                x = x, y = y,
                vx = (Math.cos(angle) * speed).toFloat(),
                vy = (Math.sin(angle) * speed).toFloat(),
                color = color,
                size = Random.nextFloat() * 14 + 6,
                life = 1f,
                type = ParticleType.SPECIAL
            ))
        }
    }

    private fun spawnStarParticles(x: Float, y: Float) {
        if (Random.nextFloat() > 0.3f) return
        particles.add(Particle(
            x = Random.nextFloat() * width,
            y = Random.nextFloat() * height * 0.6f,
            vx = (Random.nextFloat() - 0.5f) * 4,
            vy = -Random.nextFloat() * 6 - 2,
            color = Color.parseColor("#FFD700"),
            size = Random.nextFloat() * 12 + 8,
            life = 1f,
            type = ParticleType.STAR
        ))
    }

    private fun updateParticles() {
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.x += p.vx
            p.y += p.vy
            p.vy += 0.3f
            p.life -= 0.025f
            if (p.life <= 0) it.remove()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        drawArenaBackground(canvas, w, h)
        drawFloor(canvas, w, h)

        playerRobot?.let { robot ->
            val cx = w * 0.28f
            val cy = h * 0.55f
            RobotRenderer.drawRobot(canvas, robot, cx, cy, scale = 1.15f,
                mirrored = false, shake = playerShake)
            RobotRenderer.drawHealthBar(canvas, cx, h * 0.78f, w * 0.35f,
                robot.hpPercent(), robot.displayColor(), robot.name)
        }

        enemyRobot?.let { robot ->
            val cx = w * 0.72f
            val cy = h * 0.45f
            RobotRenderer.drawRobot(canvas, robot, cx, cy, scale = 1.0f,
                mirrored = true, shake = enemyShake)
            RobotRenderer.drawHealthBar(canvas, cx, h * 0.72f, w * 0.35f,
                robot.hpPercent(), robot.displayColor(), robot.name)
        }

        drawParticles(canvas)

        if (flashAlpha > 0) {
            paint.color = Color.argb(flashAlpha, Color.red(flashColor),
                Color.green(flashColor), Color.blue(flashColor))
            canvas.drawRect(0f, 0f, w, h, paint)
        }
    }

    private fun drawArenaBackground(canvas: Canvas, w: Float, h: Float) {
        // Deep space gradient
        val gradient = RadialGradient(
            w / 2, h / 2, maxOf(w, h) * 0.8f,
            Color.parseColor("#1A0A2E"),
            Color.parseColor("#0D0D1A"),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        // Animated stars
        paint.color = Color.WHITE
        val starRng = Random(42)
        repeat(60) {
            val sx = starRng.nextFloat() * w
            val sy = starRng.nextFloat() * h * 0.7f
            val brightness = 0.3f + 0.7f * abs(sin((animTick + it * 0.7f).toDouble())).toFloat()
            paint.alpha = (brightness * 200).toInt()
            canvas.drawCircle(sx, sy, 1.5f + starRng.nextFloat() * 2f, paint)
        }
        paint.alpha = 255

        // Arena energy ring
        paint.color = Color.argb(40, 100, 100, 255)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawOval(w * 0.05f, h * 0.3f, w * 0.95f, h * 0.85f, paint)
        paint.style = Paint.Style.FILL
    }

    private fun drawFloor(canvas: Canvas, w: Float, h: Float) {
        val floorGrad = LinearGradient(
            0f, h * 0.72f, 0f, h,
            Color.parseColor("#1A2040"),
            Color.parseColor("#0D0D1A"),
            Shader.TileMode.CLAMP
        )
        paint.shader = floorGrad
        canvas.drawRect(0f, h * 0.72f, w, h, paint)
        paint.shader = null

        // Grid lines
        paint.color = Color.argb(30, 100, 150, 255)
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        for (i in 0..8) {
            canvas.drawLine(i * w / 8, h * 0.72f, i * w / 8, h, paint)
        }
        for (i in 0..4) {
            canvas.drawLine(0f, h * 0.72f + i * h * 0.07f, w, h * 0.72f + i * h * 0.07f, paint)
        }
        paint.style = Paint.Style.FILL
    }

    private fun drawParticles(canvas: Canvas) {
        for (p in particles) {
            paint.alpha = (p.life * 255).toInt()
            paint.color = p.color
            when (p.type) {
                ParticleType.IMPACT -> canvas.drawCircle(p.x, p.y, p.size * p.life, paint)
                ParticleType.SPECIAL -> {
                    paint.color = p.color
                    canvas.drawCircle(p.x, p.y, p.size * p.life, paint)
                    paint.color = Color.WHITE
                    paint.alpha = (p.life * 150).toInt()
                    canvas.drawCircle(p.x, p.y, p.size * p.life * 0.4f, paint)
                }
                ParticleType.STAR -> {
                    val path = Path()
                    val s = p.size * p.life
                    path.moveTo(p.x, p.y - s)
                    for (i in 1..10) {
                        val r = if (i % 2 == 0) s else s * 0.4f
                        val a = (i * 36 - 90) * Math.PI / 180
                        path.lineTo((p.x + r * Math.cos(a)).toFloat(), (p.y + r * Math.sin(a)).toFloat())
                    }
                    path.close()
                    canvas.drawPath(path, paint)
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
        var x: Float, var y: Float,
        var vx: Float, var vy: Float,
        val color: Int, val size: Float,
        var life: Float, val type: ParticleType
    )

    private enum class ParticleType { IMPACT, SPECIAL, STAR }
}
