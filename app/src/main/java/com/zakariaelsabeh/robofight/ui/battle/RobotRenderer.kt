package com.zakariaelsabeh.robofight.ui.battle

import android.graphics.*
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.data.models.RobotType
import kotlin.math.cos
import kotlin.math.sin

object RobotRenderer {

    fun drawRobot(
        canvas: Canvas, robot: Robot,
        cx: Float, cy: Float, scale: Float = 1f,
        mirrored: Boolean = false, shake: Float = 0f, alpha: Int = 255
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

    private fun dk(color: Int, f: Float): Int {
        return Color.rgb(
            (Color.red(color) * f).toInt().coerceIn(0, 255),
            (Color.green(color) * f).toInt().coerceIn(0, 255),
            (Color.blue(color) * f).toInt().coerceIn(0, 255)
        )
    }

    private fun bolt(c: Canvas, p: Paint, x: Float, y: Float) {
        p.color = Color.parseColor("#666677"); c.drawCircle(x, y, 3f, p)
        p.color = Color.parseColor("#AAAACC"); c.drawCircle(x - 1f, y - 1f, 1.5f, p)
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

    private fun drawStar(c: Canvas, p: Paint, x: Float, y: Float, size: Float) {
        val path = Path()
        val spikes = 5; val outerR = size; val innerR = size * 0.4f
        val step = Math.PI / spikes
        path.moveTo(x, y - outerR)
        for (i in 1..spikes * 2) {
            val r = if (i % 2 == 0) outerR else innerR
            val a = i * step - Math.PI / 2
            path.lineTo((x + r * cos(a)).toFloat(), (y + r * sin(a)).toFloat())
        }
        path.close()
        c.drawPath(path, p)
    }

    // ── FIRE ROBOT ─────────────────────────────────────────────────────────────
    private fun drawFireRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs
        p.color = sec; c.drawRoundRect(-28f, 55f, -8f, 82f, 6f, 6f, p)
        c.drawRoundRect(8f, 55f, 28f, 82f, 6f, 6f, p)
        p.color = dSec; c.drawRoundRect(-26f, 57f, -20f, 80f, 4f, 4f, p)
        c.drawRoundRect(20f, 57f, 26f, 80f, 4f, 4f, p)
        // Shins
        p.color = dark; c.drawRoundRect(-30f, 80f, -6f, 106f, 7f, 7f, p)
        c.drawRoundRect(6f, 80f, 30f, 106f, 7f, 7f, p)
        p.color = accent; c.drawRoundRect(-28f, 82f, -10f, 92f, 4f, 4f, p)
        c.drawRoundRect(10f, 82f, 28f, 92f, 4f, 4f, p)
        // Feet
        p.color = dSec; c.drawRoundRect(-36f, 102f, -4f, 113f, 5f, 5f, p)
        c.drawRoundRect(4f, 102f, 36f, 113f, 5f, 5f, p)
        // Knee joints
        p.color = Color.parseColor("#1A1A1A"); c.drawCircle(-18f, 82f, 6f, p)
        c.drawCircle(18f, 82f, 6f, p)
        p.color = accent; c.drawCircle(-18f, 82f, 3f, p); c.drawCircle(18f, 82f, 3f, p)
        // Body
        p.color = primary; c.drawRoundRect(-44f, 8f, 44f, 58f, 10f, 10f, p)
        // Shoulder pauldrons
        p.color = dark; c.drawRoundRect(-50f, 8f, -40f, 30f, 5f, 5f, p)
        c.drawRoundRect(40f, 8f, 50f, 30f, 5f, 5f, p)
        // Chest plate
        p.color = dk(primary, 0.7f); c.drawRoundRect(-36f, 12f, 36f, 40f, 6f, 6f, p)
        // Energy core
        p.color = Color.parseColor("#0A0A0A"); c.drawRoundRect(-10f, 20f, 10f, 38f, 5f, 5f, p)
        p.color = accent; c.drawRoundRect(-8f, 22f, 8f, 36f, 4f, 4f, p)
        p.color = Color.WHITE; c.drawRoundRect(-5f, 24f, 5f, 32f, 3f, 3f, p)
        // Vent slots
        p.color = Color.parseColor("#0D0D0D")
        for (i in -2..2) c.drawRoundRect(-38f + i * 12, 42f, -30f + i * 12, 55f, 2f, 2f, p)
        bolt(c, p, -38f, 14f); bolt(c, p, 38f, 14f); bolt(c, p, -38f, 38f); bolt(c, p, 38f, 38f)
        // Left arm
        p.color = sec; c.drawRoundRect(-70f, 10f, -42f, 30f, 8f, 8f, p)
        p.color = dark; c.drawRoundRect(-68f, 12f, -56f, 28f, 5f, 5f, p)
        p.color = primary; c.drawRoundRect(-72f, 28f, -42f, 58f, 8f, 8f, p)
        p.color = accent; c.drawRoundRect(-70f, 32f, -56f, 42f, 3f, 3f, p)
        // Left elbow
        p.color = Color.parseColor("#111111"); c.drawCircle(-56f, 30f, 7f, p)
        p.color = accent; c.drawCircle(-56f, 30f, 3.5f, p)
        // Left fist
        p.color = dark; c.drawRoundRect(-78f, 55f, -40f, 76f, 10f, 10f, p)
        p.color = accent; c.drawRoundRect(-76f, 58f, -60f, 68f, 4f, 4f, p)
        c.drawRoundRect(-56f, 58f, -44f, 68f, 4f, 4f, p)
        // Right arm
        p.color = sec; c.drawRoundRect(42f, 10f, 70f, 30f, 8f, 8f, p)
        p.color = dark; c.drawRoundRect(56f, 12f, 68f, 28f, 5f, 5f, p)
        p.color = primary; c.drawRoundRect(42f, 28f, 72f, 58f, 8f, 8f, p)
        p.color = accent; c.drawRoundRect(56f, 32f, 70f, 42f, 3f, 3f, p)
        p.color = Color.parseColor("#111111"); c.drawCircle(56f, 30f, 7f, p)
        p.color = accent; c.drawCircle(56f, 30f, 3.5f, p)
        p.color = dark; c.drawRoundRect(40f, 55f, 78f, 76f, 10f, 10f, p)
        p.color = accent; c.drawRoundRect(44f, 58f, 58f, 68f, 4f, 4f, p)
        c.drawRoundRect(62f, 58f, 76f, 68f, 4f, 4f, p)
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-10f, -2f, 10f, 10f, 4f, 4f, p)
        // Head
        p.color = primary; c.drawRoundRect(-40f, -50f, 40f, 2f, 12f, 12f, p)
        p.color = dark; c.drawRoundRect(-40f, -50f, 40f, -38f, 12f, 12f, p)
        p.color = dk(primary, 0.65f); c.drawRoundRect(-42f, -42f, -34f, -10f, 5f, 5f, p)
        c.drawRoundRect(34f, -42f, 42f, -10f, 5f, 5f, p)
        // Visor
        p.color = Color.parseColor("#080808"); c.drawRoundRect(-32f, -42f, 32f, -12f, 8f, 8f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawRoundRect(-32f, -42f, 32f, -12f, 8f, 8f, p); p.style = Paint.Style.FILL
        // Eyes
        p.color = accent; c.drawRoundRect(-26f, -36f, -10f, -24f, 4f, 4f, p)
        c.drawRoundRect(10f, -36f, 26f, -24f, 4f, 4f, p)
        p.color = Color.WHITE; c.drawRoundRect(-24f, -34f, -14f, -28f, 2f, 2f, p)
        c.drawRoundRect(14f, -34f, 24f, -28f, 2f, 2f, p)
        // Jaw / grille
        p.color = dk(primary, 0.6f); c.drawRoundRect(-28f, -14f, 28f, -2f, 5f, 5f, p)
        p.color = Color.parseColor("#0D0D0D")
        for (i in -3..3) c.drawRoundRect(-22f + i * 7, -12f, -17f + i * 7, -8f, 2f, 2f, p)
        bolt(c, p, -38f, -48f); bolt(c, p, 38f, -48f)
        // Flame crown
        p.color = accent; drawFlame(c, p, 0f, -54f, 9f)
        drawFlame(c, p, -18f, -52f, 7f); drawFlame(c, p, 18f, -52f, 7f)
    }

    // ── THUNDER ROBOT ───────────────────────────────────────────────────────────
    private fun drawThunderRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs
        p.color = sec; c.drawRoundRect(-24f, 55f, -6f, 80f, 5f, 5f, p)
        c.drawRoundRect(6f, 55f, 24f, 80f, 5f, 5f, p)
        p.color = accent; c.drawRoundRect(-22f, 62f, -8f, 70f, 3f, 3f, p)
        c.drawRoundRect(8f, 62f, 22f, 70f, 3f, 3f, p)
        // Shins
        p.color = dark; c.drawRoundRect(-26f, 78f, -4f, 104f, 5f, 5f, p)
        c.drawRoundRect(4f, 78f, 26f, 104f, 5f, 5f, p)
        // Boots
        p.color = dSec; c.drawRoundRect(-32f, 100f, 2f, 112f, 5f, 5f, p)
        c.drawRoundRect(-2f, 100f, 32f, 112f, 5f, 5f, p)
        p.color = accent; c.drawRoundRect(-30f, 104f, -14f, 108f, 2f, 2f, p)
        c.drawRoundRect(14f, 104f, 30f, 108f, 2f, 2f, p)
        // Knees
        p.color = Color.parseColor("#0D0D1A"); c.drawCircle(-15f, 80f, 5f, p)
        c.drawCircle(15f, 80f, 5f, p)
        p.color = accent; c.drawCircle(-15f, 80f, 2.5f, p); c.drawCircle(15f, 80f, 2.5f, p)
        // Body
        p.color = primary; c.drawRoundRect(-38f, 8f, 38f, 58f, 8f, 8f, p)
        p.color = dark; c.drawRoundRect(-40f, 12f, -32f, 50f, 4f, 4f, p)
        c.drawRoundRect(32f, 12f, 40f, 50f, 4f, 4f, p)
        // Center stripe
        p.color = accent; c.drawRect(-4f, 10f, 4f, 56f, p)
        p.color = dk(primary, 0.75f); c.drawRoundRect(-30f, 14f, 30f, 44f, 6f, 6f, p)
        // Lightning core
        p.color = Color.parseColor("#0A0A0A"); c.drawCircle(0f, 29f, 12f, p)
        p.color = accent; c.drawCircle(0f, 29f, 10f, p)
        p.color = dk(accent, 0.5f); drawLightning(c, p, 0f, 29f, 9f)
        p.color = Color.WHITE; p.alpha = 200; c.drawCircle(0f, 29f, 4f, p); p.alpha = 255
        // Vents
        p.color = Color.parseColor("#0D0D1A")
        for (i in -2..2) c.drawRoundRect(-32f + i * 13, 44f, -25f + i * 13, 56f, 2f, 2f, p)
        bolt(c, p, -38f, 10f); bolt(c, p, 38f, 10f)
        // Arms
        p.color = sec; c.drawRoundRect(-64f, 10f, -36f, 28f, 7f, 7f, p)
        c.drawRoundRect(36f, 10f, 64f, 28f, 7f, 7f, p)
        p.color = primary; c.drawRoundRect(-66f, 26f, -36f, 58f, 7f, 7f, p)
        c.drawRoundRect(36f, 26f, 66f, 58f, 7f, 7f, p)
        p.color = accent; c.drawRoundRect(-64f, 30f, -52f, 40f, 3f, 3f, p)
        c.drawRoundRect(52f, 30f, 64f, 40f, 3f, 3f, p)
        // Elbows
        p.color = Color.parseColor("#0D0D0D"); c.drawCircle(-51f, 28f, 6f, p)
        c.drawCircle(51f, 28f, 6f, p)
        p.color = accent; c.drawCircle(-51f, 28f, 3f, p); c.drawCircle(51f, 28f, 3f, p)
        // Electric fists
        p.color = dark; c.drawRoundRect(-72f, 54f, -36f, 74f, 8f, 8f, p)
        c.drawRoundRect(36f, 54f, 72f, 74f, 8f, 8f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawRoundRect(-70f, 56f, -38f, 72f, 6f, 6f, p)
        c.drawRoundRect(38f, 56f, 70f, 72f, 6f, 6f, p); p.style = Paint.Style.FILL
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-8f, -2f, 8f, 10f, 4f, 4f, p)
        // Head
        p.color = primary; c.drawRoundRect(-35f, -48f, 35f, 2f, 10f, 10f, p)
        // Swept crest
        p.color = dark; val crest = Path().apply {
            moveTo(-10f, -48f); cubicTo(-5f, -60f, 5f, -60f, 10f, -48f); close()
        }; c.drawPath(crest, p)
        p.color = accent; c.drawRect(-2f, -58f, 2f, -48f, p)
        p.color = dk(primary, 0.65f); c.drawRoundRect(-38f, -40f, -30f, -8f, 5f, 5f, p)
        c.drawRoundRect(30f, -40f, 38f, -8f, 5f, 5f, p)
        // Visor
        p.color = Color.parseColor("#050510"); c.drawRoundRect(-28f, -42f, 28f, -12f, 6f, 6f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f
        c.drawRoundRect(-28f, -42f, 28f, -12f, 6f, 6f, p); p.style = Paint.Style.FILL
        // Eye slits
        p.color = accent; c.drawRoundRect(-24f, -32f, -8f, -26f, 3f, 3f, p)
        c.drawRoundRect(8f, -32f, 24f, -26f, 3f, 3f, p)
        p.color = Color.WHITE; c.drawRoundRect(-22f, -31f, -12f, -28f, 2f, 2f, p)
        // Chin vents
        p.color = dk(primary, 0.7f); c.drawRoundRect(-20f, -14f, 20f, -2f, 4f, 4f, p)
        p.color = Color.parseColor("#0A0A1A")
        c.drawRoundRect(-18f, -12f, -10f, -8f, 2f, 2f, p)
        c.drawRoundRect(-6f, -12f, 6f, -8f, 2f, 2f, p)
        c.drawRoundRect(10f, -12f, 18f, -8f, 2f, 2f, p)
        // Antenna
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2.5f
        c.drawLine(0f, -48f, 0f, -64f, p); p.style = Paint.Style.FILL
        c.drawCircle(0f, -66f, 5f, p); p.color = Color.WHITE; c.drawCircle(0f, -66f, 2.5f, p)
        bolt(c, p, -33f, -46f); bolt(c, p, 33f, -46f)
    }

    // ── CRUSHER ROBOT ───────────────────────────────────────────────────────────
    private fun drawCrusherRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs (massive)
        p.color = sec; c.drawRoundRect(-42f, 50f, -8f, 84f, 8f, 8f, p)
        c.drawRoundRect(8f, 50f, 42f, 84f, 8f, 8f, p)
        p.color = dSec; c.drawRoundRect(-40f, 52f, -18f, 72f, 5f, 5f, p)
        c.drawRoundRect(18f, 52f, 40f, 72f, 5f, 5f, p)
        // Shins
        p.color = dark; c.drawRoundRect(-44f, 82f, -6f, 114f, 8f, 8f, p)
        c.drawRoundRect(6f, 82f, 44f, 114f, 8f, 8f, p)
        p.color = accent; c.drawRoundRect(-42f, 84f, -14f, 96f, 4f, 4f, p)
        c.drawRoundRect(14f, 84f, 42f, 96f, 4f, 4f, p)
        // Heavy boots
        p.color = dk(sec, 0.6f); c.drawRoundRect(-52f, 110f, -2f, 122f, 6f, 6f, p)
        c.drawRoundRect(2f, 110f, 52f, 122f, 6f, 6f, p)
        // Boot spikes
        p.color = accent
        for (xOff in listOf(-50f, -38f, -28f)) c.drawRoundRect(xOff, 120f, xOff + 8, 128f, 3f, 3f, p)
        for (xOff in listOf(20f, 30f, 42f)) c.drawRoundRect(xOff, 120f, xOff + 8, 128f, 3f, 3f, p)
        // Knee plates
        p.color = Color.parseColor("#111111"); c.drawRoundRect(-38f, 82f, -12f, 90f, 4f, 4f, p)
        c.drawRoundRect(12f, 82f, 38f, 90f, 4f, 4f, p)
        p.color = accent; c.drawRoundRect(-36f, 84f, -20f, 88f, 3f, 3f, p)
        c.drawRoundRect(20f, 84f, 36f, 88f, 3f, 3f, p)
        // Body (massive)
        p.color = primary; c.drawRoundRect(-58f, 4f, 58f, 54f, 12f, 12f, p)
        p.color = dark; c.drawRoundRect(-64f, 2f, -46f, 28f, 8f, 8f, p)
        c.drawRoundRect(46f, 2f, 64f, 28f, 8f, 8f, p)
        p.color = accent; c.drawRoundRect(-62f, 4f, -48f, 14f, 5f, 5f, p)
        c.drawRoundRect(48f, 4f, 62f, 14f, 5f, 5f, p)
        p.color = dk(primary, 0.7f); c.drawRoundRect(-52f, 8f, 52f, 36f, 8f, 8f, p)
        // Chest core
        p.color = Color.parseColor("#111111"); c.drawRoundRect(-18f, 14f, 18f, 32f, 6f, 6f, p)
        p.color = accent; c.drawRoundRect(-14f, 16f, 14f, 30f, 4f, 4f, p)
        p.color = Color.WHITE; c.drawRoundRect(-10f, 18f, 10f, 28f, 3f, 3f, p)
        // Belly strips
        p.color = dark
        for (i in -2..2) c.drawRoundRect(-50f + i * 20, 36f, -34f + i * 20, 50f, 4f, 4f, p)
        bolt(c, p, -56f, 6f); bolt(c, p, 56f, 6f); bolt(c, p, -56f, 52f); bolt(c, p, 56f, 52f)
        // Arms (pile-driver)
        p.color = sec; c.drawRoundRect(-100f, 4f, -58f, 34f, 12f, 12f, p)
        c.drawRoundRect(58f, 4f, 100f, 34f, 12f, 12f, p)
        p.color = dSec; c.drawRoundRect(-96f, 6f, -70f, 22f, 6f, 6f, p)
        c.drawRoundRect(70f, 6f, 96f, 22f, 6f, 6f, p)
        p.color = primary; c.drawRoundRect(-102f, 32f, -58f, 70f, 10f, 10f, p)
        c.drawRoundRect(58f, 32f, 102f, 70f, 10f, 10f, p)
        p.color = accent; c.drawRoundRect(-100f, 36f, -80f, 46f, 4f, 4f, p)
        c.drawRoundRect(80f, 36f, 100f, 46f, 4f, 4f, p)
        // Elbows
        p.color = Color.parseColor("#0A0A0A"); c.drawCircle(-80f, 34f, 8f, p)
        c.drawCircle(80f, 34f, 8f, p)
        p.color = accent; c.drawCircle(-80f, 34f, 4f, p); c.drawCircle(80f, 34f, 4f, p)
        // Crushing fists
        p.color = dark; c.drawRoundRect(-110f, 66f, -58f, 98f, 12f, 12f, p)
        c.drawRoundRect(58f, 66f, 110f, 98f, 12f, 12f, p)
        p.color = accent; c.drawRoundRect(-108f, 70f, -84f, 82f, 5f, 5f, p)
        c.drawRoundRect(-80f, 70f, -62f, 82f, 5f, 5f, p)
        c.drawRoundRect(62f, 70f, 80f, 82f, 5f, 5f, p)
        c.drawRoundRect(84f, 70f, 108f, 82f, 5f, 5f, p)
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-16f, -6f, 16f, 6f, 5f, 5f, p)
        // Head (wide + intimidating)
        p.color = primary; c.drawRoundRect(-50f, -60f, 50f, 2f, 14f, 14f, p)
        p.color = dark; c.drawRoundRect(-50f, -60f, 50f, -46f, 14f, 14f, p)
        c.drawRoundRect(-48f, -48f, 48f, -40f, 5f, 5f, p)
        p.color = dk(primary, 0.65f); c.drawRoundRect(-52f, -50f, -42f, -6f, 6f, 6f, p)
        c.drawRoundRect(42f, -50f, 52f, -6f, 6f, 6f, p)
        // Red visor
        p.color = Color.parseColor("#080808"); c.drawRoundRect(-40f, -48f, 40f, -16f, 8f, 8f, p)
        p.color = Color.RED; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawRoundRect(-40f, -48f, 40f, -16f, 8f, 8f, p); p.style = Paint.Style.FILL
        // Angry red eyes
        p.color = Color.RED; c.drawRoundRect(-36f, -44f, -16f, -30f, 4f, 4f, p)
        c.drawRoundRect(16f, -44f, 36f, -30f, 4f, 4f, p)
        p.color = Color.parseColor("#FF6666"); c.drawRoundRect(-34f, -42f, -20f, -34f, 3f, 3f, p)
        c.drawRoundRect(20f, -42f, 34f, -34f, 3f, 3f, p)
        // Jaw
        p.color = dk(primary, 0.6f); c.drawRoundRect(-38f, -18f, 38f, -2f, 5f, 5f, p)
        p.color = Color.parseColor("#0D0D0D")
        for (i in -3..3) c.drawRoundRect(-36f + i * 11, -16f, -28f + i * 11, -8f, 2f, 2f, p)
        bolt(c, p, -48f, -58f); bolt(c, p, 48f, -58f)
        bolt(c, p, -48f, -6f); bolt(c, p, 48f, -6f)
    }

    // ── SPARK ROBOT ─────────────────────────────────────────────────────────────
    private fun drawSparkRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs with coil bands
        p.color = sec; c.drawRoundRect(-28f, 56f, -8f, 82f, 6f, 6f, p)
        c.drawRoundRect(8f, 56f, 28f, 82f, 6f, 6f, p)
        p.color = accent
        c.drawRoundRect(-26f, 60f, -10f, 66f, 3f, 3f, p); c.drawRoundRect(10f, 60f, 26f, 66f, 3f, 3f, p)
        c.drawRoundRect(-26f, 72f, -10f, 78f, 3f, 3f, p); c.drawRoundRect(10f, 72f, 26f, 78f, 3f, 3f, p)
        p.color = dark; c.drawRoundRect(-30f, 80f, -6f, 106f, 6f, 6f, p)
        c.drawRoundRect(6f, 80f, 30f, 106f, 6f, 6f, p)
        p.color = dSec; c.drawRoundRect(-34f, 102f, -2f, 114f, 5f, 5f, p)
        c.drawRoundRect(2f, 102f, 34f, 114f, 5f, 5f, p)
        p.color = accent; c.drawCircle(-18f, 114f, 5f, p); c.drawCircle(18f, 114f, 5f, p)
        // Knees
        p.color = Color.parseColor("#1A001A"); c.drawCircle(-18f, 82f, 6f, p)
        c.drawCircle(18f, 82f, 6f, p)
        p.color = accent; c.drawCircle(-18f, 82f, 3f, p); c.drawCircle(18f, 82f, 3f, p)
        // Body
        p.color = primary; c.drawRoundRect(-40f, 8f, 40f, 58f, 10f, 10f, p)
        p.color = dark; c.drawRoundRect(-44f, 12f, -36f, 54f, 4f, 4f, p)
        c.drawRoundRect(36f, 12f, 44f, 54f, 4f, 4f, p)
        for (i in 0..4) {
            p.color = accent; c.drawRoundRect(-43f, 14f + i * 8, -37f, 18f + i * 8, 2f, 2f, p)
            c.drawRoundRect(37f, 14f + i * 8, 43f, 18f + i * 8, 2f, 2f, p)
        }
        p.color = dk(primary, 0.7f); c.drawRoundRect(-30f, 14f, 30f, 46f, 8f, 8f, p)
        // Electric orb
        p.color = Color.parseColor("#1A0020"); c.drawCircle(0f, 30f, 16f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawCircle(0f, 30f, 16f, p); p.style = Paint.Style.FILL
        p.color = accent; c.drawCircle(0f, 30f, 11f, p)
        p.color = Color.WHITE; p.alpha = 180; c.drawCircle(-3f, 27f, 5f, p); p.alpha = 255
        p.color = Color.parseColor("#0D0D0D")
        for (i in -3..3) c.drawRoundRect(-36f + i * 11, 48f, -28f + i * 11, 56f, 2f, 2f, p)
        bolt(c, p, -40f, 10f); bolt(c, p, 40f, 10f)
        // Arms
        p.color = sec; c.drawRoundRect(-64f, 10f, -38f, 30f, 8f, 8f, p)
        c.drawRoundRect(38f, 10f, 64f, 30f, 8f, 8f, p)
        p.color = accent; c.drawRoundRect(-62f, 14f, -42f, 18f, 3f, 3f, p)
        c.drawRoundRect(42f, 14f, 62f, 18f, 3f, 3f, p)
        c.drawRoundRect(-62f, 22f, -42f, 26f, 3f, 3f, p)
        c.drawRoundRect(42f, 22f, 62f, 26f, 3f, 3f, p)
        p.color = primary; c.drawRoundRect(-66f, 28f, -38f, 58f, 8f, 8f, p)
        c.drawRoundRect(38f, 28f, 66f, 58f, 8f, 8f, p)
        p.color = Color.parseColor("#110011"); c.drawCircle(-52f, 30f, 7f, p)
        c.drawCircle(52f, 30f, 7f, p)
        p.color = accent; c.drawCircle(-52f, 30f, 3.5f, p); c.drawCircle(52f, 30f, 3.5f, p)
        // Spark emitter hands
        p.color = dark; c.drawRoundRect(-72f, 54f, -36f, 76f, 9f, 9f, p)
        c.drawRoundRect(36f, 54f, 72f, 76f, 9f, 9f, p)
        for (i in 0..2) {
            p.color = accent; c.drawCircle(-68f + i * 14, 65f, 4f, p)
            c.drawCircle(38f + i * 14, 65f, 4f, p)
            p.color = Color.WHITE; c.drawCircle(-68f + i * 14, 65f, 2f, p)
            c.drawCircle(38f + i * 14, 65f, 2f, p)
        }
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-10f, -2f, 10f, 10f, 4f, 4f, p)
        p.color = accent; c.drawRoundRect(-10f, 0f, 10f, 4f, 2f, 2f, p)
        c.drawRoundRect(-10f, 6f, 10f, 10f, 2f, 2f, p)
        // Head
        p.color = primary; c.drawRoundRect(-38f, -50f, 38f, 2f, 12f, 12f, p)
        p.color = dark; c.drawRoundRect(-38f, -50f, 38f, -38f, 12f, 12f, p)
        p.color = accent; drawGem(c, p, 0f, -46f, 10f)
        p.color = Color.WHITE; p.alpha = 150; drawGem(c, p, -2f, -48f, 5f); p.alpha = 255
        p.color = dk(primary, 0.65f); c.drawRoundRect(-40f, -44f, -32f, -8f, 5f, 5f, p)
        c.drawRoundRect(32f, -44f, 40f, -8f, 5f, 5f, p)
        p.color = Color.parseColor("#1A0020"); c.drawRoundRect(-30f, -38f, 30f, -12f, 7f, 7f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f
        c.drawRoundRect(-30f, -38f, 30f, -12f, 7f, 7f, p); p.style = Paint.Style.FILL
        p.color = Color.parseColor("#DA70D6")
        c.drawRoundRect(-26f, -34f, -8f, -26f, 4f, 4f, p)
        c.drawRoundRect(8f, -34f, 26f, -26f, 4f, 4f, p)
        p.color = Color.WHITE; c.drawRoundRect(-24f, -32f, -14f, -29f, 2f, 2f, p)
        p.color = Color.parseColor("#0A000A"); c.drawRoundRect(-24f, -16f, 24f, -8f, 4f, 4f, p)
        for (i in -3..3) { p.color = accent; c.drawRect(-22f + i * 7, -15f, -17f + i * 7, -9f, p) }
        bolt(c, p, -36f, -48f); bolt(c, p, 36f, -48f)
    }

    // ── ROCKET ROBOT ────────────────────────────────────────────────────────────
    private fun drawRocketRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs
        p.color = sec; c.drawRoundRect(-30f, 54f, -8f, 80f, 7f, 7f, p)
        c.drawRoundRect(8f, 54f, 30f, 80f, 7f, 7f, p)
        p.color = accent; c.drawRoundRect(-28f, 70f, -10f, 78f, 4f, 4f, p)
        c.drawRoundRect(10f, 70f, 28f, 78f, 4f, 4f, p)
        p.color = dark; c.drawRoundRect(-34f, 78f, -6f, 106f, 7f, 7f, p)
        c.drawRoundRect(6f, 78f, 34f, 106f, 7f, 7f, p)
        // Booster nozzles
        p.color = accent; c.drawRoundRect(-30f, 102f, -10f, 118f, 5f, 5f, p)
        c.drawRoundRect(10f, 102f, 30f, 118f, 5f, 5f, p)
        p.color = Color.parseColor("#FF6600"); c.drawRoundRect(-28f, 114f, -12f, 122f, 3f, 3f, p)
        c.drawRoundRect(12f, 114f, 28f, 122f, 3f, 3f, p)
        p.color = Color.parseColor("#FF4400"); c.drawRoundRect(-26f, 120f, -14f, 128f, 4f, 4f, p)
        c.drawRoundRect(14f, 120f, 26f, 128f, 4f, 4f, p)
        p.color = Color.parseColor("#FFAA00"); c.drawRoundRect(-23f, 126f, -17f, 132f, 3f, 3f, p)
        c.drawRoundRect(17f, 126f, 23f, 132f, 3f, 3f, p)
        // Knees
        p.color = Color.parseColor("#111111"); c.drawCircle(-19f, 80f, 6f, p)
        c.drawCircle(19f, 80f, 6f, p)
        p.color = accent; c.drawCircle(-19f, 80f, 3f, p); c.drawCircle(19f, 80f, 3f, p)
        // Body (fuselage)
        p.color = primary; c.drawRoundRect(-46f, 6f, 46f, 56f, 10f, 10f, p)
        // Wing fins
        val lFin = Path().apply { moveTo(-46f, 10f); lineTo(-68f, 4f); lineTo(-68f, 30f); lineTo(-46f, 32f); close() }
        val rFin = Path().apply { moveTo(46f, 10f); lineTo(68f, 4f); lineTo(68f, 30f); lineTo(46f, 32f); close() }
        p.color = dark; c.drawPath(lFin, p); c.drawPath(rFin, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawPath(lFin, p); c.drawPath(rFin, p); p.style = Paint.Style.FILL
        // Chest targeting
        p.color = dk(primary, 0.7f); c.drawRoundRect(-34f, 10f, 34f, 42f, 8f, 8f, p)
        p.color = Color.parseColor("#0D0D0D"); c.drawCircle(0f, 26f, 16f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawCircle(0f, 26f, 16f, p); c.drawCircle(0f, 26f, 9f, p); c.drawCircle(0f, 26f, 4f, p)
        c.drawLine(-18f, 26f, 18f, 26f, p); c.drawLine(0f, 8f, 0f, 44f, p)
        p.style = Paint.Style.FILL; p.color = Color.RED; c.drawCircle(0f, 26f, 3f, p)
        // Missile pods
        p.color = dark; c.drawRoundRect(-44f, 42f, -22f, 54f, 4f, 4f, p)
        c.drawRoundRect(22f, 42f, 44f, 54f, 4f, 4f, p)
        p.color = accent
        for (i in 0..2) { c.drawRoundRect(-42f + i * 8, 44f, -36f + i * 8, 52f, 2f, 2f, p)
            c.drawRoundRect(24f + i * 8, 44f, 30f + i * 8, 52f, 2f, 2f, p) }
        bolt(c, p, -44f, 8f); bolt(c, p, 44f, 8f)
        // Arms (cannon)
        p.color = sec; c.drawRoundRect(-76f, 6f, -44f, 26f, 10f, 10f, p)
        c.drawRoundRect(44f, 6f, 76f, 26f, 10f, 10f, p)
        p.color = dSec; c.drawRoundRect(-74f, 8f, -58f, 22f, 6f, 6f, p)
        c.drawRoundRect(58f, 8f, 74f, 22f, 6f, 6f, p)
        p.color = primary; c.drawRoundRect(-78f, 24f, -44f, 60f, 9f, 9f, p)
        c.drawRoundRect(44f, 24f, 78f, 60f, 9f, 9f, p)
        p.color = accent; c.drawRoundRect(-76f, 28f, -62f, 38f, 4f, 4f, p)
        c.drawRoundRect(62f, 28f, 76f, 38f, 4f, 4f, p)
        p.color = Color.parseColor("#0A0A0A"); c.drawCircle(-61f, 26f, 8f, p)
        c.drawCircle(61f, 26f, 8f, p)
        p.color = accent; c.drawCircle(-61f, 26f, 4f, p); c.drawCircle(61f, 26f, 4f, p)
        // Cannon muzzles
        p.color = dark; c.drawRoundRect(-84f, 56f, -44f, 78f, 9f, 9f, p)
        c.drawRoundRect(44f, 56f, 84f, 78f, 9f, 9f, p)
        p.color = Color.parseColor("#050505"); c.drawCircle(-84f, 67f, 8f, p)
        c.drawCircle(84f, 67f, 8f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawCircle(-84f, 67f, 8f, p); c.drawCircle(84f, 67f, 8f, p); p.style = Paint.Style.FILL
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-12f, -4f, 12f, 8f, 5f, 5f, p)
        // Head
        p.color = primary; c.drawRoundRect(-42f, -56f, 42f, 2f, 12f, 12f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 2f
        c.drawLine(-8f, -56f, -8f, -70f, p); c.drawLine(8f, -56f, 8f, -70f, p)
        c.drawLine(-8f, -70f, 8f, -70f, p); p.style = Paint.Style.FILL
        c.drawCircle(-8f, -70f, 4f, p); c.drawCircle(8f, -70f, 4f, p)
        p.color = Color.WHITE; c.drawCircle(-8f, -70f, 2f, p); c.drawCircle(8f, -70f, 2f, p)
        p.color = dark; c.drawRoundRect(-42f, -56f, 42f, -44f, 12f, 12f, p)
        p.color = dk(primary, 0.65f); c.drawRoundRect(-44f, -50f, -36f, -6f, 6f, 6f, p)
        c.drawRoundRect(36f, -50f, 44f, -6f, 6f, 6f, p)
        p.color = Color.parseColor("#001122"); c.drawRoundRect(-34f, -48f, 34f, -10f, 8f, 8f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f
        c.drawRoundRect(-34f, -48f, 34f, -10f, 8f, 8f, p); p.style = Paint.Style.FILL
        p.color = accent; c.drawCircle(-14f, -32f, 9f, p); c.drawCircle(14f, -32f, 9f, p)
        p.color = Color.parseColor("#002244"); c.drawCircle(-14f, -32f, 6f, p); c.drawCircle(14f, -32f, 6f, p)
        p.color = accent; c.drawCircle(-14f, -32f, 3f, p); c.drawCircle(14f, -32f, 3f, p)
        p.color = Color.WHITE; c.drawCircle(-12f, -34f, 2f, p)
        p.color = dk(primary, 0.6f); c.drawRoundRect(-30f, -12f, 30f, -2f, 5f, 5f, p)
        p.color = Color.parseColor("#0D0D0D")
        for (i in -2..2) c.drawRoundRect(-24f + i * 10, -11f, -17f + i * 10, -6f, 2f, 2f, p)
        bolt(c, p, -40f, -54f); bolt(c, p, 40f, -54f)
    }

    // ── NOVA ROBOT ──────────────────────────────────────────────────────────────
    private fun drawNovaRobot(c: Canvas, p: Paint, primary: Int, sec: Int, accent: Int) {
        val dark = dk(primary, 0.5f); val dSec = dk(sec, 0.55f)
        // Legs
        p.color = sec; c.drawRoundRect(-28f, 56f, -8f, 82f, 7f, 7f, p)
        c.drawRoundRect(8f, 56f, 28f, 82f, 7f, 7f, p)
        p.color = accent; drawStar(c, p, -18f, 68f, 8f); drawStar(c, p, 18f, 68f, 8f)
        p.color = dark; c.drawRoundRect(-30f, 80f, -6f, 108f, 7f, 7f, p)
        c.drawRoundRect(6f, 80f, 30f, 108f, 7f, 7f, p)
        p.color = accent; c.drawRoundRect(-28f, 102f, -8f, 108f, 4f, 4f, p)
        c.drawRoundRect(8f, 102f, 28f, 108f, 4f, 4f, p)
        p.color = Color.WHITE; p.alpha = 150; c.drawRoundRect(-26f, 106f, -14f, 110f, 3f, 3f, p)
        c.drawRoundRect(14f, 106f, 26f, 110f, 3f, 3f, p); p.alpha = 255
        // Knee gems
        p.color = Color.parseColor("#1A0020"); c.drawCircle(-18f, 82f, 7f, p)
        c.drawCircle(18f, 82f, 7f, p)
        p.color = accent; drawGem(c, p, -18f, 82f, 6f); drawGem(c, p, 18f, 82f, 6f)
        // Body
        p.color = primary; c.drawRoundRect(-42f, 8f, 42f, 58f, 12f, 12f, p)
        val lPanel = Path().apply { moveTo(-42f,12f); cubicTo(-52f,15f,-52f,45f,-42f,54f)
            lineTo(-38f,54f); cubicTo(-46f,45f,-46f,15f,-38f,12f); close() }
        val rPanel = Path().apply { moveTo(42f,12f); cubicTo(52f,15f,52f,45f,42f,54f)
            lineTo(38f,54f); cubicTo(46f,45f,46f,15f,38f,12f); close() }
        p.color = dark; c.drawPath(lPanel, p); c.drawPath(rPanel, p)
        p.color = dk(primary, 0.7f); c.drawRoundRect(-30f, 14f, 30f, 46f, 8f, 8f, p)
        p.color = Color.parseColor("#1A0020"); c.drawCircle(0f, 30f, 14f, p)
        p.color = accent; drawStar(c, p, 0f, 30f, 14f)
        p.color = Color.WHITE; p.alpha = 180; drawStar(c, p, 0f, 30f, 7f); p.alpha = 255
        p.color = accent; c.drawRoundRect(-36f, 48f, 36f, 52f, 3f, 3f, p)
        bolt(c, p, -40f, 10f); bolt(c, p, 40f, 10f)
        // Arms
        p.color = sec; c.drawRoundRect(-64f, 10f, -40f, 30f, 9f, 9f, p)
        c.drawRoundRect(40f, 10f, 64f, 30f, 9f, 9f, p)
        p.color = accent; drawStar(c, p, -52f, 20f, 8f); drawStar(c, p, 52f, 20f, 8f)
        p.color = primary; c.drawRoundRect(-66f, 28f, -40f, 58f, 9f, 9f, p)
        c.drawRoundRect(40f, 28f, 66f, 58f, 9f, 9f, p)
        p.color = accent; c.drawRoundRect(-64f, 32f, -48f, 38f, 3f, 3f, p)
        c.drawRoundRect(48f, 32f, 64f, 38f, 3f, 3f, p)
        p.color = Color.parseColor("#1A0020"); c.drawCircle(-53f, 30f, 7f, p)
        c.drawCircle(53f, 30f, 7f, p)
        p.color = accent; drawGem(c, p, -53f, 30f, 6f); drawGem(c, p, 53f, 30f, 6f)
        // Star hands
        p.color = dark; c.drawRoundRect(-72f, 54f, -38f, 76f, 10f, 10f, p)
        c.drawRoundRect(38f, 54f, 72f, 76f, 10f, 10f, p)
        p.color = accent; drawStar(c, p, -55f, 65f, 14f); drawStar(c, p, 55f, 65f, 14f)
        p.color = Color.WHITE; p.alpha = 150; drawStar(c, p, -55f, 65f, 7f)
        drawStar(c, p, 55f, 65f, 7f); p.alpha = 255
        // Neck
        p.color = dk(sec, 0.7f); c.drawRoundRect(-10f, -2f, 10f, 10f, 4f, 4f, p)
        p.color = accent; c.drawRoundRect(-10f, 2f, 10f, 6f, 2f, 2f, p)
        // Head (crown)
        p.color = primary; c.drawRoundRect(-40f, -50f, 40f, 2f, 13f, 13f, p)
        // Crown spikes
        p.color = accent
        for (i in 0 until 7) {
            val cx2 = -30f + i * 10f
            val sH = if (i == 3) 16f else if (i == 2 || i == 4) 12f else 8f
            c.drawRoundRect(cx2 - 3, -50f - sH, cx2 + 3, -50f, 2f, 2f, p)
            p.color = Color.WHITE; p.alpha = 160; c.drawCircle(cx2, -50f - sH, 3f, p)
            p.alpha = 255; p.color = accent
        }
        p.color = dk(primary, 0.65f); c.drawRoundRect(-42f, -44f, -34f, -8f, 5f, 5f, p)
        c.drawRoundRect(34f, -44f, 42f, -8f, 5f, 5f, p)
        p.color = accent; drawGem(c, p, -38f, -26f, 6f); drawGem(c, p, 38f, -26f, 6f)
        p.color = Color.parseColor("#1A0020"); c.drawRoundRect(-30f, -40f, 30f, -12f, 8f, 8f, p)
        p.color = accent; p.style = Paint.Style.STROKE; p.strokeWidth = 1.5f
        c.drawRoundRect(-30f, -40f, 30f, -12f, 8f, 8f, p); p.style = Paint.Style.FILL
        p.color = Color.parseColor("#FFB6C1"); c.drawCircle(-15f, -28f, 9f, p)
        c.drawCircle(15f, -28f, 9f, p)
        p.color = Color.parseColor("#FF44AA"); c.drawCircle(-15f, -28f, 6f, p)
        c.drawCircle(15f, -28f, 6f, p)
        p.color = Color.WHITE; drawStar(c, p, -15f, -28f, 5f); drawStar(c, p, 15f, -28f, 5f)
        p.color = dk(primary, 0.6f); c.drawRoundRect(-26f, -14f, 26f, -2f, 5f, 5f, p)
        p.color = accent
        for (i in -2..2) c.drawRect(-22f + i * 9, -12f, -16f + i * 9, -7f, p)
        bolt(c, p, -38f, -48f); bolt(c, p, 38f, -48f)
    }

    fun drawHealthBar(
        canvas: Canvas, cx: Float, cy: Float, width: Float,
        percent: Float, robotColor: Int, label: String
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val barH = 20f; val left = cx - width / 2; val right = cx + width / 2
        // Track
        paint.color = Color.parseColor("#08080F")
        canvas.drawRoundRect(left - 3, cy - 3, right + 3, cy + barH + 3, 12f, 12f, paint)
        paint.color = Color.parseColor("#222244"); paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRoundRect(left - 3, cy - 3, right + 3, cy + barH + 3, 12f, 12f, paint)
        paint.style = Paint.Style.FILL
        // Danger bg
        paint.color = Color.parseColor("#220000")
        canvas.drawRoundRect(left, cy, right, cy + barH, 10f, 10f, paint)
        // HP fill
        val hpColor = when {
            percent > 0.6f -> Color.parseColor("#00E676")
            percent > 0.35f -> Color.parseColor("#FFEA00")
            else -> Color.parseColor("#FF1744")
        }
        val filled = left + width * percent.coerceIn(0f, 1f)
        paint.color = hpColor
        canvas.drawRoundRect(left, cy, filled, cy + barH, 10f, 10f, paint)
        // Shine
        paint.color = Color.argb(80, 255, 255, 255)
        canvas.drawRoundRect(left + 2, cy + 2, filled - 2, cy + barH / 2, 8f, 8f, paint)
        // Segment dividers
        paint.color = Color.argb(40, 0, 0, 0); paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        for (i in 1..9) canvas.drawLine(left + width * i / 10, cy, left + width * i / 10, cy + barH, paint)
        paint.style = Paint.Style.FILL
        // Label
        paint.color = Color.WHITE; paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT_BOLD; paint.textAlign = Paint.Align.CENTER
        paint.setShadowLayer(4f, 0f, 2f, Color.BLACK)
        canvas.drawText(label, cx, cy - 8f, paint); paint.clearShadowLayer()
        // HP%
        paint.textSize = 14f; paint.color = Color.argb(180, 255, 255, 255)
        canvas.drawText("${(percent * 100).toInt()}%", cx, cy + barH + 14f, paint)
    }
}
