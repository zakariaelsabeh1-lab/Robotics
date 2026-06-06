package com.zakariaelsabeh.robofight.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.zakariaelsabeh.robofight.data.GameData
import com.zakariaelsabeh.robofight.data.models.Robot
import com.zakariaelsabeh.robofight.ui.battle.RobotRenderer

class RobotIconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var robot: Robot? = null
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setRobotId(id: Int) {
        robot = try { GameData.getRobotById(id) } catch (_: Exception) { null }
        invalidate()
    }

    fun setRobot(r: Robot) {
        robot = r
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = robot ?: return
        val cx = width / 2f
        val cy = height / 2f
        val scale = minOf(width, height) / 220f
        RobotRenderer.drawRobot(canvas, r, cx, cy, scale = scale)
    }
}
