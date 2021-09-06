package com.yingenus.pocketchinese.view.bubblecust;

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat


const val ICON_STATE_ANIMATOR_DURATION: Long = 350

internal fun ImageView.colorAnimator(
    @ColorInt from: Int,
    @ColorInt to: Int,
    durationInMillis: Long
): Animator = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
    duration = durationInMillis
    addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        run { setColorFilter(color) }
    }
}

internal fun ImageView.setColorStateListAnimator(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int
) {
    val stateList = StateListAnimator().apply {
        addState(
            intArrayOf(android.R.attr.state_selected),
            colorAnimator(unselectedColor, color, ICON_STATE_ANIMATOR_DURATION)
        )
        addState(
            intArrayOf(),
            colorAnimator(color, unselectedColor, ICON_STATE_ANIMATOR_DURATION)
        )
    }

    stateListAnimator = stateList

    // Refresh the drawable state to avoid the unselected animation on view creation
    refreshDrawableState()
}


var DURATION = 350L
var ALPHA = 1f
internal fun TextView.expand(container: LinearLayout, iconColor: Int) {
    val bounds = Rect()
    container.setCustomBackground(iconColor, ALPHA)
    paint.apply {
        getTextBounds(text.toString(), 0, text.length, bounds)
        ValueAnimator.ofInt(0, bounds.width() + paddingStart + 10).apply {
            addUpdateListener {
                if (it.animatedFraction == (0.0f)) {
                    visibility = View.INVISIBLE
                }
                layoutParams.apply {
                    width = it.animatedValue as Int
                    height=height;
                }

                if (it.animatedFraction == (1.0f)) {
                    visibility = View.VISIBLE
                }
                requestLayout()
            }
            this.addListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {
                    layoutParams.apply {
                        width = 0
                        height=height;
                    }
                    visibility = View.INVISIBLE
                    requestLayout()
                }

                override fun onAnimationStart(animation: Animator?) {

                }
            })
            interpolator = LinearInterpolator()

            duration = DURATION
        }.start()
    }
}


internal fun TextView.collapse( container: LinearLayout,  iconColor: Int) {
    animate().alpha(0f).apply {
        setUpdateListener {
            layoutParams.apply {
                width = (width - (width * it.animatedFraction)).toInt()
                height=height;
            }
            if (it.animatedFraction == 1.0f) {
                visibility = View.GONE
                alpha = 1.0f
            }
            interpolator = LinearInterpolator()
            duration = DURATION
            //container.setCustomBackground(iconColor, ALPHA - (ALPHA * it.animatedFraction))
            container.setCustomBackground(iconColor, ALPHA)
            requestLayout()
        }
        setListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                layoutParams.apply {
                    width = 0
                    height=height;
                }
                requestLayout()
            }

            override fun onAnimationCancel(animation: Animator?) {
                layoutParams.apply {
                    width = 0
                    height=height;
                }
                requestLayout()
            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
    }.start()

}

internal fun LinearLayout.setCustomBackground(color: Int, alpha: Float) {
    val containerBackground = GradientDrawable().apply {
        cornerRadius = 100f
        DrawableCompat.setTint(
            DrawableCompat.wrap(this), Color.argb(
                (Color.alpha(color) * alpha).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
    }
    background = containerBackground
}
