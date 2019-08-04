package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils
import kotlin.math.min


class CircleImageView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR: Int = Color.WHITE
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPx(context, 2F)

    init {
        if (attrs != null) {
            val attrVal = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = attrVal.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = attrVal.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            attrVal.recycle()
        }
    }

    fun getBorderWidth(): Int = Utils.convertPxToDp(context, borderWidth)

    fun setBorderWidth(dp: Int) {
        borderWidth = Utils.convertDpToPx(context, dp.toFloat())
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

     override fun onDraw(canvas: Canvas) {
         var bitmap = getBitmapFromDrawable() ?: return
         if (width == 0 || height == 0) return

         bitmap = getScaledBitmap(bitmap, width)
         bitmap = getCenterCroppedBitmap(bitmap, width)
         bitmap = getCircleBitmap(bitmap)

         if (borderWidth > 0)
             bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)

         canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Int, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F

        inCircle.set(strokeStart , strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()

        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val cropStartX = (bitmap.width - size) / 2
        val cropStartY = (bitmap.height - size) / 2

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, size, size)
    }

    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int) : Bitmap {
        return if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = min(bitmap.width, bitmap.height).toFloat()
            val factor = smallest / minSide
            Bitmap.createScaledBitmap(bitmap, (bitmap.width / factor).toInt(), (bitmap.height / factor).toInt(), false)
        } else bitmap
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        return drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Config.ARGB_8888)
        val canvas = Canvas(outputBmp)

        val paint = Paint()

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)
        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0F, 0F,  paint)

        return outputBmp
    }
}


