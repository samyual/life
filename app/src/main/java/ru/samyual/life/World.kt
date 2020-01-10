package ru.samyual.life

import android.content.Context
import android.graphics.*
import android.util.Size
import kotlin.random.Random

/**
 * Класс "Мир", отвечает за отображение колонии клеток
 * @param screenSize размеры экрана в пикселях
 */
class World(context: Context, private val screenSize: Size) {

    companion object {
        // Количеств клеток в строке
        const val cellsPerLine = 50
        val arrowPaint = Paint().apply { color = Color.WHITE }
    }

    // Размеры клетки в пикселях на экране
    private val cellSize = Size(
        screenSize.width / cellsPerLine,
        screenSize.width / cellsPerLine
    )

    // Количество клеток по горизонтали и вертикали на экране
    private val cellsOnScreen = Size(
        screenSize.width / cellSize.width,
        screenSize.height / cellSize.height
    )

    // Колония клеток
    private val colony = Colony(randomColony())

    // Характеристики информационной панели
    private val infoFontSize = screenSize.height / 20f
    private val infoLeftMargin = screenSize.width / 100f

    // Стрелки направления
    private val arrowBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.arrow
    )
    private val arrowSize = Size(screenSize.height / 10, screenSize.height / 10)
    private val arrows = Arrows(arrowBitmap, arrowSize, screenSize)

    fun update() {
        colony.nextGeneration()
    }

    /**
     * Рисовать колонию
     * @param canvas холст для отрисовки
     */
    fun draw(canvas: Canvas) {

        // Рисовать фон
        canvas.drawColor(Color.WHITE)

        // Определить границы колонии в размерах клеток
        val horizontalRange = colony.horizontalRange
        val verticalRange = colony.verticalRange

        // Нарисовать стрелки, если имеются клетки за границами экрана
        if (verticalRange.first < 0) {
            arrows.draw(canvas, arrowPaint, Arrows.Direction.Up)
        }
        if (verticalRange.last > cellsOnScreen.height) {
            arrows.draw(canvas, arrowPaint, Arrows.Direction.Down)
        }
        if (horizontalRange.first < 0) {
            arrows.draw(canvas, arrowPaint, Arrows.Direction.Left)
        }
        if (horizontalRange.last > cellsOnScreen.width) {
            arrows.draw(canvas, arrowPaint, Arrows.Direction.Right)
        }

        // Нарисовать все живые клетки, попадающие в границы холста
        colony.filter {
            it.key.x in 0 until cellsOnScreen.width && it.key.y in 0 until cellsOnScreen.height
        }.forEach { (point, cell) ->
            cell.draw(canvas, point, cellSize)
        }

        drawInfoPanel(canvas)
    }

    private fun drawInfoPanel(canvas: Canvas) {
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
        (1..cellsPerLine * 10).forEach { _ ->
            addressList += Point(
                Random.nextInt(cellsOnScreen.width),
                Random.nextInt(cellsOnScreen.height)
            )
        }
        return addressList
    }
}