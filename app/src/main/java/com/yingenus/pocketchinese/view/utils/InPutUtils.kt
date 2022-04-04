package com.yingenus.pocketchinese.controller

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


fun hideKeyboard(view: View){
    val imm=view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken,0)
}

fun showKeyboard(view: View){
    val imm=view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT)
}


