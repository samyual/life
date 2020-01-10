package ru.samyual.life

import android.graphics.*
import kotlin.random.Random

/**
 * Класс "Мир", отвечает за отображение колонии клеток
 * @param screenSize размеры экрана в пикселях
 */
class World(screenSize: Point) {

    companion object {
        const val CELLS_PER_LINE = 50
    }

    // Размеры клетки в пикселях на экране
    private val cellSize = Point().apply {
        x = screenSize.x / CELLS_PER_LINE
        y = screenSize.x / CELLS_PER_LINE
    }

    // Количество клеток по горизонтали и вертикали на экране
    private val cellsOnScreen = Point().apply {
        x = screenSize.x / cellSize.x
        y = screenSize.y / cellSize.y
    }

    // Колония клеток
    private val colony = Colony(randomColony())

    private val infoFontSize = screenSize.y / 20f
    private val infoLeftMargin = screenSize.x / 100f

    // Стрелки направления
    private val arrows = mapOf(
        Arrow.Direction.UP to Arrow(Arrow.Direction.UP, screenSize),
        Arrow.Direction.DOWN to Arrow(Arrow.Direction.DOWN, screenSize),
        Arrow.Direction.LEFT to Arrow(Arrow.Direction.LEFT, screenSize),
        Arrow.Direction.RIGHT to Arrow(Arrow.Direction.RIGHT, screenSize)
    )

    fun update() {
        colony.nextGeneration()
    }

    /**
     * Рисовать колонию
     * @param canvas холст для отрисовки
     */
    fun draw(canvas: Canvas) {

        // Вычислить граница колонии клеток, попадающих в границы холста
        // (в клетках)
        val bounds: Rect = canvas.clipBounds.apply {
            left /= cellSize.x
            right /= cellSize.x
            top /= cellSize.y
            bottom /= cellSize.y
        }

        // Рисовать фон
        canvas.drawColor(Color.WHITE)

        // Вычислить границы холста в размерах клетки
        val horizontalBounds = bounds.left until bounds.right
        val verticalBounds = bounds.top until bounds.bottom

        // Нарисовать стрелки, если имеются клетки за границами экрана
        if (colony.verticalRange.first < verticalBounds.first) {
            arrows[Arrow.Direction.UP]?.draw(canvas)
        }
        if (colony.verticalRange.last > verticalBounds.last) {
            arrows[Arrow.Direction.DOWN]?.draw(canvas)
        }
        if (colony.horizontalRange.first < horizontalBounds.first) {
            arrows[Arrow.Direction.LEFT]?.draw(canvas)
        }
        if (colony.horizontalRange.last > horizontalBounds.last) {
            arrows[Arrow.Direction.RIGHT]?.draw(canvas)
        }

        // Нарисовать все живые клетки, попадающие в границы холста
        colony.filter {
            it.key.x in horizontalBounds && it.key.y in verticalBounds
        }.forEach { (point, cell) ->
            cell.draw(canvas, point, cellSize)
        }

        drawInfoPanel(canvas)
    }

    fun drawInfoPanel(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            textSize = infoFontSize
        }
        canvas.drawText(
            "Generation ${colony.generation}",
            infoLeftMargin, infoFontSize, paint
        )
        canvas.drawText(
            "Live cells ${colony.size}",
            infoLeftMargin, infoFontSize * 2, paint
        )
    }

    private fun randomColony(): List<Point> {
        val addressList = mutableListOf<Point>()
        (1..CELLS_PER_LINE * 10).forEach { _ ->
            addressList += Point(Random.nextInt(cellsOnScreen.x), Random.nextInt(cellsOnScreen.y))
        }
        return addressList
    }
}