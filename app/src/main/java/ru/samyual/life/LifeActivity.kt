package ru.samyual.life

import android.app.Activity
import android.graphics.Point
import android.os.Bundle

class LifeActivity : Activity() {

    private lateinit var game: GameOfLife

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Определяем размер экрана
        val display = windowManager.defaultDisplay
        val screenSize = Point()
        display.getSize(screenSize)

        game = GameOfLife(this, screenSize)
        setContentView(game)
    }
}
