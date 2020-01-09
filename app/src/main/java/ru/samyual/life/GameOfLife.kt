package ru.samyual.life

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MASK
import android.view.MotionEvent.ACTION_UP
import android.view.SurfaceView

class GameOfLife(context: Context, screenSize: Point) : SurfaceView(context) {

    companion object {
        // Количество клеток, отображаемых в строке
        const val cellsPerLine = 50
    }

    // Размеры клеток на экране
    private val cellSize = Point().apply { x = screenSize.x / cellsPerLine; y = x }

    // Размер шрифта (5% от высоты экрана)
    private val fontSize: Float = screenSize.y / 20f

    // Колония клеток
    private val colony = Colony(
        cellSize,
        listOf(
            Point(10, 10),
            Point(10, 11),
            Point(10, 12)
        )
    )

    // Обработка нажатий
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {

            // Пользователь убрал палец с экрана
            if (it.action and ACTION_MASK == ACTION_UP) {
                draw()
                colony.nextGeneration()
            }
        }
        return true
    }

    // Рисовать колонию и информационную панель
    private fun draw() {
        if (holder.surface.isValid) {

            // Блокировать перерисовку
            val canvas = holder.lockCanvas()

            // Отрисовать колонию
            colony.draw(canvas)

            // Отрисовать инфопанель
            val paint = Paint().apply {
                color = Color.BLUE
                textSize = fontSize
            }
            canvas.drawText("Generation ${colony.generation}", 10f, fontSize, paint)

            // Разблокировать и нарисовать
            holder.unlockCanvasAndPost(canvas)
        }
    }
}