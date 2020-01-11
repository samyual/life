package ru.samyual.life

import android.content.Context
import android.graphics.*
import android.util.Size
import kotlin.random.Random

/**
 * Класс "Мир", отвечает за отображение колонии клеток
 * @param screenSize размеры экрана в пикселях
 */
class World(private val context: Context, private val screenSize: Size) {

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

    // Стрелки, указывающие на наличие клеток за границей экрана
    private val arrowBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources, R.drawable.arrow
    )
    private val arrowSize = Size(screenSize.height / 10, screenSize.height / 10)
    private val arrows = Arrows(arrowBitmap, arrowSize, screenSize)


    // Перенести окно просмотра на указанное число клеток
    // Коэффициент 2 нужен для "оживления" прокрутки,
    // подобран опытным путём ;)
    fun moveOn(distanceX: Float, distanceY: Float) {
        // Вычислить координаты окна
        viewport.apply {
            top += (2 * distanceY / cellSize.height).toInt()
            bottom = top + cellsOnScreen.height
            left += (2 * distanceX / cellSize.width).toInt()
            right = left + cellsOnScreen.width
        }

        // Проверить выход за границу колонии
        // Важен порядок проверки границ!
        with(viewport) {
            if (bottom > colony.verticalRange.last) {
                bottom = colony.verticalRange.last
                top = bottom - cellsOnScreen.height
            }
            if (top < colony.verticalRange.first) {
                top = colony.verticalRange.first
                bottom = top + cellsOnScreen.height
            }
            if (right > colony.horizontalRange.last) {
                right = colony.horizontalRange.last
                left = right - cellsOnScreen.width
            }
            if (left < colony.horizontalRange.first) {
                left = colony.horizontalRange.first
                right = left + cellsOnScreen.width
            }
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
        // Стрелки сигнализируют, что имеются клетки за границами окна
        if (verticalRange.first < viewport.top) {
            arrows.draw(canvas, Arrows.Direction.Up)
        }
        if (verticalRange.last > viewport.bottom) {
            arrows.draw(canvas, Arrows.Direction.Down)
        }
        if (horizontalRange.first < viewport.left) {
            arrows.draw(canvas, Arrows.Direction.Left)
        }
        if (horizontalRange.last > viewport.right) {
            arrows.draw(canvas, Arrows.Direction.Right)
        }

        colony.draw(canvas, viewport, cellSize)

        drawInformation(canvas)
    }

    // Вывести надпись "ПАУЗА"
    fun drawPause(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.RED
            textSize = infoFontSize * 2
        }
        canvas.drawText(
            context.getString(R.string.pause),
            infoLeftMargin, infoFontSize * 6, paint
        )
    }

    // Вывести информационную панель
    private fun drawInformation(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            textSize = infoFontSize
        }
        canvas.drawText(
            context.getString(R.string.generation_number, colony.generation),
            infoLeftMargin, infoFontSize, paint
        )
        canvas.drawText(
            context.getString(R.string.live_cells, colony.size),
            infoLeftMargin, infoFontSize * 2, paint
        )
        canvas.drawText(
            context.getString(
                R.string.area,
                colony.horizontalRange.last - colony.horizontalRange.first + 1,
                colony.verticalRange.last - colony.verticalRange.first - 1
            ),
            infoLeftMargin, infoFontSize * 3, paint
        )
        canvas.drawText(
            context.getString(
                R.string.viewport,
                viewport.left, viewport.right, viewport.top, viewport.bottom
            ),
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