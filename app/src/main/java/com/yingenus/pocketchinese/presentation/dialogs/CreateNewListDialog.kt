package com.yingenus.pocketchinese.controller.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.yingenus.pocketchinese.R
import com.google.android.material.textfield.TextInputLayout
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsViewModel


class CreateNewListDialog( val viewModel : UserListsViewModel) : DialogFragment() {
    object ERRORS_MES{
        const val EMPTY_FIELDS=R.string.notifi_empty_field
        const val BUSY_NAME=R.string.notifi_busy_list_name
    }

    private var mInputLayout: TextInputLayout? = null
    private var mCreateButton: Button? = null
    private var mCancelButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.create_dialog,container)


        mInputLayout=view.findViewById(R.id.edit_name)
        mCreateButton=view.findViewById(R.id.create_button)
        mCancelButton=view.findViewById(R.id.cancel_button)

        mCreateButton!!.setOnClickListener {onCreateClicked()}
        mCancelButton!!.setOnClickListener { onCancelClicked() }

        mInputLayout!!.editText!!.doAfterTextChanged { onEditTextChanged() }

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
        val text=mInputLayout!!.editText!!.text
        if (text.isEmpty()||text.isBlank()){
            showEmp()
        }else{
            mInputLayout!!.error=null
        }
        if (!mCreateButton!!.isEnabled) mCreateButton!!.isEnabled=true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mInputLayout = null
        mCreateButton = null
        mCancelButton = null
    }

    private fun onCreateClicked(){
        mCreateButton!!.isEnabled=false
        val name=mInputLayout!!.editText!!.text.toString()
        if (mInputLayout!!.editText!!.text.isEmpty())
            showEmp()
        viewModel.createStudyList(name).observe(viewLifecycleOwner){
            if (!it){
                showBusy()
                mCreateButton!!.isEnabled=true
            }
            else {
                viewModel.updateStatistic()
                viewModel.updateStudyLists()
                super.dismiss()
            }
        }
    }

    private fun showEmp(){
        mInputLayout!!.error=getString(ERRORS_MES.EMPTY_FIELDS)
    }
    private fun showBusy(){
        mInputLayout!!.error=getString(ERRORS_MES.BUSY_NAME)
    }
}

