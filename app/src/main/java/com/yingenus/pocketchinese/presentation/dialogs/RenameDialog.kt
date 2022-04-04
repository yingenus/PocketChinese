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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class RenameDialog(val studyList: StudyList) :DialogFragment(){
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

    private lateinit var listDAO: StudyListDAO

    var observer: Observer?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view=inflater.inflate(R.layout.rename_dialog,container)


        mInputLayout=view.findViewById(R.id.edit_name)
        mCreateButton=view.findViewById(R.id.create_button)
        mCancelButton=view.findViewById(R.id.cancel_button)

        mCancelButton.setOnClickListener { onCancelClicked()}
        mCreateButton.setOnClickListener {onCreateClicked()}

        mInputLayout.editText!!.doAfterTextChanged { onEditTextChanged() }
        mInputLayout.editText!!.text.append(studyList.name)

        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)

        val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

        listDAO = StudyListDAO(sqlDb)

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
        val text=mInputLayout.editText!!.text.toString()
        if (mInputLayout.editText!!.text.isEmpty())
            showEmp()
        else {
            val observer = Observable.create<Boolean> { emitter ->
                emitter.onNext(nameIsBusy(text))
            }
            observer.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            observer.subscribe {
                mCreateButton.isEnabled = true
                if (it)
                    showBusy()
                else{
                    studyList.name=text
                    listDAO.update(studyList)
                    onRename()
                    super.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        observer=null

        listDAO.finish()
        PocketDBOpenManger.releaseHelper()
    }

    private fun onRename(){
        observer?.onRename()
    }

    private fun showEmp(){
        mInputLayout.error=getString(ERRORS_MES.EMPTY_FIELDS)
    }
    private fun showBusy(){
        mInputLayout.error=getString(ERRORS_MES.BUSY_NAME)
    }

    private fun nameIsBusy(name: String)=
            listDAO.get(name)!=null



}