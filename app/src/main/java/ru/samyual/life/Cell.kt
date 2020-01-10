package ru.samyual.life

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * Живая клетка
 */
class Cell {

    private val paint = Paint()

    private var age = 0L

    // Увеличить возраст клетки
    fun grow(): Cell {
        age += 1
        return this
    }

    /**
     * Рисовать клетку
     * @param canvas заданный холст
     * @param boundRect положение на холсте
     */
    fun draw(canvas: Canvas, boundRect: RectF) {

        paint.color = if (age > 0) Color.BLACK else Color.GRAY

        canvas.drawOval(boundRect, paint)
    }
}