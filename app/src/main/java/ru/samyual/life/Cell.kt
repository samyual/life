package ru.samyual.life

import android.graphics.*

/**
 * Живая клетка
 */
class Cell(private val generation: Long) {

    private val paint = Paint().apply {
        color = Color.DKGRAY
    }

    /**
     * Рисовать клетку
     * @param canvas заданный холст
     * @param address адрес клетки
     * @param size размеры клетки на холсте
     */
    fun draw(canvas: Canvas, address: Point, size: Point) {
        val rect = RectF().apply {
            left = (address.x * size.x).toFloat()
            right = left + size.x
            top = (address.y * size.y).toFloat()
            bottom = top + size.y
        }
        canvas.drawOval(rect, paint)
    }
}