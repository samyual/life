package ru.samyual.life

import android.graphics.*

/**
 * Живая клетка
 */
class Cell(private val borned: Long) {

    private val paint = Paint()

    init {
        paint.color = when (borned % 7) {
            0L -> Color.RED
            1L -> Color.GRAY
            2L -> Color.YELLOW
            3L -> Color.GREEN
            4L -> Color.CYAN
            5L -> Color.BLUE
            6L -> Color.MAGENTA
            else -> Color.BLACK
        }
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

        canvas.drawOval(rect, paint)
    }
}