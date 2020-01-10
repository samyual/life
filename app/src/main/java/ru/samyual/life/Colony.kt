package ru.samyual.life

import android.graphics.*

/**
 * Колония клеток, развивающася по классическим правилам
 * игры "Жизнь" Конвея
 * @param screenSize размеры экрана в пикселях
 * @param initial начальный список адресов живых ячеек
 */
class Colony(screenSize: Point, initial: List<Point> = listOf()) {

    companion object {
        const val CELLS_PER_LINE = 50
    }

    // Поколение колонии
    var generation: Long = 1
        private set

    // Размер колонии (количество живых клеток)
    val size: Int
        get() = cells.size

    // Хранилище живых клеток
    private val cells = mutableMapOf<Point, Cell>()

    // Следующее поколение клеток
    private val newCells = mutableMapOf<Point, Cell>()

    // Возвращает диапазон адресов по горизонтали, к которым необходимо
    // применить правила жизни
    private val horizontalRange: IntRange
        get() = getHorizontalMinMax()

    // То же самое по вертикали
    private val verticalRange: IntRange
        get() = getVerticalMinMax()

    // Размеры клетки в пикселях на экране
    private val cellSize: Point

    // Стрелки направления
    private val arrows = mapOf(
        Arrow.Direction.UP to Arrow(Arrow.Direction.UP, screenSize),
        Arrow.Direction.DOWN to Arrow(Arrow.Direction.DOWN, screenSize),
        Arrow.Direction.LEFT to Arrow(Arrow.Direction.LEFT, screenSize),
        Arrow.Direction.RIGHT to Arrow(Arrow.Direction.RIGHT, screenSize)
    )

    init {
        cellSize = Point().apply {
            x = screenSize.x / CELLS_PER_LINE
            y = screenSize.x / CELLS_PER_LINE
        }
        initial.forEach {
            cells[it] = Cell()
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

        // Очистить колонию следующего поколения
        newCells.clear()

        for (x in horizontalRange) {
            for (y in verticalRange) {
                val address = Point(x, y)
                when (this[x, y]) {

                    // Пустая клетка
                    null -> {
                        // В пустой клетке с тремя соседями зарождается жизнь
                        if (numberOfNeighbors(x, y) == 3) {
                            newCells[address] = Cell()
                        }
                    }

                    // Живая клетка
                    else -> {
                        if (numberOfNeighbors(x, y) in 2..3) {
                            newCells[address] = this[x, y]!!
                            newCells[address]?.grow()
                        }
                    }
                }
            }
        }

        cells.clear()
        cells.putAll(newCells)
    }

    // Границы просмотра колонии по горизонтали
    private fun getHorizontalMinMax(): IntRange {
        return if (cells.isEmpty()) {
            IntRange.EMPTY
        } else {
            val minX = cells.keys.reduce { acc, point -> if (acc.x < point.x) acc else point }.x
            val maxX = cells.keys.reduce { acc, point -> if (acc.x > point.x) acc else point }.x
            minX - 1..maxX + 1
        }
    }

    // Границы просмотра колонии по вертикали
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

        // Вычислить границы холста в размерах клетки
        val horizontalBounds = bounds.left until bounds.right
        val verticalBounds = bounds.top until bounds.bottom

        // Нарисовать стрелки, если имеются клетки за границами экрана
        if (verticalRange.first < verticalBounds.first) {
            arrows[Arrow.Direction.UP]?.draw(canvas)
        }
        if (verticalRange.last > verticalBounds.last) {
            arrows[Arrow.Direction.DOWN]?.draw(canvas)
        }
        if (horizontalRange.first < horizontalBounds.first) {
            arrows[Arrow.Direction.LEFT]?.draw(canvas)
        }
        if (horizontalRange.last > horizontalBounds.last) {
            arrows[Arrow.Direction.RIGHT]?.draw(canvas)
        }

        // Нарисовать все живые клетки, попадающие в границы холста
        cells.filter {
            it.key.x in horizontalBounds && it.key.y in verticalBounds
        }.forEach { (point, cell) ->
            cell.draw(canvas, point, cellSize)
        }
    }

    private fun drawUpArrow(canvas: Canvas) {
        val size = canvas.width / 20
        val rect = RectF().apply {
            top = 0f
            bottom = size.toFloat()
            left = ((canvas.width - size) / 2).toFloat()
            right = ((canvas.width + size) / 2).toFloat()
        }
        val paint = Paint().apply {
            color = Color.argb(64, 0, 0, 128)
        }
        canvas.drawOval(rect, paint)
    }

    private fun drawDownArrow(canvas: Canvas) {
        val size = canvas.width / 20
        val rect = RectF().apply {
            top = (canvas.height - size).toFloat()
            bottom = canvas.height.toFloat()
            left = (canvas.width - size) / 2f
            right = (canvas.width + size) / 2f
        }
        val paint = Paint().apply {
            color = Color.argb(64, 0, 0, 128)
        }
        canvas.drawOval(rect, paint)
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