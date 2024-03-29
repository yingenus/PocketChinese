package com.yingenus.pocketchinese.view.utils

import android.view.View

interface KeyboardCallbackInterface {
    fun adjustResize(boolean: Boolean)
    fun showKeyboard(view: View)
    fun hideKeyboard(view: View)
    fun showAppKeyboard(view: View)
    fun hideAppKeyboard(view: View)
}