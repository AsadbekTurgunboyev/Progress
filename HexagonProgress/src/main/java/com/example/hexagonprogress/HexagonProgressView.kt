package com.example.hexagonprogress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class HexagonProgressView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val stroke = 15f * Resources.getSystem().displayMetrics.density

    private var onAnimationEnd: (() -> Unit)? = null

    fun setOnAnimationEndListener(callback: () -> Unit) {
        onAnimationEnd = callback
    }
    var cornerRadius: Float = stroke / 2
        set(value) {
            field = value
            backgroundPaint.pathEffect = CornerPathEffect(cornerRadius)
            progressPaint.pathEffect = CornerPathEffect(cornerRadius)
            invalidate() // redraw the view with the new corner radius
        }


    private val backgroundPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        color = Color.GRAY
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(cornerRadius)
    }


    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = stroke
        color = Color.YELLOW
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(cornerRadius)
    }

    private val path = Path()
    private val pathMeasure = PathMeasure()

    // 0 <= progress <= 1
    var progress: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    var animationDuration: Long = 0
        set(value) {
            field = value
            startAnimation(value)
        }

    fun animateCornerRadius(from: Float, to: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(from, to).apply {
            this.duration = duration
            addUpdateListener { animation ->
                cornerRadius = animation.animatedValue as Float
            }
        }
        animator.start()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = minOf(centerX, centerY) - stroke / 2

        // define the path for the hexagon
        path.reset()
        for (i in 0 until 6) {
            val angle = 2 * StrictMath.PI * i / 6 - StrictMath.PI / 2
            val x = centerX + radius * StrictMath.cos(angle).toFloat()
            val y = centerY + radius * StrictMath.sin(angle).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        // draw the full hexagon with the background paint
        canvas.drawPath(path, backgroundPaint)

        // measure the path
        pathMeasure.setPath(path, false)
        val pathLength = pathMeasure.length

        // create a path segment based on the progress
        val progressPath = Path()
        pathMeasure.getSegment(0f, progress * pathLength, progressPath, true)

        // draw the path segment with the progress paint
        canvas.drawPath(progressPath, progressPaint)
    }

    private fun startAnimation(duration: Long) {
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            this.duration = duration
            addUpdateListener { animation ->
                progress = animation.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    onAnimationEnd?.invoke()
                }
            })
        }

        animator.start()
    }

}