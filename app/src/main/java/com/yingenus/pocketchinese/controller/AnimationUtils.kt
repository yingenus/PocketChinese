package com.yingenus.pocketchinese.controller

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation

fun rotationAnimation(view: View, up: Boolean, duration : Int) {
    val from = if (up) 180 else 0
    val to = if (up) 0 else 180
    val animation = RotateAnimation(from.toFloat(), to.toFloat(), (view.width / 2).toFloat(), (view.height / 2).toFloat())
    animation.fillAfter = true
    animation.duration = duration.toLong()
    view.startAnimation(animation)
}

fun alphaAnimation(view: View, initialAlpha : Int, finalAlpha : Int, duration : Int) {
    //TODO()
}

fun expandAnimation(view: View, up: Boolean, duration : Int) {
    val initHeight: Int = view.getHeight()
    val finalHeight: Int
    finalHeight = if (up) {
        0
    } else {
        val measureSpeckW = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY)
        val measureSpeckH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(measureSpeckW, measureSpeckH)
        view.getMeasuredHeight()
    }
    val params: ViewGroup.LayoutParams = view.getLayoutParams()
    val animator = ValueAnimator.ofInt(initHeight, finalHeight)
    animator.duration = duration.toLong()
    animator.addUpdateListener { animation ->
        params.height = animation.animatedValue as Int
        view.requestLayout()
        view.invalidate()
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            if (!up) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                view.requestLayout()
                view.invalidate()
            }
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    animator.start()
}