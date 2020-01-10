package ru.samyual.life

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.SurfaceView

class GameOfLife(context: Context, private val screenSize: Point) : SurfaceView(context), Runnable {

    companion object {

        // Количество кадров в секунду (один кадр = одно поколение)
        private const val targetFPS: Long = 2

        // Количество миллисекунд в секунде
        private const val millisPerSecond: Long = 1_000

    }

    // Размер шрифта (5% от высоты экрана)
    private val fontSize: Float = screenSize.y / 20f

    // Мир
    private val world = World(screenSize)

    // Признак паузы игры
    private var isPaused = false

    // Игра в процессе
    private var isPlaying = false

    // Тред
    private lateinit var thread: Thread

    // Время для отображение следующего кадра
    private var timeOfNextFrame: Long = 0

    // Обработка нажатий
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        event?.let {

            when (event.action and ACTION_MASK) {

                // Пользователь нажал пальцем на экран
                ACTION_DOWN -> isPaused = true

                // Пользователь убрал палец от экрана
                ACTION_UP -> isPaused = false
            }
        }

        return true
    }

    // Цикл игры
    override fun run() {
        while (isPlaying) {

            if (!isPaused) {
                if (updateRequired()) {
                    world.update()
                }
            }

            draw()
        }
    }

    /**
     * Снять игру с паузы
     */
    fun resume() {
        isPlaying = true
        timeOfNextFrame = System.currentTimeMillis()
        thread = Thread(this)
        thread.start()
    }

    /**
     * Поставить игру на паузу
     */
    fun pause() {
        isPlaying = false
        try {
            thread.join()
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

    // Рисовать колонию и информационную панель
    private fun draw() {

        if (holder.surface.isValid) {

            // Блокировать перерисовку
            val canvas = holder.lockCanvas()

            // Отрисовать колонию
            world.draw(canvas)

            // Игра на паузе
            if (isPaused) {

                // Отрисовать заставку
                drawSplash(canvas)
            }

            // Разблокировать и нарисовать
            holder.unlockCanvasAndPost(canvas)
        }
    }

    // Нарисовать заставку
    private fun drawSplash(canvas: Canvas) {

        val paint = Paint().apply {
            textSize = screenSize.x / 20f
            color = Color.BLUE
        }
        canvas.drawText("Pause", 20f, screenSize.y / 20f * 10.5f, paint)
    }
}