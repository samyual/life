package ru.samyual.life

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect

/**
 * Колония клеток, развивающася по классическим правилам
 * игры "Жизнь" Конвея
 * @param cellSize размер ячейки в пикселях для отрисовки на холсте
 * @param initial начальный список адресов живых ячеек
 */
class Colony(private val cellSize: Point, initial: List<Point> = listOf()) {

    // Поколение колонии
    var generation: Long = 1
        private set

    val size: Int
        get() = cells.size

    // Хранилище живых клеток
    private val cells = mutableMapOf<Point, Cell>()

    // Возвращает диапазон адресов по горизонтали, к которым необходимо
    // применить правила жизни
    private val horizontalRange: IntRange
        get() = getHorizontalMinMax()

    // То же самое по вертикали
    private val verticalRange: IntRange
        get() = getVerticalMinMax()

    init {
        initial.forEach {
            cells[it] = Cell(generation)
        }
    }

    /**
     *  Позволяет обращаться к отдельным клеткам колонии как this[x,y]
     *  Пустые клетки возвращаются как <b>null</b>
     */
    operator fun get(x: Int, y: Int): Cell? {
        return cells[Point(x, y)]
    }

    /**
     *  Позволяет выполнить присваивание клеток в виде this[x,y] = Cell(generation),
     *  a также удалять клетки из колонии с помощью выражения this[x, y] = null
     */
    operator fun set(x: Int, y: Int, cell: Cell?) {
        if (cell != null) {
            cells[Point(x, y)] = cell
        } else {
            cells -= Point(x, y)
        }
    }

    /**
     * Выполнить вычисление нового поколения колонии
     * по классическим правилам Конвея
     */
    fun nextGeneration() {
        generation += 1

        // Колония следующего поколения
        val nextColony = mutableMapOf<Point, Cell>()

        for (x in horizontalRange) {
            for (y in verticalRange) {
                val address = Point(x, y)
                when (this[x, y]) {

                    // Пустая клетка
                    null -> {
                        // В пустой клетке с тремя соседями зарождается жизнь
                        if (numberOfNeighbors(x, y) == 3) {
                            nextColony[address] = Cell(generation)
                        }
                    }

                    // Живая клетка
                    else -> {
                        if (numberOfNeighbors(x, y) in 2..3) {
                            nextColony[address] = this[x, y]!!
                        }
                    }
                }
            }
        }
        cells.clear()
        cells.putAll(nextColony)
    }

    private fun getHorizontalMinMax(): IntRange {
        return if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val minX = cells.keys.reduce { acc, point -> if (acc.x < point.x) acc else point }.x
            val maxX = cells.keys.reduce { acc, point -> if (acc.x > point.x) acc else point }.x
            minX - 1..maxX + 1
        }
    }

    private fun getVerticalMinMax(): IntRange {
        return if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val minY = cells.keys.reduce { acc, point -> if (acc.y < point.y) acc else point }.y
            val maxY = cells.keys.reduce { acc, point -> if (acc.y > point.y) acc else point }.y
            minY - 1..maxY + 1
        }
    }

    /**
     * Рисовать колонию
     * @param canvas холст для отрисовки
     */
    fun draw(canvas: Canvas) {

        // Вычислить граница колонии клеток, попадающих в границы холста
        val bounds: Rect = canvas.clipBounds.apply {
            left /= cellSize.x
            right /= cellSize.x
            top /= cellSize.y
            bottom /= cellSize.y
        }

        // Рисовать фон
        canvas.drawColor(Color.WHITE)

        // Рисовать все живые клетки, попадающие в заданные границы
        cells
            .filter {
                it.key.x in bounds.left until bounds.right &&
                        it.key.y in bounds.top until bounds.bottom
            }
            .forEach { (point, cell) ->
                cell.draw(canvas, point, cellSize)
            }
    }

    /**
     * Возвратить количество соседних живых клеток
     * @param xPos адрес клетки в колонии по горизонтали
     * @param yPos адрес клетки в колонии по вертикали
     * @return количество соседей
     */
    private fun numberOfNeighbors(xPos: Int, yPos: Int): Int {
        var number = 0
        // Подсчитать живые клетки в квадрате 3х3
        for (x in xPos - 1..xPos + 1) {
            for (y in yPos - 1..yPos + 1) {
                if (this[x, y] != null) {
                    number += 1
                }
            }
        }
        // Если клетка живая, вернуть на одну меньше
        return if (this[xPos, yPos] != null) number - 1 else number
    }
}