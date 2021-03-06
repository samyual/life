package ru.samyual.life

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_MASK
import android.view.MotionEvent.ACTION_UP
import android.view.SurfaceView

@SuppressLint("ViewConstructor")
class GameOfLife(context: Context, screenSize: Point) :
    SurfaceView(context),
    Runnable {

    companion object {

        // Количество кадров в секунду (один кадр = одно поколение)
        private const val targetFPS: Long = 5

        // Количество миллисекунд в секунде
        private const val millisPerSecond: Long = 1_000

    }

    // Мир
    private val world = World(context, Size(screenSize.x, screenSize.y))

    // Признак паузы игры
    private var isPaused = false

    // Игра в процессе
    private var isPlaying = false

    // Тред
    private lateinit var thread: Thread

    // Время для отображение следующего кадра
    private var timeOfNextFrame: Long = 0

    inner class WorldGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.d("DEBUG", "onScroll(distanceX=$distanceX, distanceY=$distanceY")
            world.moveOn(distanceX, distanceY)
            return true
        }
    }

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, WorldGestureListener())
    }

    // Обработка нажатий
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and ACTION_MASK) {

            // При поднятии пальца снять с паузы
            ACTION_UP -> isPaused = false

            // При скролле поставить на паузу
            else -> if (gestureDetector.onTouchEvent(event)) {
                isPaused = true
                return true
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
                world.drawPause(canvas)
            }

            // Разблокировать и нарисовать
            holder.unlockCanvasAndPost(canvas)
        }
    }
}