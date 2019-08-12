package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Bitmap.createBitmap
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils
import java.util.*
import kotlin.math.max
import kotlin.math.min


class CircleImageView @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR: Int = Color.WHITE
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPx(context, 2F)
    private val path = Path()
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        if (attrs != null) {
            val attrVal = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = attrVal.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = attrVal.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            attrVal.recycle()
        }
        borderPaint.style = Paint.Style.STROKE
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    @Dimension(unit = DP)
    fun getBorderWidth(): Int = Utils.convertPxToDp(context, borderWidth)

    @Dimension(unit = DP)
    fun setBorderWidth(dp: Int) {
        borderWidth = Utils.convertDpToPx(context, dp.toFloat())
        this.invalidate()
    }

    @ColorInt
    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    /* ЗАМЕРЫ ПРОИЗВОДЯТСЯ ЗДЕСЬ */
    override fun onDraw(canvas: Canvas) {
        val start = System.currentTimeMillis()
        repeat(1_000) {pathMethodDraw(canvas)}
        val end = System.currentTimeMillis()
        Log.d("M_CircleImageView", "method spent ${end - start} ms")
    }

    /*отрисовка битмапа - 9000, сдвиг 40_000, */
    private fun pathMethodDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        getBitmapFromDrawable(width, height) ?: return

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)
        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW)
        canvas.clipPath(path)

        super.onDraw(canvas)

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }
    }

    /*отрисовка битмапа - 1500, сдвиг 850, */
    private fun defaultMethodDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable(width, height) ?: return
        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0)
            bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    /*отрисовка битмапа - 800, сдвиг 1200 */
    private fun shaderMethodDraw(canvas: Canvas) {
        val bitmap = getBitmapFromDrawable(width, height) ?: return
        if (width == 0 || height == 0) return

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)

        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        circlePaint.shader = shader

        canvas.drawCircle(halfWidth, halfHeight, radius, circlePaint)

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }
    }

    /* отрисовка битмапа - 600, сдвиг 0 */
    private fun improvedDefaultMethodDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        val bitmap = getBitmapFromDrawable(width, height) ?: return

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)

        circlePaint.xfermode = PorterDuffXfermode(Mode.SRC)
        canvas.drawCircle(halfWidth, halfHeight, radius, circlePaint)
        circlePaint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0F, 0F, circlePaint)

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }
    }

    /* отрисовка битмапа - 24000, сдвиг - не долждался */
    private fun pathPaintMethodDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        getBitmapFromDrawable(width, height) ?: return

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)

        super.onDraw(canvas)
        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW)
        circlePaint.xfermode = PorterDuffXfermode(Mode.DST_IN)
        canvas.drawPath(path, circlePaint)

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }
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

    private fun getBitmapFromDrawable(width: Int, height: Int): Bitmap? {
        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        val bmp =  drawable.toBitmap(width, height, Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
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


