package com.yingenus.pocketchinese.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.yingenus.pocketchinese.R
import com.google.android.material.textfield.TextInputLayout
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsViewModel

class RenameDialog(val showedStudyList: ShowedStudyList, val userListsViewModel: UserListsViewModel) :DialogFragment(){
    object ERRORS_MES{
        const val EMPTY_FIELDS= R.string.notifi_empty_field
        const val BUSY_NAME= R.string.notifi_busy_list_name
    }

    interface Observer{
        fun onRename()
    }

    private lateinit var mInputLayout: TextInputLayout
    private lateinit var mCreateButton: Button
    private lateinit var mCancelButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.rename_dialog,container)

        mInputLayout=view.findViewById(R.id.edit_name)
        mCreateButton=view.findViewById(R.id.create_button)
        mCancelButton=view.findViewById(R.id.cancel_button)

        mCancelButton.setOnClickListener { onCancelClicked()}
        mCreateButton.setOnClickListener {onCreateClicked()}

        mInputLayout.editText!!.doAfterTextChanged { onEditTextChanged() }
        mInputLayout.editText!!.text.append(showedStudyList.name)

        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)

        return view
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window ?: return
        val layoutParam = window.attributes
        layoutParam.height = dp2px(410, requireContext())
        layoutParam.width = dp2px(350, requireContext())
        window.attributes = layoutParam
    }

    private fun onCancelClicked(){
        super.dismiss()
    }

    private fun onEditTextChanged(){
        val text=mInputLayout.editText!!.text
        if (text.isEmpty()||text.isBlank()){
            showEmp()
        }else{
            mInputLayout.error=null
        }
        if (!mCreateButton.isEnabled) mCreateButton.isEnabled=true
    }

    private fun onCreateClicked(){
        mCreateButton.isEnabled=false
        val name=mInputLayout.editText!!.text.toString()

        userListsViewModel.renameStudyList(name, showedStudyList).observe(viewLifecycleOwner){
            if (it) super.dismiss()
            else showBusy()
            mCreateButton.isEnabled=true
        }
    }

    private fun showEmp(){
        mInputLayout.error=getString(ERRORS_MES.EMPTY_FIELDS)
    }
    private fun showBusy(){
        mInputLayout.error=getString(ERRORS_MES.BUSY_NAME)
    }
}