package com.yingenus.pocketchinese.presentation.views

import android.view.View
import android.view.ViewGroup

@Suppress("UNCHECKED_CAST")
fun <T : View> View.findViewByClass(clazz: Class<T>): T?{
    if (clazz.isAssignableFrom(this::class.java)){
        return this as T?
    }
    if (this is ViewGroup){
        for (childIndex in 0 until childCount){
            val child = this.getChildAt(childIndex)
            if (clazz.isAssignableFrom(child::class.java)){
                return child as T?
            }
            if (child is ViewGroup){
                val subChild = child.findViewByClass(clazz)
                subChild?: return subChild
            }
        }
    }
    return null
}

@Suppress("UNCHECKED_CAST")
fun <T : View> View.findViewsByClass(clazz: Class<T>): List<T>{
    if (clazz.isAssignableFrom(this::class.java)){
        return listOf(this as T)
    }
    if (this is ViewGroup){
        val views = mutableListOf<T>()

        for (childIndex in 0 until childCount){
            val child = this.getChildAt(childIndex)
            if (clazz.isAssignableFrom(child::class.java)){
                views.add(child as T)
            }
            if (child is ViewGroup){
                val subChilds = child.findViewsByClass(clazz)
                views.addAll(subChilds)
            }
        }
        return views
    }
    return emptyList()
}

fun isRussian( str : String) = Regex("""[А-Яа-я]""").containsMatchIn(str)