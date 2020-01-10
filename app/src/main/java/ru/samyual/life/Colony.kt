package ru.samyual.life

import android.graphics.Point

/**
 * Колония клеток, развивающаяся по классическим правилам
 * игры "Жизнь" Конвея
 */
class Colony(initial: List<Point>) {

    // Хранилище клеток (только живых!)
    private var cells = mutableMapOf<Point, Cell>()

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

    // диапазон между минимальной и максимальной позициями клеток по горизонтали
    val horizontalRange: IntRange
        get() = if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val first = cells.keys.reduce { min, address ->
                if (min.x < address.x) min else address
            }.x
            val last = cells.keys.reduce { max, address ->
                if (max.x > address.x) max else address
            }.x
            first - 1..last + 1
        }

    // диапазон между минимальной и максимальной позициями клеток по вертикали
    val verticalRange: IntRange
        get() = if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val first = cells.keys.reduce { min, address ->
                if (min.y < address.y) min else address
            }.y
            val last = cells.keys.reduce { max, address ->
                if (max.y > address.y) max else address
            }.y
            first - 1..last + 1
        }

    operator fun get(x: Int, y: Int): Cell? = cells[Point(x, y)]

    operator fun set(x: Int, y: Int, cell: Cell?) {
        if (cell != null) {
            cells[Point(x, y)] = cell
        } else {
            cells.remove(Point(x, y))
        }
    }

    fun filter(predicate: (Map.Entry<Point, Cell>) -> Boolean): Map<Point, Cell> {
        return cells.filter { predicate(it) }
    }

    // Вычислить следующее поколение клеток
    fun nextGeneration() {
        generation += 1

        val newGeneration = mutableMapOf<Point, Cell>()

        for (x in horizontalRange) {
            for (y in verticalRange) {
                when (this[x, y]) {
                    null -> if (neighbors(x, y) == 3) {
                        newGeneration[Point(x, y)] = Cell()
                    }
                    else -> if (neighbors(x, y) in 2..3) {
                        newGeneration[Point(x, y)] = this[x, y]!!.grow()
                    }
                }
            }
        }

        cells = newGeneration
    }

    // Подсчет числа соседей у клетки с адресом row, col
    private fun neighbors(row: Int, col: Int): Int {
        // Число соседей
        var number = 0
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (this[r, c] != null) number += 1
            }
        }
        // Если клетка живая, убрать её из числа соседей
        return if (this[row, col] != null) number - 1 else number
    }
}