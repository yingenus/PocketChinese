package com.yingenus.pocketchinese.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.domain.entitiys.database.PocketDBOpenManger
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyListDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList
import com.google.android.material.textfield.TextInputLayout
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.presentation.views.stydylist.UserStudyListViewModel
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class UserListRenameDialog(val userStudyListViewModel: UserStudyListViewModel) :DialogFragment(){
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
        userStudyListViewModel.studyListName.observe(viewLifecycleOwner){
            mInputLayout.editText!!.text.append(it)
        }

        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)

        return view
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

        userStudyListViewModel.renameStudyList(name).observe(this){
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