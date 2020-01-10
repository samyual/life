package ru.samyual.life

import android.graphics.*

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
     * @param address адрес клетки
     * @param size размеры клетки на холсте
     */
    fun draw(canvas: Canvas, address: Point, size: Point) {

        // Вычислить положение и размеры клетки
        val rect = RectF().apply {
            left = (address.x * size.x).toFloat()
            right = left + size.x
            top = (address.y * size.y).toFloat()
            bottom = top + size.y
        }

        paint.color = if (age > 0) Color.BLACK else Color.GRAY

        canvas.drawOval(rect, paint)
    }
}