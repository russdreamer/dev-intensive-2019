package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.PorterDuff.Mode
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
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
import kotlin.math.min


class CircleImageView @JvmOverloads constructor (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR: Int = Color.WHITE
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = Utils.convertDpToPx(context, 2F)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val srcMode = PorterDuffXfermode(Mode.SRC)
    private val srcInMode = PorterDuffXfermode(Mode.SRC_IN)

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

    override fun onDraw(canvas: Canvas) {
        if (width == 0 || height == 0) return
        val bitmap = getBitmapFromDrawable(width, height) ?: return

        val halfWidth = width / 2F
        val halfHeight = height / 2F
        val radius = min(halfWidth, halfHeight)

        circlePaint.xfermode = srcMode
        canvas.drawCircle(halfWidth, halfHeight, radius, circlePaint)
        circlePaint.xfermode = srcInMode
        canvas.drawBitmap(bitmap, 0F, 0F, circlePaint)

        if (borderWidth > 0) {
            borderPaint.color = borderColor
            borderPaint.strokeWidth = borderWidth.toFloat()
            canvas.drawCircle(halfWidth, halfHeight, radius - borderWidth / 2, borderPaint)
        }
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
}


