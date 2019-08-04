package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.PX


class TextBitmapBuilder(val width: Int, val height: Int) {

    private var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private var text: String = ""
    private var textSize: Int = 0
    @ColorInt private var textColor: Int = -0x1000000
    @ColorInt private var bgColor: Int = -0x1

    fun setBackgroundColor(@ColorInt color: Int) = this.apply { bgColor = color }
    fun setTextColor(@ColorInt color: Int) = this.apply { textColor = color }
    fun setText(text: String) = this.apply { this.text = text }
    fun setTextSize(@Dimension(unit = PX) size: Int) = this.apply { textSize = size }

    fun build(): Bitmap {
        val canvas = Canvas(bitmap)
        canvas.drawColor(bgColor)

        if (text.isNotEmpty())
            drawText(canvas)

        return bitmap
    }

    private fun drawText(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize.toFloat()
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, width.toFloat(), height.toFloat())

        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
        canvas.drawText(text, backgroundBounds.centerX(), textBottom, paint)
    }
}