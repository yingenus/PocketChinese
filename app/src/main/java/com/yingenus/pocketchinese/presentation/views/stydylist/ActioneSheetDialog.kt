package com.yingenus.pocketchinese.presentation.views.stydylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yingenus.pocketchinese.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActioneSheetDialog : BottomSheetDialogFragment() {

    var observer : EditSheetDialogCallback?=null;

    interface EditSheetDialogCallback{
        fun onRemove()
        fun onMove()
        fun onRename()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.edit_word_dialog_layout,container)

        view.findViewById<View>(R.id.edit).setOnClickListener { onRename() }
        view.findViewById<View>(R.id.delete).setOnClickListener { onRemove() }
        view.findViewById<View>(R.id.move).setOnClickListener { onMove() }

        return view;
    }

    override fun onDestroy() {
        super.onDestroy()
        observer = null
    }

    private fun onMove(){
        observer?.onMove()
        dismiss()
    }

    private fun onRemove(){
        observer?.onRemove()
        dismiss()
    }
    private fun onRename(){
        observer?.onRename()
        dismiss()
    }
}