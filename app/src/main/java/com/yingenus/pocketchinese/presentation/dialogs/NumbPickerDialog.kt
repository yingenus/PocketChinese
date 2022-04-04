package com.yingenus.pocketchinese.presentation.dialogs

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.yingenus.pocketchinese.R

class NumbPickerDialog : DialogFragment(){

    private lateinit var numberPicker:NumberPicker


    interface Observer{
        fun itemPicked(int: Int)
        fun cancel()
    }

    var observer: Observer?= null

    var titel: String = ""
        set(value) {field = value
            if (dialog != null) (dialog as AlertDialog).setMessage(value)
        }
    var fromValue: Int = 0
        set(value) {
            field= value
            if (::numberPicker.isInitialized) numberPicker.minValue = value
        }
    var toValue: Int = 0
        set(value) {
            field= value
            if (::numberPicker.isInitialized) numberPicker.maxValue = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //return super.onCreateDialog(savedInstanceState)

        val frameLayout = FrameLayout(context!!)
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
                ,FrameLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_HORIZONTAL

        numberPicker = NumberPicker(context )
        numberPicker.minValue = fromValue
        numberPicker.maxValue = toValue

        numberPicker.solidColor

        frameLayout.addView(numberPicker,params)

        titel = getString(R.string.dialog_chose_block_mes)

        val dialog= AlertDialog.Builder(context!!)

        dialog.setView(frameLayout)
        dialog.setTitle(R.string.dialog_chose_block)
        dialog.setMessage(titel)
        dialog.setPositiveButton(R.string.move) { _: DialogInterface, _:Int ->
            if(observer!=null) observer!!.itemPicked(numberPicker.value)

            dialog.setCancelable(true)
        }
        dialog.setNegativeButton(R.string.cancel) { _: DialogInterface, _:Int ->
            if(observer!=null) observer!!.cancel()

            dialog.setCancelable(true)
        }

        return dialog.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer = null
    }
}