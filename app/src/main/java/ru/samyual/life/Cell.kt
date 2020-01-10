package ru.samyual.life

import android.graphics.*
import android.util.Size

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
     * @param position адрес клетки
     * @param size размеры клетки на холсте
     */
    fun draw(canvas: Canvas, position: Point, size: Size) {

        // Вычислить положение и размеры клетки
        val rect = RectF().apply {
            left = (position.x * size.width).toFloat()
            right = left + size.width
            top = (position.y * size.height).toFloat()
            bottom = top + size.height
        }

        paint.color = if (age > 0) Color.BLACK else Color.GRAY

        canvas.drawOval(rect, paint)
    }
}