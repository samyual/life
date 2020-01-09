package ru.samyual.life

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

class LifeActivity : Activity() {

    private lateinit var game: GameOfLife

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Экран не должен отключаться
        window.addFlags(FLAG_KEEP_SCREEN_ON)

        // Определяем размер экрана
        val display = windowManager.defaultDisplay
        val screenSize = Point()
        display.getSize(screenSize)

        game = GameOfLife(this, screenSize)
        setContentView(game)
    }

    override fun onResume() {
        super.onResume()
        game.resume()
    }

    override fun onPause() {
        super.onPause()
        game.pause()
    }
}
