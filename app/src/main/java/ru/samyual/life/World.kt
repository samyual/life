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

    // Количеств клеток в строке
    private val cellsPerLine = 50

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

    // Координаты окна просмотра колонии (в клетках)
    private val viewport: Rect = Rect(
        0, 0, cellsOnScreen.width, cellsOnScreen.width
    )

    // Колония клеток
    private val colony = Colony(randomColony(100, 100))

    // Характеристики информационной панели
    private val infoFontSize = screenSize.height / 20f
    private val infoLeftMargin = screenSize.width / 100f

    // Стрелки направления
    private val arrowBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.arrow
    )
    private val arrowSize = Size(screenSize.height / 10, screenSize.height / 10)
    private val arrows = Arrows(arrowBitmap, arrowSize, screenSize)


    // Перенести окно просмотра на указанное число клеток
    // Коэффициент 2 нужен для "оживления" прокрутки
    fun moveOn(distanceX: Float, distanceY: Float) {
        viewport.apply {
            top += (2 * distanceY / cellSize.height).toInt()
            bottom = top + cellsOnScreen.height
            left += (2 * distanceX / cellSize.width).toInt()
            right = left + cellsOnScreen.width
        }
    }

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
        if (verticalRange.first < viewport.top) {
            arrows.draw(canvas, Arrows.Direction.Down)
        }
        if (verticalRange.last > viewport.bottom) {
            arrows.draw(canvas, Arrows.Direction.Up)
        }
        if (horizontalRange.first < viewport.left) {
            arrows.draw(canvas, Arrows.Direction.Right)
        }
        if (horizontalRange.last > viewport.right) {
            arrows.draw(canvas, Arrows.Direction.Left)
        }

        colony.draw(canvas, viewport, cellSize)

        drawInformation(canvas)
    }

    private fun drawInformation(canvas: Canvas) {
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
        canvas.drawText(
            "Area ${colony.horizontalRange.last - colony.horizontalRange.first + 1} x " +
                    "${colony.verticalRange.last - colony.verticalRange.first - 1}",
            infoLeftMargin, infoFontSize * 3, paint
        )
        canvas.drawText(
            "View ${viewport.left}:${viewport.right}-${viewport.top}:${viewport.bottom}",
            infoLeftMargin, infoFontSize * 4, paint
        )
    }

    private fun randomColony(width: Int, height: Int): List<Position> {
        val addressList = mutableListOf<Position>()
        (0..(width * height * 0.25).toInt()).forEach { _ ->
            addressList += Position(
                x = Random.nextInt(width),
                y = Random.nextInt(height)
            )
        }
        return addressList
    }
}