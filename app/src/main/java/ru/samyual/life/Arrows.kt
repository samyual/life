package ru.samyual.life

import android.graphics.*
import android.util.Size

/**
 * Создаёт граничные трелки в четырёх направлениях заданного размера,
 * стрелки генерируются путём поворота оригинального битмапа
 * @param originalBitmap битмап со стрелкой, направленной вверх
 * @param size размеры стрелки
 * @param screenSize размеры экрана в пикселях
 */
class Arrows(
    private val originalBitmap: Bitmap,
    private val size: Size,
    private val screenSize: Size
) {

    enum class Direction(val degrees: Float) {
        Up(0f),
        Right(90f),
        Down(180f),
        Left(270f)
    }

    companion object {
        val paint = Paint()
    }

    private val bitmapCache = mutableMapOf<Direction, Bitmap>()

    init {
        bitmapCache[Direction.Up] = createArrowBitmap(Direction.Up)
        bitmapCache[Direction.Right] = createArrowBitmap(Direction.Right)
        bitmapCache[Direction.Down] = createArrowBitmap(Direction.Down)
        bitmapCache[Direction.Left] = createArrowBitmap(Direction.Left)
    }

    private val positionCache = mutableMapOf<Direction, PointF>()

    init {
        positionCache[Direction.Up] = PointF().apply {
            x = (screenSize.width - size.width) / 2f
            y = 0f
        }
        positionCache[Direction.Down] = PointF().apply {
            x = (screenSize.width - size.width) / 2f
            y = (screenSize.height - size.height).toFloat()
        }
        positionCache[Direction.Left] = PointF().apply {
            x = 0f
            y = (screenSize.height - size.height) / 2f
        }
        positionCache[Direction.Right] = PointF().apply {
            x = (screenSize.width - size.width).toFloat()
            y = (screenSize.height - size.height) / 2f
        }
    }

    fun draw(canvas: Canvas, direction: Direction) {
        canvas.drawBitmap(
            bitmapCache[direction]!!,
            positionCache[direction]!!.x,
            positionCache[direction]!!.y,
            paint
        )
    }

    // Создаёт и помещает в кэш битмапов стрелку в нужном направлении
    private fun createArrowBitmap(direction: Direction): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            size.width, size.height, true
        )
        val rotation = Matrix()
        rotation.postRotate(direction.degrees)
        return Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, rotation, true
        )
    }
}