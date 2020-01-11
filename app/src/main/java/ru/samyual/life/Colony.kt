package ru.samyual.life

import android.graphics.*
import android.util.Size

data class Position(val x: Int, val y: Int)

typealias Age = Long

/**
 * Колония клеток, развивающаяся по классическим правилам
 * игры "Жизнь" Конвея
 */
class Colony(initial: List<Position>) {

    // Хранилище клеток (только живых!)
    private var cells = mutableMapOf<Position, Age>()

    operator fun get(x: Int, y: Int): Age? = cells[Position(x, y)]

    init {
        initial.forEach {
            cells[it] = generation
        }
    }

    // Поколение колонии
    var generation: Age = 1
        private set

    private val cellPaint = Paint()

    /**
     *  Размер колонии (количество живых клеток)
     */
    val size: Int
        get() = cells.size

    // Диапазон колонок для перебора
    var horizontalRange: IntRange = calculateHorizontalRange()
        private set

    private fun calculateHorizontalRange(): IntRange {
        return if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val first = cells.keys.reduce { min, compared ->
                if (min.x < compared.x) min else compared
            }.x
            val last = cells.keys.reduce { max, address ->
                if (max.x > address.x) max else address
            }.x
            first - 1..last + 1
        }
    }

    // Диапазон строк для перебора
    var verticalRange: IntRange = calculateVerticalRange()
        private set

    private fun calculateVerticalRange(): IntRange {
        return if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val first = cells.keys.reduce { min, compared ->
                if (min.y < compared.y) min else compared
            }.y
            val last = cells.keys.reduce { max, address ->
                if (max.y > address.y) max else address
            }.y
            first - 1..last + 1
        }
    }

    // Вычислить следующее поколение клеток
    fun nextGeneration() {
        generation += 1

        val newGeneration = mutableMapOf<Position, Age>()

        for (x in horizontalRange) {
            for (y in verticalRange) {
                if (this[x, y] == null) {
                    if (neighbours(x, y) == 3) {
                        newGeneration[Position(x, y)] = generation
                    }
                } else {
                    if (neighbours(x, y) in 2..3) {
                        newGeneration[Position(x, y)] = this[x, y]!!
                    }
                }
            }
        }

        cells = newGeneration
        horizontalRange = calculateHorizontalRange()
        verticalRange = calculateVerticalRange()
    }

    /**
     * Нарисовать живые клетки колонии, попадающие в окно просмотра
     *
     * @param canvas холст
     * @param viewport размеры окна просмотра (в клетках)
     * @param size размеры клетки
     */
    fun draw(canvas: Canvas, viewport: Rect, size: Size) {
        cells
            // отфильтровать клетки, попадающие в форточку
            .filterKeys { pos ->
                pos.x in viewport.left..viewport.right &&
                        pos.y in viewport.top..viewport.bottom
            }
            // нарисовать клетку с учётом форточки
            .forEach { (pos, cell) ->
                // вычислить границы отрисовки клетки
                val rect = RectF().apply {
                    left = ((pos.x - viewport.left) * size.width).toFloat()
                    right = left + size.width
                    top = ((pos.y - viewport.top) * size.height).toFloat()
                    bottom = top + size.height
                }
                // Чем старше клетка, тем темнее
                cellPaint.color = when (generation - cell) {
                    0L -> Color.LTGRAY
                    1L -> Color.GRAY
                    2L -> Color.DKGRAY
                    else -> Color.BLACK
                }
                canvas.drawOval(rect, cellPaint)
            }
    }

    // Подсчет числа соседей у клетки с адресом row, col
    private fun neighbours(posX: Int, posY: Int): Int {
        // Число соседей
        var number = 0
        (posX - 1..posX + 1).forEach { xPos ->
            (posY - 1..posY + 1).forEach { yPos ->
                if (this[xPos, yPos] != null) number += 1
            }
        }
        // Если клетка живая, убрать её из числа соседей
        return if (this[posX, posY] != null) number - 1 else number
    }
}