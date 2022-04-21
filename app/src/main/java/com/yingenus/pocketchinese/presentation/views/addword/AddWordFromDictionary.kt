package com.yingenus.pocketchinese.presentation.views.addword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordFomDictionaryViewModel
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import java.lang.ref.WeakReference
import javax.inject.Inject


class AddWordFromDictionaryActivity : SingleFragmentActivityWithKeyboard(){

    companion object{
        private const val DICTIONARY_ITEM = "com.yingenus.pocketchinese.presentation.views.addword.dictionary_item"
        private var words : List<SuggestWord> = emptyList()

        fun getIntent(word : DictionaryItem, context: Context): Intent {
            val intent = Intent(context, AddWordFromDictionaryActivity::class.java)
            intent.putExtra(DICTIONARY_ITEM,word)
            return intent
        }

        private fun getWordsFromIntent(intent: Intent): DictionaryItem?{
            return intent.getSerializableExtra(DICTIONARY_ITEM) as DictionaryItem?
        }
    }


    override fun createFragment(): Fragment {
       val word = getWordsFromIntent(intent)!!
        return AddWordFromDictionary(word)
    }
}

class AddWordFromDictionary( val item : DictionaryItem)
    : Fragment(R.layout.add_word_activity), ChooseListFragment.Callbacks,CreateEditFragment.Callback {
    
    @Inject
    lateinit var createWordFomDictionaryViewModelFactory: CreateWordFomDictionaryViewModelFactory.Builder
    lateinit var viewModel: CreateWordFomDictionaryViewModel

    private var toolbar : Toolbar? = null
    private var viewPager : ViewPager2? = null
    private var actionButton : Button? = null

    private var chooseListFragment : ChooseListFragment? = null
    private var createEditFragment : CreateEditFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        PocketApplication.getAppComponent().injectAddWordFromDictionary(this)
        viewModel = ViewModelProvider(viewModelStore, createWordFomDictionaryViewModelFactory.create(item)).get(CreateWordFomDictionaryViewModel::class.java)

        toolbar = view.findViewById(R.id.toolbar)
        viewPager = view.findViewById(R.id.view_pager)
        actionButton = view.findViewById(R.id.fab_start)

        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager!!.isUserInputEnabled = false

        chooseListFragment = ChooseListFragment()
        createEditFragment = CreateEditFragment()

        viewPager!!.adapter = ViewPagerAdapter(WeakReference(chooseListFragment),WeakReference(createEditFragment),this)

        actionButton?.setOnClickListener { onActionButtonClicked() }

        subscribeViewModel()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        viewPager = null
        actionButton = null

        chooseListFragment = null
        createEditFragment = null
    }

    private fun subscribeViewModel(){
        viewModel.showedUserLists.observe(viewLifecycleOwner){
            chooseListFragment?.studyLists = it
        }
        viewModel.chinese.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.CHINESE, it!!)
        }
        viewModel.pinyin.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.PINYIN,it!!)
        }
        viewModel.translation.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.RUSSIAN,it!!)
        }
        viewModel.errorChinese.observe(viewLifecycleOwner){
            when(it){
                CreateWordFomDictionaryViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.CHINESE)
                CreateWordFomDictionaryViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.CHINESE, true)
                CreateWordFomDictionaryViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.CHINESE,true)
                CreateWordFomDictionaryViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.CHINESE,true)
            }
        }
        viewModel.errorPinyin.observe(viewLifecycleOwner){
            when(it){
                CreateWordFomDictionaryViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.PINYIN)
                CreateWordFomDictionaryViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.PINYIN, true)
                CreateWordFomDictionaryViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.PINYIN,true)
                CreateWordFomDictionaryViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.PINYIN,true)
            }
        }
        viewModel.errorTranslation.observe(viewLifecycleOwner){
            when(it){
                CreateWordFomDictionaryViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.RUSSIAN)
                CreateWordFomDictionaryViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.RUSSIAN, true)
                CreateWordFomDictionaryViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.RUSSIAN,true)
                CreateWordFomDictionaryViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.RUSSIAN,true)
            }
        }
        viewModel.updateStudyLists()
    }

    private fun onActionButtonClicked(){
        when(viewPager!!.currentItem){
            0 -> tryGoToOptions()
            1 -> copleteAdd()
        }
    }

    private fun tryGoToOptions(){
        actionButton!!.isEnabled = false

        val isNew = chooseListFragment!!.isListSelected()

        if (isNew){

            viewModel.checkUseName(chooseListFragment!!.getNewListName()!!).observe(viewLifecycleOwner){
                if (!it){
                    chooseListFragment!!.editError = getString(R.string.notifi_busy_list_name)
                }
                else{
                    viewPager!!.currentItem = 1
                }
                actionButton!!.isEnabled = true
            }
        }


    }

    private fun copleteAdd(){
        val isnew = chooseListFragment!!.isListSelected()
        actionButton!!.isEnabled = false
        val chinese = createEditFragment!!.getText(Language.CHINESE)
        val pinyin = createEditFragment!!.getText(Language.PINYIN)
        val translation = createEditFragment!!.getText(Language.RUSSIAN)
        if (isnew){
            viewModel.addToNewStudyList(chooseListFragment!!.getNewListName()!!,chinese,pinyin,translation).observe(viewLifecycleOwner){
                if (it) onAdded()
                else showDialog()
            }
        }else{
            viewModel.addToExisting(chooseListFragment!!.getSelectedListId(),chinese,pinyin,translation).observe(viewLifecycleOwner){
                if (it) onAdded()
                else showDialog()
            }
        }
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.error_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> requireActivity().finish()}
            .show()
    }

    private fun onAdded() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.success_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> requireActivity().finish()}
            .show()
    }

    override fun sumSelected(isit: Boolean) {
        val disable= viewPager?.currentItem == 0 && !isit
        actionButton!!.isEnabled = !disable
    }

    override fun afterTextChanged(language: Language, text : String) {
        when(language){
            Language.RUSSIAN -> viewModel.onTranslationTextChanged(text)
            Language.PINYIN -> viewModel.onPinyinTextChanged(text)
            Language.CHINESE -> viewModel.onPinyinTextChanged(text)
        }
    }

    override fun chineseLostFocus() {

    }

    class ViewPagerAdapter(val chooseListFragment: WeakReference<Fragment>, val createEditFragment: WeakReference<CreateEditFragment>, fragment: Fragment) :
        FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> chooseListFragment.get()!!
                1 -> createEditFragment.get()!!
                else -> throw RuntimeException()
            }
        }
    }

}

