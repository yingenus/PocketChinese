package com.yingenus.pocketchinese.controller

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

internal  fun dp2px(dp : Int, context: Context): Int{
    val dm = context.resources.displayMetrics
    return Math.round(dp * (dm.xdpi/ DisplayMetrics.DENSITY_DEFAULT))
}

internal fun getDisplayHeight(context : Context): Int =  try {
        context.display!!.height
    } catch (e : NoSuchMethodError){
        context.resources.displayMetrics.heightPixels
    }

internal fun Throwable.logErrorMes() = cause.toString()+"\n"+suppressed+"\n"+message+"\n"+stackTrace.joinToString(separator = "\n")

class CardBoundTopBottom(context: Context, dp : Int) : RecyclerView.ItemDecoration(){

    val bound : Int = dp2px(8,context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom += bound
        outRect.top += bound
    }
}
class CardBoundLeftRight(context: Context, dp : Int) : RecyclerView.ItemDecoration(){

    val bound : Int = dp2px(8,context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left += bound
        outRect.right += bound
    }
}

class BoundsDecoratorBottom(context: Context) : ItemDecoration() {

    val bound : Int = dp2px(100,context)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.adapter!!.itemCount - 1 == parent.getChildAdapterPosition(view)) {
            outRect.bottom += bound
        }
    }
}

