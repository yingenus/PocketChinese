package com.yingenus.pocketchinese.controller.dialog

import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.yingenus.pocketchinese.R

class DeleteDialog :DialogFragment(){
    interface Decision{
        fun delete()
        fun cancel()
    }

    private var mFullMes:String = ""
        set(value) {field = value
            if (dialog != null) (dialog as AlertDialog).setMessage(value)
        }
    var mMes:String =""

    var observer: Decision? = null

    fun addSubMes(mes:String){
        mMes+=mes
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog= AlertDialog.Builder(context!!)

        mFullMes = getString(R.string.delete)
        mFullMes +=mMes

        dialog.setMessage(mFullMes)
        dialog.setPositiveButton(R.string.delete) { _:DialogInterface, _:Int ->
            if(observer!=null) observer!!.delete()
        }
        dialog.setNegativeButton(R.string.cancel) { _:DialogInterface, _:Int ->
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