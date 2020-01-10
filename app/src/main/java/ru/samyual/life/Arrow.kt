package ru.samyual.life

import android.graphics.*

class Arrow(direction: Direction, screenSize: Point) {

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    companion object {
        const val HEIGHT_PART = 0.05f
    }

    // Координаты на экране
    private val rect: RectF

    private val paint = Paint().apply {
        color = Color.argb(64, 128, 0, 0)
    }

    init {
        val arrowSize = screenSize.y * HEIGHT_PART
        rect = RectF().apply {
            when (direction) {
                Direction.UP -> {
                    top = 0f
                    bottom = arrowSize
                    left = (screenSize.x - arrowSize) / 2f
                    right = (screenSize.x + arrowSize) / 2f
                }
                Direction.DOWN -> {
                    top = screenSize.y - arrowSize
                    bottom = screenSize.y.toFloat()
                    left = (screenSize.x - arrowSize) / 2f
                    right = (screenSize.x + arrowSize) / 2f
                }
                Direction.LEFT -> {
                    top = (screenSize.y - arrowSize) / 2f
                    bottom = (screenSize.y + arrowSize) / 2f
                    left = 0f
                    right = arrowSize
                }
                Direction.RIGHT -> {
                    top = (screenSize.y - arrowSize) / 2f
                    bottom = (screenSize.y + arrowSize) / 2f
                    left = screenSize.x - arrowSize
                    right = screenSize.x.toFloat()
                }
            }
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawOval(rect, paint)
    }
}