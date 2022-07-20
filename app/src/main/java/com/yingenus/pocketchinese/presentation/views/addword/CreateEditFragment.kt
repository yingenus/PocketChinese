package com.yingenus.pocketchinese.presentation.views.addword

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillValue
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.Settings
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.controller.hideKeyboard
import com.yingenus.pocketchinese.view.utils.KeyboardCallbackInterface

class CreateEditFragment : Fragment(R.layout.create_edit_layout), View.OnFocusChangeListener {

    interface Callback{
        fun afterTextChanged(language: Language, text : String)
        fun chineseLostFocus();
    }

    object ERRORS_MES{
        const val INVALID_CHARS=R.string.nitify_invalid_chars
        const val EMPTY_FIELDS=R.string.notifi_empty_field
        const val MORE_THEN_MAX_CHARS=R.string.notifi_max_length
    }

    private object STANDARDHINT{
        const val chn= R.string.chinese
        const val pin= R.string.pinyin
        const val trn= R.string.my_language
    }

    var callback : Callback? = null

    private var editChn: TextInputLayout? = null
    private var editPin: TextInputLayout? = null
    private var editTrn: TextInputLayout? = null

    private var chineseText: String = ""
    private var pinyinText: String = ""
    private var translationText: String = ""

    private fun afterTextChanged(s: Editable?, language: Language) {
        when (language) {
            Language.CHINESE -> {
                chineseText = s.toString()
                callback?.afterTextChanged(Language.CHINESE, s.toString())
            }
            Language.PINYIN -> {
                pinyinText = s.toString()
                callback?.afterTextChanged(Language.PINYIN, s.toString())
            }
            Language.RUSSIAN ->{
                translationText = s.toString()
                callback?.afterTextChanged(Language.RUSSIAN, s.toString())
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if(v==editChn){
            if (!hasFocus&&editChn!!.editText!!.text.isNotBlank())
                callback?.chineseLostFocus()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        editChn = view.findViewById(R.id.edit_chn)
        editPin = view.findViewById(R.id.edit_pin)
        editTrn = view.findViewById(R.id.edit_trn)

        editChn!!.hint = getString(STANDARDHINT.chn)
        editPin!!.hint = getString(STANDARDHINT.pin)
        editTrn!!.hint = getString(STANDARDHINT.trn)

        editChn!!.editText!!.setText(chineseText)
        editPin!!.editText!!.setText(pinyinText)
        editTrn!!.editText!!.setText(translationText)

        editChn!!.editText?.doAfterTextChanged { afterTextChanged(it, Language.CHINESE) }
        editPin!!.editText?.doAfterTextChanged { afterTextChanged(it, Language.PINYIN) }
        editTrn!!.editText?.doAfterTextChanged { afterTextChanged(it, Language.RUSSIAN) }

        editChn!!.editText?.onFocusChangeListener = this
        editPin!!.editText?.onFocusChangeListener = this
        editTrn!!.editText?.onFocusChangeListener = this

        editChn!!.counterMaxLength = resources.getInteger(R.integer.max_chn)
        editPin!!.counterMaxLength = resources.getInteger(R.integer.max_pin)
        editTrn!!.counterMaxLength = resources.getInteger(R.integer.max_trn)

        initConnectionWithAppKeyboard()
        registerHideTouchListener(view)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
        editChn = null
        editPin = null
        editTrn = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initConnectionWithAppKeyboard(){
        if (activity is KeyboardCallbackInterface && Settings.useAppKeyboard(requireContext())){
            val activity=activity as KeyboardCallbackInterface
            editPin!!.editText?.setOnClickListener { v: View ->  activity.showAppKeyboard(v)}
            editPin!!.editText?.setOnFocusChangeListener { v, hasFocus ->
                val editText=v as EditText
                this.onFocusChange(editText,hasFocus)
                if (hasFocus) {
                    activity.showAppKeyboard(editText)
                }
                else activity.hideAppKeyboard(v)
                editText.setSelection(editText.length())
            }

            editPin!!.editText?.setOnTouchListener { v, event ->
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

    fun setText(language: Language, text: String) =
        when(language){
            Language.RUSSIAN -> {
                translationText = text
                editTrn?.editText?.let {
                    if(it.text.toString() != text)
                        it.setText(text)
                }
            }
            Language.PINYIN -> {
                pinyinText = text
                editPin?.editText?.let {
                    if (it.text.toString() != text)
                        it.setText(text)

                }
            }
            Language.CHINESE -> {
                chineseText = text
                editChn?.editText?.let {
                    if (it.text.toString() != text)
                        it.setText(text)
                }
            }
        }

    fun getText(language: Language) =
        when(language){
            Language.RUSSIAN -> translationText
            Language.PINYIN -> pinyinText
            Language.CHINESE -> chineseText
        }


    fun showEmtFieldMes(language: Language, show: Boolean) {
        val inputLayout = getInputLayout(language)
        if (show)
            showError(getString(ERRORS_MES.EMPTY_FIELDS),getInputLayout(language))
        else if (inputLayout?.error!= null && inputLayout.error!!.equals(getString(
                ERRORS_MES.EMPTY_FIELDS))){
            inputLayout.error = null
        }
    }

    fun showInvalCharsMes(language: Language, show: Boolean) {
        val inputLayout = getInputLayout(language)
        if (show)
            showError(getString(ERRORS_MES.INVALID_CHARS),getInputLayout(language))
        else if (inputLayout?.error!= null && inputLayout.error!!.equals(getString(
                ERRORS_MES.INVALID_CHARS))){
            inputLayout.error = null
        }
    }

    fun showMaxCharsMes(language: Language, show: Boolean) {
        val inputLayout = getInputLayout(language)
        if (show)
            showError(getString(ERRORS_MES.MORE_THEN_MAX_CHARS),getInputLayout(language))
        else if (inputLayout?.error!= null && inputLayout.error!!.equals(getString(
                ERRORS_MES.MORE_THEN_MAX_CHARS))){
            inputLayout.error = null
        }

    }

    fun hideError(language: Language){
        val inputLayout = getInputLayout(language)
        inputLayout?.error = null
    }

    private fun getInputLayout(language: Language): TextInputLayout? = when(language){
        Language.RUSSIAN -> editTrn
        Language.PINYIN -> editPin
        Language.CHINESE-> editChn
    }

    private fun showError(mes: String, inputLayout: TextInputLayout?){
        inputLayout?.error=mes
    }

    private fun registerHideTouchListener(view : View){

        if(view !is EditText){
            view.setOnTouchListener { v, event ->
                val focusView = requireActivity().currentFocus
                if (focusView!= null){
                    if (requireActivity() is KeyboardCallbackInterface){
                        (requireActivity() as KeyboardCallbackInterface).hideKeyboard(focusView)
                    }
                    else {
                        hideKeyboard(focusView)
                    }
                }
                false
            }
        }

        if (view is ViewGroup){
            for ( child in  0 until  view.childCount){
                registerHideTouchListener(view.getChildAt(child))
            }
        }

    }
}