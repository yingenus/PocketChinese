package com.yingenus.pocketchinese.controller.fragment

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.expandAnimation
import com.yingenus.pocketchinese.model.words.suggestwords.JSONObjects
import com.yingenus.pocketchinese.presenters.AddWordsPresenter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout

class AddWordFragment() : Fragment(R.layout.add_words_layout), AddWordInterface{


    class AddWordFragmentFactory(val words : List<JSONObjects.Word>, var callback: AddWordsCallbacks?): FragmentFactory(){
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return when(className){
                AddWordFragment::class.java.name -> {
                    val fragment = AddWordFragment(words)
                    if (callback != null)
                        fragment.setCallBack(callback!!)
                    fragment
                }
                else -> super.instantiate(classLoader, className)
            }
        }
    }

    constructor(words : List<JSONObjects.Word>): this(){
        this.words = words
    }


    object InsertingList{
        var word: List<JSONObjects.Word>? = null
    }

    interface AddWordsCallbacks{
        fun onError()
        fun onClose()
        fun onAdded()
    }


    private var presenter : AddWordsPresenter? = AddWordsPresenter(this)

    private lateinit var words : List<JSONObjects.Word>

    private lateinit var listTypeTab : TabLayout
    private lateinit var selectListLayout : TextInputLayout
    private lateinit var newListLayout : TextInputLayout
    private lateinit var selectSpinner : AutoCompleteTextView
    private lateinit var romCheckBox: CheckBox
    private lateinit var romField : View
    private lateinit var romTypeGroup : RadioGroup
    private lateinit var blocksTab : TabLayout
    private lateinit var splitField : View
    private lateinit var spitCountView : View
    private lateinit var splitCheckBox: CheckBox
    private lateinit var itemCountField : View
    private lateinit var itemCountEditText: EditText
    private lateinit var randomizeCheckBox : CheckBox
    private lateinit var randomizeField : View
    private lateinit var foggingView : FrameLayout
    private lateinit var progressDialog : CircularProgressIndicator

    private var callback: AddWordsCallbacks? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        listTypeTab = view.findViewById(R.id.list_tubs)
        selectListLayout = view.findViewById(R.id.list_select_layout)
        newListLayout = view.findViewById(R.id.new_list_layout)
        selectSpinner = view.findViewById(R.id.list_select)
        romCheckBox = view.findViewById(R.id.romanization_checkBox)
        romField = view.findViewById(R.id.romanization_field)
        romTypeGroup = view.findViewById(R.id.romanization_type)
        blocksTab = view.findViewById(R.id.block_tub_layout)
        splitField = view.findViewById(R.id.split_Field)
        splitCheckBox = view.findViewById(R.id.split_checkbox)
        itemCountEditText = view.findViewById(R.id.item_count_number)
        itemCountField = view.findViewById(R.id.item_count_field)
        randomizeCheckBox = view.findViewById(R.id.randomize_check)
        randomizeField = view.findViewById(R.id.randomize_field)
        foggingView = view.findViewById(R.id.fogging_view)
        progressDialog = view.findViewById(R.id.progress_dialog)
        spitCountView = view.findViewById(R.id.item_count_field)

        view.findViewById<Button>(R.id.create_button).setOnClickListener(this::onCreateClicked)
        view.findViewById<Button>(R.id.cancel_button).setOnClickListener(this::onCancelClicked)

        romField.setOnClickListener(this::onFieldClicked)
        splitField.setOnClickListener(this::onFieldClicked)
        randomizeField.setOnClickListener(this::onFieldClicked)

        splitCheckBox.setOnCheckedChangeListener(this::onCheckedChanged)
        romCheckBox.setOnCheckedChangeListener(this::onCheckedChanged)

        initListTabListener()
        initSelectionListener()
        initTextListener()

        presenter!!.onCreate(context!!, getInsertedWords())

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter!!.onDestroy()
        presenter = null
    }

    override fun getNewListName(): String {
        return newListLayout.editText?.text.toString()
    }

    override fun getSelectedList(): String {
        return selectSpinner.text.toString()
    }

    override fun getSelectedParams() = AddWordInterface.SelectedParams(
                existBlock = (listTypeTab.selectedTabPosition == 0),
                pinType = if (romCheckBox.isChecked && romTypeGroup.checkedRadioButtonId == R.id.romanization_simple)
                                AddWordInterface.PinType.SIMPLIFIED
                            else
                                AddWordInterface.PinType.STANDARD,
                startedBlock = blocksTab.selectedTabPosition + 1,
                splitToBlocks = splitCheckBox.isChecked,
                wordsInBlock = itemCountEditText.text.toString().toInt(),
                mixWords = randomizeCheckBox.isChecked
                )


    override fun setBlocksCount(blocks: Int) {
        if (blocksTab.visibility == View.GONE)
            blocksTab.visibility = View.VISIBLE

        blocksTab.removeAllTabs()
        for (i in 1..(blocks+1)){
            blocksTab.addTab(blocksTab.newTab().setText(getString(R.string.block, i)))
        }
    }

    override fun setUserLists(names: List<String>) {
        selectSpinner.setAdapter(ArrayAdapter(context!!,android.R.layout.simple_spinner_dropdown_item, names))
        selectSpinner.setSelection(0)
    }

    override fun showNameError(error: AddWordInterface.CreateError, show: Boolean) {
        val mess = getErrorMess(error)

        if (!show && newListLayout.error != null && newListLayout.error!!.equals(mess)){
            newListLayout.error = null
        }
        else if (show){
            newListLayout.error = mess
        }
    }

    override fun listNotSelected(show: Boolean) {
        if(show){
            selectListLayout.error = getString(R.string.error_list_not_selected)
        }else{
            selectListLayout.error = null
        }
    }

    override fun startInsert() {
        progressDialog.show()
        foggingView.visibility = View.VISIBLE
        foggingView.setOnClickListener(this:: nullOnClickListener)
    }

    override fun finishInsert(isSuccessful: Boolean) {
        progressDialog.hide()
        progressDialog.indeterminateDrawable?.clearAnimationCallbacks()
        foggingView.visibility = View.GONE
        foggingView.setOnClickListener(null)
        if(!isSuccessful) {
            callback?.onError()
        }
        else{
            callback?.onAdded()
        }
    }

    fun setCallBack(addWordsCallbacks: AddWordsCallbacks?){
        this.callback = addWordsCallbacks
    }

    private fun nullOnClickListener(v: View){

    }

    private fun onCheckedChanged(button : CompoundButton, isChecked : Boolean){
        when(button){
            romCheckBox ->{
                expandAnimation(romTypeGroup, !isChecked, 250)
            }
            splitCheckBox ->{
                expandAnimation(spitCountView, !isChecked, 250)
            }
        }
    }

    private fun onFieldClicked(v : View){
        when (v){
            romField ->{
                romCheckBox.isChecked =  !romCheckBox.isChecked
            }
            splitField ->{
                splitCheckBox.isChecked = !splitCheckBox.isChecked
            }
            randomizeField ->{
                randomizeCheckBox.isChecked = !randomizeCheckBox.isChecked
            }

        }
    }

    private fun onCancelClicked(v : View){
        callback?.onClose()
    }

    private fun onCreateClicked(v : View){
        presenter!!.insertWords()
    }

    private fun initTextListener(){
        newListLayout.editText!!.addTextChangedListener( object: TextWatcher{
            override fun afterTextChanged(s: Editable?) { presenter!!.onEditText(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun initSelectionListener(){
        selectSpinner.onItemClickListener =  AdapterView.OnItemClickListener{ _, _, _, _ ->
            presenter!!.onListSelected()
        }
    }

    private fun initListTabListener(){
        listTypeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab!!.position){
                    0 -> {
                        alphaViewTransfer(newListLayout, selectListLayout, 250)
                    }
                    1 -> {
                        alphaViewTransfer(selectListLayout, newListLayout , 250)
                    }
                }
            }
        })
    }

    private fun alphaViewTransfer(from : View, to : View , duration : Int){
        val animator = ValueAnimator.ofFloat(0f,1f)
        animator.duration = duration.toLong()
        animator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationEnd(animation: Animator?) {
                from.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animator?) {
                to.alpha = 0f
                to.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
        } )
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            from.alpha = 1 - value
            to.alpha = value
            from.invalidate()
            to.invalidate()
        }
        animator.start()
    }

    private fun getErrorMess(error: AddWordInterface.CreateError) = when (error){
        AddWordInterface.CreateError.NOT_AVAIL_CHARS -> getString(R.string.nitify_invalid_chars)
        AddWordInterface.CreateError.NAME_BUSY -> getString(R.string.notifi_busy_list_name)
        AddWordInterface.CreateError.EMPTY_NAME -> getString(R.string.notifi_empty_list_name)
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(context!!)
                .setMessage(getString(R.string.error_insert_words))
                .setPositiveButton(android.R.string.cancel) { _, _-> activity!!.finish()}
                .show()
    }

    private fun getInsertedWords(): List<JSONObjects.Word>{
        if (::words.isInitialized){
            return words
        }
        if (InsertingList.word == null){
            return emptyList()
        }
        return InsertingList.word!!
    }
}