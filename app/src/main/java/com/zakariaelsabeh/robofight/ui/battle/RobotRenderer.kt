package com.zakariaelsabeh.robofight.ui.battle

import android.graphics.*
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.data.models.RobotType
import kotlin.math.cos
import kotlin.math.sin

object RobotRenderer {

    fun drawRobot(
        canvas: Canvas,
        robot: Robot,
        cx: Float, cy: Float,
        scale: Float = 1f,
        mirrored: Boolean = false,
        shake: Float = 0f,
        alpha: Int = 255
    ) {
        canvas.save()
        canvas.translate(cx + shake, cy)
        if (mirrored) canvas.scale(-1f, 1f)
        canvas.scale(scale, scale)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.alpha = alpha }
        val primary = robot.displayColor()
        val secondary = robot.secondaryColor
        val accent = robot.accentColor

        when (robot.robotType) {
            RobotType.FIRE -> drawFireRobot(canvas, paint, primary, secondary, accent)
            RobotType.THUNDER -> drawThunderRobot(canvas, paint, primary, secondary, accent)
            RobotType.CRUSHER -> drawCrusherRobot(canvas, paint, primary, secondary, accent)
            RobotType.SPARK -> drawSparkRobot(canvas, paint, primary, secondary, accent)
            RobotType.ROCKET -> drawRocketRobot(canvas, paint, primary, secondary, accent)
            RobotType.NOVA -> drawNovaRobot(canvas, paint, primary, secondary, accent)
        }

        canvas.restore()
    }

    private fun drawFireRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Legs
        p.color = secondary
        c.drawRoundRect(-30f, 60f, -5f, 100f, 8f, 8f, p)
        c.drawRoundRect(5f, 60f, 30f, 100f, 8f, 8f, p)
        // Feet
        p.color = accent
        c.drawRoundRect(-35f, 90f, -3f, 105f, 6f, 6f, p)
        c.drawRoundRect(3f, 90f, 35f, 105f, 6f, 6f, p)
        // Body
        p.color = primary
        c.drawRoundRect(-40f, 10f, 40f, 65f, 12f, 12f, p)
        // Chest flame emblem
        p.color = accent
        drawFlame(c, p, 0f, 37f, 14f)
        // Arms
        p.color = secondary
        c.drawRoundRect(-60f, 15f, -38f, 55f, 10f, 10f, p)
        c.drawRoundRect(38f, 15f, 60f, 55f, 10f, 10f, p)
        // Fists
        p.color = primary
        c.drawCircle(-55f, 58f, 12f, p)
        c.drawCircle(55f, 58f, 12f, p)
        p.color = accent
        c.drawCircle(-55f, 58f, 6f, p)
        c.drawCircle(55f, 58f, 6f, p)
        // Neck
        p.color = secondary
        c.drawRoundRect(-12f, -5f, 12f, 12f, 5f, 5f, p)
        // Head
        p.color = primary
        c.drawRoundRect(-38f, -48f, 38f, 8f, 14f, 14f, p)
        // Visor
        p.color = Color.parseColor("#1A1A2E")
        c.drawRoundRect(-30f, -40f, 30f, -15f, 8f, 8f, p)
        // Eyes glow
        p.color = accent
        c.drawCircle(-14f, -27f, 8f, p)
        c.drawCircle(14f, -27f, 8f, p)
        p.color = Color.WHITE
        c.drawCircle(-11f, -29f, 3f, p)
        c.drawCircle(17f, -29f, 3f, p)
        // Head spikes (flame)
        p.color = accent
        drawFlame(c, p, 0f, -52f, 10f)
        drawFlame(c, p, -20f, -50f, 7f)
        drawFlame(c, p, 20f, -50f, 7f)
        // Mouth
        p.color = Color.parseColor("#FF3300")
        c.drawRoundRect(-15f, -12f, 15f, -6f, 4f, 4f, p)
    }

    private fun drawFlame(c: Canvas, p: Paint, x: Float, y: Float, size: Float) {
        val path = Path().apply {
            moveTo(x, y - size * 2)
            cubicTo(x + size, y - size, x + size * 0.5f, y, x, y + size * 0.3f)
            cubicTo(x - size * 0.5f, y, x - size, y - size, x, y - size * 2)
            close()
        }
        c.drawPath(path, p)
    }

    private fun drawThunderRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Legs (slim, fast)
        p.color = secondary
        c.drawRoundRect(-25f, 60f, -5f, 95f, 6f, 6f, p)
        c.drawRoundRect(5f, 60f, 25f, 95f, 6f, 6f, p)
        // Aerodynamic feet
        p.color = accent
        c.drawRoundRect(-32f, 90f, 0f, 100f, 5f, 5f, p)
        c.drawRoundRect(0f, 90f, 32f, 100f, 5f, 5f, p)
        // Body (sleek)
        p.color = primary
        c.drawRoundRect(-35f, 10f, 35f, 62f, 10f, 10f, p)
        // Lightning bolt on chest
        p.color = accent
        drawLightning(c, p, 0f, 36f, 18f)
        // Arms (thin, fast)
        p.color = secondary
        c.drawRoundRect(-55f, 12f, -33f, 48f, 8f, 8f, p)
        c.drawRoundRect(33f, 12f, 55f, 48f, 8f, 8f, p)
        // Electric hands
        p.color = accent
        c.drawCircle(-50f, 52f, 10f, p)
        c.drawCircle(50f, 52f, 10f, p)
        p.color = Color.WHITE
        c.drawCircle(-50f, 52f, 4f, p)
        c.drawCircle(50f, 52f, 4f, p)
        // Neck
        p.color = secondary
        c.drawRoundRect(-10f, -4f, 10f, 12f, 4f, 4f, p)
        // Head (aerodynamic)
        p.color = primary
        c.drawRoundRect(-32f, -44f, 32f, 8f, 10f, 10f, p)
        // Visor
        p.color = Color.parseColor("#0D0D1A")
        c.drawRoundRect(-26f, -38f, 26f, -14f, 7f, 7f, p)
        // Glowing eyes
        p.color = accent
        c.drawCircle(-12f, -26f, 7f, p)
        c.drawCircle(12f, -26f, 7f, p)
        p.color = Color.WHITE
        c.drawCircle(-10f, -28f, 3f, p)
        c.drawCircle(14f, -28f, 3f, p)
        // Antenna
        p.color = accent
        c.drawLine(0f, -44f, 0f, -62f, p.also { it.strokeWidth = 3f; it.style = Paint.Style.STROKE })
        c.drawCircle(0f, -65f, 5f, p.also { it.style = Paint.Style.FILL })
        // Mouth
        p.color = accent
        c.drawRoundRect(-12f, -10f, 12f, -5f, 3f, 3f, p)
    }

    private fun drawLightning(c: Canvas, p: Paint, x: Float, y: Float, size: Float) {
        val path = Path().apply {
            moveTo(x + size * 0.3f, y - size)
            lineTo(x - size * 0.1f, y - size * 0.1f)
            lineTo(x + size * 0.2f, y - size * 0.05f)
            lineTo(x - size * 0.3f, y + size)
            lineTo(x + size * 0.1f, y + size * 0.1f)
            lineTo(x - size * 0.2f, y + size * 0.05f)
            close()
        }
        c.drawPath(path, p)
    }

    private fun drawCrusherRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Big chunky legs
        p.color = secondary
        c.drawRoundRect(-35f, 55f, -5f, 100f, 10f, 10f, p)
        c.drawRoundRect(5f, 55f, 35f, 100f, 10f, 10f, p)
        // Heavy boots
        p.color = accent
        c.drawRoundRect(-42f, 88f, -2f, 108f, 8f, 8f, p)
        c.drawRoundRect(2f, 88f, 42f, 108f, 8f, 8f, p)
        // Massive body
        p.color = primary
        c.drawRoundRect(-50f, 5f, 50f, 60f, 14f, 14f, p)
        // Armor plates
        p.color = accent
        c.drawRoundRect(-45f, 8f, -20f, 20f, 4f, 4f, p)
        c.drawRoundRect(20f, 8f, 45f, 20f, 4f, 4f, p)
        c.drawRect(-20f, 25f, 20f, 55f, p)
        // Fist symbol
        p.color = secondary
        c.drawRoundRect(-12f, 30f, 12f, 52f, 6f, 6f, p)
        // Giant arms
        p.color = secondary
        c.drawRoundRect(-78f, 8f, -48f, 58f, 14f, 14f, p)
        c.drawRoundRect(48f, 8f, 78f, 58f, 14f, 14f, p)
        // Giant fists
        p.color = primary
        c.drawRoundRect(-85f, 52f, -42f, 80f, 12f, 12f, p)
        c.drawRoundRect(42f, 52f, 85f, 80f, 12f, 12f, p)
        p.color = accent
        c.drawRoundRect(-80f, 55f, -48f, 72f, 8f, 8f, p)
        c.drawRoundRect(48f, 55f, 80f, 72f, 8f, 8f, p)
        // Neck
        p.color = secondary
        c.drawRoundRect(-15f, -8f, 15f, 8f, 6f, 6f, p)
        // Head (wide)
        p.color = primary
        c.drawRoundRect(-45f, -52f, 45f, 0f, 16f, 16f, p)
        // Visor
        p.color = Color.parseColor("#0A0A15")
        c.drawRoundRect(-38f, -44f, 38f, -18f, 10f, 10f, p)
        // Angry eyes
        p.color = accent
        c.drawCircle(-18f, -30f, 10f, p)
        c.drawCircle(18f, -30f, 10f, p)
        p.color = Color.RED
        c.drawCircle(-18f, -30f, 5f, p)
        c.drawCircle(18f, -30f, 5f, p)
        // Frown
        p.color = Color.parseColor("#333333")
        val mouth = Path().apply {
            moveTo(-15f, -12f); cubicTo(-8f, -5f, 8f, -5f, 15f, -12f)
        }
        c.drawPath(mouth, p.also { it.style = Paint.Style.STROKE; it.strokeWidth = 4f })
        p.style = Paint.Style.FILL
    }

    private fun drawSparkRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Legs with electric glow
        p.color = secondary
        c.drawRoundRect(-26f, 58f, -6f, 96f, 7f, 7f, p)
        c.drawRoundRect(6f, 58f, 26f, 96f, 7f, 7f, p)
        p.color = accent
        c.drawRoundRect(-30f, 90f, -2f, 102f, 5f, 5f, p)
        c.drawRoundRect(2f, 90f, 30f, 102f, 5f, 5f, p)
        // Body
        p.color = primary
        c.drawRoundRect(-38f, 10f, 38f, 62f, 12f, 12f, p)
        // Electric orb chest
        p.color = accent
        c.drawCircle(0f, 36f, 14f, p)
        p.color = Color.WHITE
        c.drawCircle(-4f, 32f, 5f, p)
        // Arms
        p.color = secondary
        c.drawRoundRect(-58f, 13f, -36f, 52f, 9f, 9f, p)
        c.drawRoundRect(36f, 13f, 58f, 52f, 9f, 9f, p)
        // Spark hands
        p.color = accent
        c.drawCircle(-52f, 56f, 11f, p)
        c.drawCircle(52f, 56f, 11f, p)
        p.color = Color.WHITE
        for (i in 0 until 6) {
            val angle = Math.toRadians(i * 60.0)
            c.drawLine(-52f, 56f, (-52f + 14 * sin(angle)).toFloat(), (56f - 14 * cos(angle)).toFloat(),
                p.also { it.strokeWidth = 2f; it.style = Paint.Style.STROKE })
            c.drawLine(52f, 56f, (52f + 14 * sin(angle)).toFloat(), (56f - 14 * cos(angle)).toFloat(), p)
        }
        p.style = Paint.Style.FILL
        // Neck
        p.color = secondary
        c.drawRoundRect(-11f, -5f, 11f, 12f, 4f, 4f, p)
        // Head
        p.color = primary
        c.drawRoundRect(-36f, -46f, 36f, 8f, 12f, 12f, p)
        // Gem on forehead
        p.color = accent
        drawGem(c, p, 0f, -36f, 10f)
        // Visor
        p.color = Color.parseColor("#110011")
        c.drawRoundRect(-28f, -38f, 28f, -15f, 7f, 7f, p)
        // Purple glowing eyes
        p.color = Color.parseColor("#DA70D6")
        c.drawCircle(-13f, -26f, 7f, p)
        c.drawCircle(13f, -26f, 7f, p)
        p.color = Color.WHITE
        c.drawCircle(-11f, -28f, 3f, p)
        c.drawCircle(15f, -28f, 3f, p)
        // Smile
        val smile = Path().apply {
            moveTo(-12f, -10f); cubicTo(-6f, -3f, 6f, -3f, 12f, -10f)
        }
        p.color = accent
        c.drawPath(smile, p.also { it.style = Paint.Style.STROKE; it.strokeWidth = 3f })
        p.style = Paint.Style.FILL
    }

    private fun drawGem(c: Canvas, p: Paint, x: Float, y: Float, size: Float) {
        val path = Path().apply {
            moveTo(x, y - size)
            lineTo(x + size * 0.8f, y - size * 0.2f)
            lineTo(x + size * 0.5f, y + size)
            lineTo(x - size * 0.5f, y + size)
            lineTo(x - size * 0.8f, y - size * 0.2f)
            close()
        }
        c.drawPath(path, p)
    }

    private fun drawRocketRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Rocket boosters on legs
        p.color = accent
        c.drawRoundRect(-28f, 72f, -8f, 95f, 6f, 6f, p)
        c.drawRoundRect(8f, 72f, 28f, 95f, 6f, 6f, p)
        // Exhaust flame
        p.color = Color.parseColor("#FF6600")
        c.drawRoundRect(-25f, 92f, -11f, 108f, 4f, 4f, p)
        c.drawRoundRect(11f, 92f, 25f, 108f, 4f, 4f, p)
        // Legs
        p.color = secondary
        c.drawRoundRect(-32f, 56f, -6f, 75f, 8f, 8f, p)
        c.drawRoundRect(6f, 56f, 32f, 75f, 8f, 8f, p)
        // Body with fuselage
        p.color = primary
        c.drawRoundRect(-42f, 8f, 42f, 60f, 10f, 10f, p)
        // Missile launchers on shoulders
        p.color = accent
        c.drawRoundRect(-62f, 8f, -40f, 24f, 5f, 5f, p)
        c.drawRoundRect(40f, 8f, 62f, 24f, 5f, 5f, p)
        // Arms
        p.color = secondary
        c.drawRoundRect(-62f, 22f, -40f, 54f, 9f, 9f, p)
        c.drawRoundRect(40f, 22f, 62f, 54f, 9f, 9f, p)
        // Blaster hands
        p.color = primary
        c.drawRoundRect(-68f, 50f, -36f, 68f, 8f, 8f, p)
        c.drawRoundRect(36f, 50f, 68f, 68f, 8f, 8f, p)
        p.color = accent
        c.drawRoundRect(-72f, 56f, -60f, 64f, 4f, 4f, p)
        c.drawRoundRect(60f, 56f, 72f, 64f, 4f, 4f, p)
        // Chest target
        p.color = accent
        c.drawCircle(0f, 34f, 16f, p.also { it.style = Paint.Style.STROKE; it.strokeWidth = 3f })
        c.drawCircle(0f, 34f, 8f, p)
        p.style = Paint.Style.FILL
        p.color = Color.RED
        c.drawCircle(0f, 34f, 4f, p)
        // Neck
        p.color = secondary
        c.drawRoundRect(-13f, -6f, 13f, 10f, 5f, 5f, p)
        // Helmet head
        p.color = primary
        c.drawRoundRect(-38f, -50f, 38f, 8f, 12f, 12f, p)
        // Visor (full face)
        p.color = Color.parseColor("#001133")
        c.drawRoundRect(-30f, -44f, 30f, -8f, 10f, 10f, p)
        // HUD eyes
        p.color = accent
        c.drawCircle(-13f, -28f, 8f, p)
        c.drawCircle(13f, -28f, 8f, p)
        p.color = Color.WHITE
        c.drawCircle(-11f, -30f, 3f, p)
        c.drawCircle(15f, -30f, 3f, p)
        // Antenna
        p.color = accent
        c.drawLine(-5f, -50f, -5f, -65f, p.also { it.strokeWidth = 3f; it.style = Paint.Style.STROKE })
        c.drawLine(5f, -50f, 5f, -65f, p)
        c.drawLine(-5f, -65f, 5f, -65f, p)
        p.style = Paint.Style.FILL
        c.drawCircle(-5f, -65f, 4f, p)
        c.drawCircle(5f, -65f, 4f, p)
    }

    private fun drawNovaRobot(c: Canvas, p: Paint, primary: Int, secondary: Int, accent: Int) {
        // Flowing leg streams
        p.color = secondary
        c.drawRoundRect(-28f, 58f, -8f, 95f, 7f, 7f, p)
        c.drawRoundRect(8f, 58f, 28f, 95f, 7f, 7f, p)
        // Star-shaped feet
        p.color = accent
        drawStar(c, p, -18f, 102f, 12f)
        drawStar(c, p, 18f, 102f, 12f)
        // Body (elegant)
        p.color = primary
        c.drawRoundRect(-40f, 10f, 40f, 62f, 14f, 14f, p)
        // Star emblem
        p.color = accent
        drawStar(c, p, 0f, 36f, 18f)
        // Arms (graceful)
        p.color = secondary
        c.drawRoundRect(-60f, 13f, -38f, 52f, 10f, 10f, p)
        c.drawRoundRect(38f, 13f, 60f, 52f, 10f, 10f, p)
        // Star hands
        p.color = accent
        drawStar(c, p, -52f, 58f, 14f)
        drawStar(c, p, 52f, 58f, 14f)
        // Neck
        p.color = secondary
        c.drawRoundRect(-11f, -5f, 11f, 12f, 4f, 4f, p)
        // Head
        p.color = primary
        c.drawRoundRect(-38f, -48f, 38f, 8f, 13f, 13f, p)
        // Crown / star halo
        p.color = accent
        for (i in 0 until 5) {
            val angle = Math.toRadians((i * 72 - 90).toDouble())
            val r1 = 34f; val r2 = 22f
            val x1 = (r1 * cos(angle)).toFloat()
            val y1 = (-52f + r1 * sin(angle)).toFloat()
            val x2 = (r2 * cos(angle + Math.toRadians(36.0))).toFloat()
            val y2 = (-52f + r2 * sin(angle + Math.toRadians(36.0))).toFloat()
            c.drawLine(0f, -52f, x1, y1, p.also { it.strokeWidth = 3f; it.style = Paint.Style.STROKE })
            c.drawCircle(x1, y1, 5f, p.also { it.style = Paint.Style.FILL })
        }
        p.style = Paint.Style.FILL
        // Visor
        p.color = Color.parseColor("#1A0011")
        c.drawRoundRect(-30f, -40f, 30f, -15f, 8f, 8f, p)
        // Sparkle eyes
        p.color = Color.parseColor("#FFB6C1")
        c.drawCircle(-14f, -27f, 8f, p)
        c.drawCircle(14f, -27f, 8f, p)
        p.color = Color.WHITE
        drawStar(c, p, -14f, -27f, 6f)
        drawStar(c, p, 14f, -27f, 6f)
        // Gentle smile
        val smile = Path().apply {
            moveTo(-14f, -10f); cubicTo(-7f, -2f, 7f, -2f, 14f, -10f)
        }
        p.color = accent
        c.drawPath(smile, p.also { it.style = Paint.Style.STROKE; it.strokeWidth = 3f })
        p.style = Paint.Style.FILL
    }

    private fun drawStar(c: Canvas, p: Paint, x: Float, y: Float, size: Float) {
        val path = Path()
        val spikes = 5
        val outerR = size
        val innerR = size * 0.4f
        var step = Math.PI / spikes
        path.moveTo(x, y - outerR)
        for (i in 1..spikes * 2) {
            val r = if (i % 2 == 0) outerR else innerR
            val a = i * step - Math.PI / 2
            path.lineTo((x + r * cos(a)).toFloat(), (y + r * sin(a)).toFloat())
        }
        path.close()
        c.drawPath(path, p)
    }

    fun drawHealthBar(
        canvas: Canvas,
        cx: Float, cy: Float, width: Float,
        percent: Float,
        robotColor: Int,
        label: String
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val barH = 18f
        val left = cx - width / 2
        val right = cx + width / 2

        // Background track
        paint.color = Color.parseColor("#1A1A2E")
        canvas.drawRoundRect(left - 2, cy - 2, right + 2, cy + barH + 2, 10f, 10f, paint)

        // Red warning zone
        paint.color = Color.parseColor("#FF1744")
        canvas.drawRoundRect(left, cy, right, cy + barH, 9f, 9f, paint)

        // HP fill
        val hpColor = when {
            percent > 0.5f -> Color.parseColor("#00E676")
            percent > 0.25f -> Color.parseColor("#FFEA00")
            else -> Color.parseColor("#FF1744")
        }
        paint.color = hpColor
        canvas.drawRoundRect(left, cy, left + width * percent, cy + barH, 9f, 9f, paint)

        // Shine
        paint.color = Color.argb(60, 255, 255, 255)
        canvas.drawRoundRect(left, cy, left + width * percent, cy + barH / 2, 9f, 9f, paint)

        // Label
        paint.color = Color.WHITE
        paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(label, cx, cy - 6f, paint)
    }
}
