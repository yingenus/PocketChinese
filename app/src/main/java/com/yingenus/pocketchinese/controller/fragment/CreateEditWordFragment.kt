package com.yingenus.pocketchinese.controller.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.KeyboardCallbackInterface
import com.yingenus.pocketchinese.controller.Settings
import com.yingenus.pocketchinese.controller.hideKeyboard
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

abstract class CreateEditWordFragment protected constructor() : Fragment(R.layout.create_word_fragment), View.OnFocusChangeListener, TextWatcher, CreateWordInterface {
    object ERRORS_MES{
        const val INVALID_CHARS=R.string.nitify_invalid_chars
        const val EMPTY_FIELDS=R.string.notifi_empty_field
        const val MORE_THEN_MAX_CHARS=R.string.notifi_max_length
    }
    private object ANS{
        const val UUID="uuid"
        const val REQUEST_OK_CANSEL=1000
        const val RESULT_OK=1001
        const val RESULT_CANCEL=1002
    }

    private object STANDARDHINT{
        const val chn= R.string.chinese
        const val pin= R.string.pinyin
        const val trn= R.string.my_language
    }

    private val presenter : CreateEditWordPresenter by lazy { getViewPresenter() }

    protected lateinit var editChn: TextInputLayout
    protected lateinit var editPin: TextInputLayout
    protected lateinit var editTrn: TextInputLayout
    protected lateinit var listSpinner: AutoCompleteTextView
    protected lateinit var title: TextView
    protected lateinit var blockTabLayout: TabLayout
    protected lateinit var createButton: Button

    private lateinit var textChangedObservable: Observable<String>

    protected lateinit var studyListSelectedObserver : Observable<String>

    abstract fun getViewPresenter(): CreateEditWordPresenter

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(v==editChn){
            if (!hasFocus&&editChn.editText!!.text.isNotBlank())
                presenter.chnLostFocus()
        }
        if (!hasFocus) {
            if (activity is KeyboardCallbackInterface) {
                val activity = activity as KeyboardCallbackInterface
                if (v is EditText) activity.hideKeyboard(v)
            } else {
                if (v != null) hideKeyboard(v)
            }
        }

    }

    override fun afterTextChanged(s: Editable?) {
        when (activity!!.window.currentFocus) {
            editChn.editText -> presenter.afterTextChanged(CreateWordInterface.FIELD.CHN)
            editPin.editText -> presenter.afterTextChanged(CreateWordInterface.FIELD.PIN)
            editTrn.editText -> presenter.afterTextChanged(CreateWordInterface.FIELD.TRN)
        }

    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= super.onCreateView(inflater, container, savedInstanceState)!!

        editChn = view.findViewById(R.id.edit_chn)
        editPin = view.findViewById(R.id.edit_pin)
        editTrn = view.findViewById(R.id.edit_trn)
        listSpinner = view.findViewById(R.id.list_spinner)
        blockTabLayout = view.findViewById(R.id.block_tub_layout)
        createButton = view.findViewById(R.id.create_button)
        title = view.findViewById(R.id.top_text)

        editChn.hint = getString(STANDARDHINT.chn)
        editPin.hint = getString(STANDARDHINT.pin)
        editTrn.hint = getString(STANDARDHINT.trn)

        editChn.editText?.addTextChangedListener(this)
        editPin.editText?.addTextChangedListener(this)
        editTrn.editText?.addTextChangedListener(this)

        editChn.editText?.onFocusChangeListener = this
        editPin.editText?.onFocusChangeListener = this
        editTrn.editText?.onFocusChangeListener = this

        editChn.counterMaxLength = resources.getInteger(R.integer.max_chn)
        editPin.counterMaxLength = resources.getInteger(R.integer.max_pin)
        editTrn.counterMaxLength = resources.getInteger(R.integer.max_trn)

        initConnectionWithAppKeyboard()

        blockTabLayout.tabIndicatorAnimationMode = TabLayout.INDICATOR_ANIMATION_MODE_ELASTIC
        blockTabLayout.isInlineLabel = true

        createButton.setOnClickListener { createButtonClicked()}

        presenter.onCreate(context!!)

        (activity as AppCompatActivity).supportActionBar?.hide()
        activity?.setActionBar(view.findViewById(R.id.toolbar))

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

     override fun onDestroyView() {
         super.onDestroyView()
         presenter.onDestroy()
     }

     @SuppressLint("ClickableViewAccessibility")
    private fun initConnectionWithAppKeyboard(){
        if (activity is KeyboardCallbackInterface && Settings.useAppKeyboard(context!!)){
            val activity=activity as KeyboardCallbackInterface
            editPin.editText?.setOnClickListener { v: View ->  activity.showAppKeyboard(v)}
            editPin.editText?.setOnFocusChangeListener { v, hasFocus ->
                val editText=v as EditText
                this.onFocusChange(editText,hasFocus)
                if (hasFocus) {
                    activity.showAppKeyboard(editText)
                }
                else activity.hideAppKeyboard(v)
                editText.setSelection(editText.length())
            }

            editPin.editText?.setOnTouchListener { v, event ->
                val editText=v as EditText
                val inputType=editText.inputType
                editText.inputType= InputType.TYPE_NULL
                editText.onTouchEvent(event)
                editText.inputType=inputType
                activity.showAppKeyboard(v)
                editText.setSelection(editText.length())
                return@setOnTouchListener true
            }
        }
    }

    protected fun fillTabLayout(blocks: Int){
        blockTabLayout.removeAllTabs()
        for (i in 1..(blocks+1)){
            blockTabLayout.addTab(blockTabLayout.newTab().setText(getString(R.string.block, i)))
        }
    }

    override fun analogFounded(studyWord: StudyWord) {
        val dialog=HaveMatchDialog(studyWord)
        dialog.setTargetFragment(this,ANS.REQUEST_OK_CANSEL)
        dialog.show(fragmentManager!!,"create")
    }

    override fun setSelectBlock(position: Int) {
        if (blockTabLayout.tabCount>=position){
            blockTabLayout.selectTab(blockTabLayout.getTabAt(position-1))
        }
    }

    override fun getSelectedBlock(): Int {
        return blockTabLayout.selectedTabPosition
    }

    override fun setBlocks(items: Int) {
        fillTabLayout(items)
    }

    override fun setSelectStudyList(position: Int) {
        val name = listSpinner.adapter.getItem(position) as String

        listSpinner.setAdapter( null )
        listSpinner.setText(name)
    }

    override fun getChnInputObserver(): Observable<String> {
        if (!::textChangedObservable.isInitialized){
            textChangedObservable = Observable.create<String>{ emitter ->
                editChn.editText?.addTextChangedListener { text ->
                    emitter.onNext(text.toString()) }}
                    .subscribeOn(AndroidSchedulers.mainThread()).publish().autoConnect()
        }

        return textChangedObservable
    }

    override fun getSelectStudyListObservable(): Observable<String> {
        if(!::studyListSelectedObserver.isInitialized){
            studyListSelectedObserver = Observable.create<String> {
                listSpinner.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
                    it.onNext(parent.adapter.getItem(position) as String)
                }
            }.publish().autoConnect()
        }

        return studyListSelectedObserver
    }

    override fun wordCreated() {
        activity?.finish()
    }

    override fun getMaxCharsLength(field: CreateWordInterface.FIELD) =
        when(field){
            CreateWordInterface.FIELD.TRN -> resources.getInteger(R.integer.max_trn)
            CreateWordInterface.FIELD.PIN -> resources.getInteger(R.integer.max_pin)
            CreateWordInterface.FIELD.CHN -> resources.getInteger(R.integer.max_chn)
        }


    override fun getText(field: CreateWordInterface.FIELD) =
            when(field){
                CreateWordInterface.FIELD.TRN -> editTrn.editText!!.editableText.toString()
                CreateWordInterface.FIELD.PIN -> editPin.editText!!.editableText.toString()
                CreateWordInterface.FIELD.CHN -> editChn.editText!!.editableText.toString()
            }

    override fun setText(field: CreateWordInterface.FIELD, text: String) =
            when(field){
        CreateWordInterface.FIELD.TRN -> editTrn.editText!!.setText(text)
        CreateWordInterface.FIELD.PIN -> editPin.editText!!.setText(text)
        CreateWordInterface.FIELD.CHN -> editChn.editText!!.setText(text)
    }

    override fun setHint(field: CreateWordInterface.FIELD, hint: String) =
            when(field){
        CreateWordInterface.FIELD.TRN -> editTrn.hint = hint
        CreateWordInterface.FIELD.PIN -> editPin.hint = hint
        CreateWordInterface.FIELD.CHN -> editChn.hint = hint
    }

    override fun setListOfStudyLists(list: List<String>) {
        listSpinner.setAdapter(ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, list))
    }

    override fun enableChoseStudyList(enable: Boolean) {
        listSpinner.isEnabled = enable
    }

    open fun createButtonClicked(){
         presenter.onCreateWordClicked()
     }

     override fun showEmtFieldMes(field: CreateWordInterface.FIELD, show: Boolean) {
         val inputLayout = getInputLayout(field)
         if (show)
            showError(getString(ERRORS_MES.EMPTY_FIELDS),getInputLayout(field))
         else if (inputLayout.error!= null && inputLayout.error!!.equals(getString(ERRORS_MES.EMPTY_FIELDS))){
             inputLayout.error = null
         }
     }

     override fun showInvalCharsMes(field: CreateWordInterface.FIELD, show: Boolean) {
         val inputLayout = getInputLayout(field)
         if (show)
             showError(getString(ERRORS_MES.INVALID_CHARS),getInputLayout(field))
         else if (inputLayout.error!= null && inputLayout.error!!.equals(getString(ERRORS_MES.INVALID_CHARS))){
             inputLayout.error = null
         }
     }

     override fun showMaxCharsMes(field: CreateWordInterface.FIELD, show: Boolean) {
         val inputLayout = getInputLayout(field)
         if (show)
             showError(getString(ERRORS_MES.MORE_THEN_MAX_CHARS),getInputLayout(field))
         else if (inputLayout.error!= null && inputLayout.error!!.equals(getString(ERRORS_MES.MORE_THEN_MAX_CHARS))){
             inputLayout.error = null
         }

     }

     private fun getInputLayout(field: CreateWordInterface.FIELD): TextInputLayout = when(field){
         CreateWordInterface.FIELD.TRN -> editTrn
         CreateWordInterface.FIELD.PIN -> editPin
         CreateWordInterface.FIELD.CHN -> editChn
     }

     private fun showError(mes: String, inputLayout: TextInputLayout){
        inputLayout.error=mes
     }

    private class HaveMatchDialog(val studyWord: StudyWord) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val builder= AlertDialog.Builder(activity)
            builder.setNegativeButton(R.string.cancel) { dialogInterface: DialogInterface?, _: Int ->
                val intent= Intent()
                intent.putExtra(ANS.UUID,studyWord.uuid)
                targetFragment?.onActivityResult(targetRequestCode, ANS.RESULT_CANCEL,intent)
                dialogInterface?.cancel()
            }
            builder.setPositiveButton(R.string.Combine){ dialogInterface: DialogInterface?, _: Int ->
                val intent= Intent()
                intent.putExtra(ANS.UUID,studyWord.uuid)
                targetFragment?.onActivityResult(targetRequestCode, ANS.RESULT_OK,intent)
                dialogInterface?.cancel()
            }
            builder.setTitle(R.string.have_match)
            builder.setMessage("${studyWord.chinese} - ${studyWord.translate}")

            return builder.create()
        }
    }
}