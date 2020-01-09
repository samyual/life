package ru.samyual.life

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MASK
import android.view.MotionEvent.ACTION_UP
import android.view.SurfaceView

class GameOfLife(context: Context, private val screenSize: Point) : SurfaceView(context), Runnable {

    companion object {

        // Количество клеток, отображаемых в строке
        private const val cellsPerLine = 50

        // Количество кадров в секунду (один кадр = одно поколение)
        private const val targetFPS: Long = 2

        // Количество миллисекунд в секунде
        private const val millisPerSecond: Long = 1_000
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

    // Признак останова игры
    private var isPaused = true

    // Тред
    private var thread: Thread? = null

    // Время для отображение следующего кадра
    private var timeOfNextFrame: Long = 0

    // Обработка нажатий
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {

            // Пользователь убрал палец с экрана
            if (event.action and ACTION_MASK == ACTION_UP) {
                isPaused = !isPaused
            }
        }

        return true
    }

    // Цикл игры
    override fun run() {
        if (!isPaused) {
            if (updateRequired()) {
                update()
                draw()
            }
        }
    }

    /**
     * Запуск игры
     */
    fun resume() {
        isPaused = false
        timeOfNextFrame = System.currentTimeMillis()
        thread = Thread(this)
        thread!!.start()
    }

    /**
     * Поставить игру на паузу
     */
    fun pause() {
        isPaused = true
        try {
            thread?.join()
        } catch (e: InterruptedException) {
            Log.e("ERROR", "Thread not joined")
        }
    }

    // Определить, есть ли необходимость отрисовки следующего кадра
    private fun updateRequired(): Boolean {

        // Время следующего кадра ещё не наступило
        if (timeOfNextFrame > System.currentTimeMillis()) {
            return false
        }

        // Да, наступило, назначить время для следующего кадра
        timeOfNextFrame = System.currentTimeMillis() + millisPerSecond / targetFPS
        return true
    }

    // Внести изменения в колонию
    private fun update() {
        colony.nextGeneration()
    }

    // Рисовать колонию и информационную панель
    private fun draw() {
        if (holder.surface.isValid) {

            // Блокировать перерисовку
            val canvas = holder.lockCanvas()

            if (isPaused) {

                // Отрисовать заставку
                drawSplash(canvas)
            } else {

                // Отрисовать колонию
                colony.draw(canvas)

                // Отрисовать информационную панель
                drawInfo(canvas)

            }

            // Разблокировать и нарисовать
            holder.unlockCanvasAndPost(canvas)
        }
    }

    // Нарисовать информационную панель
    private fun drawInfo(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            textSize = fontSize
        }
        canvas.drawText("Generation ${colony.generation}", 10f, fontSize, paint)
    }

    // Нарисовать заставку
    private fun drawSplash(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)

        val paint = Paint().apply {
            textSize = screenSize.x / 10f
            color = Color.BLACK
        }
        canvas.drawText("Conway's Life", 20f, screenSize.y / 10f * 5.5f, paint)
    }
}