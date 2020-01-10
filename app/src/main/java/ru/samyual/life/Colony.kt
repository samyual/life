package ru.samyual.life

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.Size

data class Position(val x: Int, val y: Int)

/**
 * Колония клеток, развивающаяся по классическим правилам
 * игры "Жизнь" Конвея
 */
class Colony(initial: List<Position>) {

    // Хранилище клеток (только живых!)
    private var cells = mutableMapOf<Position, Cell>()

    init {
        initial.forEach {
            cells[it] = Cell()
        }
    }

    // Поколение колонии
    var generation: Long = 1
        private set

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

        val newGeneration = mutableMapOf<Position, Cell>()

        for (x in horizontalRange) {
            for (y in verticalRange) {
                when (cells[Position(x, y)]) {
                    null -> if (neighbours(Position(x, y)) == 3) {
                        newGeneration[Position(x, y)] = Cell()
                    }
                    else -> if (neighbours(Position(x, y)) in 2..3) {
                        newGeneration[Position(x, y)] = cells[Position(x, y)]!!.grow()
                    }
                }
            }
        }

        cells = newGeneration
        horizontalRange = calculateHorizontalRange()
        verticalRange = calculateVerticalRange()
    }

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
                cell.draw(canvas, rect)
            }
    }

    // Подсчет числа соседей у клетки с адресом row, col
    private fun neighbours(pos: Position): Int {
        // Число соседей
        var number = 0
        for (xNeighbour in pos.x - 1..pos.x + 1) {
            for (yNeighbour in pos.y - 1..pos.y + 1) {
                if (cells[Position(xNeighbour, yNeighbour)] != null) number += 1
            }
        }
        // Если клетка живая, убрать её из числа соседей
        return if (cells[pos] != null) number - 1 else number
    }
}