package com.yingenus.pocketchinese.view

import android.graphics.Rect
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout

class SecondScrollingViewBehavior : AppBarLayout.ScrollingViewBehavior() {

    /*
    private var tempRect1 = Rect()
    private val tempRect2 = Rect()

    override fun layoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        val dependencies = parent.getDependencies(child)
        val header: View? = findFirstDependency(dependencies)

        if (header != null) {
            val lp = child.layoutParams as CoordinatorLayout.LayoutParams
            val available = tempRect1
            available[parent.paddingLeft + lp.leftMargin, header.bottom + lp.topMargin, parent.width - parent.paddingRight - lp.rightMargin] =
                parent.height + header.bottom - parent.paddingBottom - lp.bottomMargin
            val parentInsets = parent.lastWindowInsets
            if (parentInsets != null && ViewCompat.getFitsSystemWindows(parent)
                && !ViewCompat.getFitsSystemWindows(child)
            ) {
                // If we're set to handle insets but this child isn't, then it has been measured as
                // if there are no insets. We need to lay it out to match horizontally.
                // Top and bottom and already handled in the logic above
                available.left += parentInsets.systemWindowInsetLeft
                available.right -= parentInsets.systemWindowInsetRight
            }
            val out = tempRect2
            GravityCompat.apply(
                resolveGravity(lp.gravity),
                child.measuredWidth,
                child.measuredHeight,
                available,
                out,
                layoutDirection
            )
            val overlap = getOverlapPixelsForOffset(header)
            child.layout(out.left, out.top - overlap, out.right, out.bottom - overlap)
            verticalLayoutGap = out.top - header.bottom
        } else {
            // If we don't have a dependency, let super handle it
            super.layoutChild(parent, child, layoutDirection)
            verticalLayoutGap = 0
        }
    }

     */
}