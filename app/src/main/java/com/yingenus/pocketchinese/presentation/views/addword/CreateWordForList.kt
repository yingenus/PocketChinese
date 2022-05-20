package com.yingenus.pocketchinese.presentation.views.addword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordFomDictionaryViewModel
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordForStudyListViewModel
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordForStudyListViewModelFragment
import com.yingenus.pocketchinese.view.Durations
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import com.yingenus.pocketchinese.view.vibrate
import java.lang.ref.WeakReference
import javax.inject.Inject

class CreateWordForList : SingleFragmentActivityWithKeyboard() {

    companion object{
        const val STUDY_LIST_ID = "com.yingenus.pocketchinese.presentation.views.addword.createWordForListFragment"

        fun getIntent(studyListId : Long, context : Context): Intent {
            val intent = Intent(context, CreateWordForList::class.java)
            intent.putExtra( STUDY_LIST_ID,studyListId)
            return intent
        }
        private fun getStudyListId(intent: Intent): Long{
            return intent.getLongExtra(STUDY_LIST_ID, -1L)
        }
    }

    override fun createFragment(): Fragment {
        return CreateWordForListFragment(getStudyListId(intent))
    }
}

class CreateWordForListFragment(val studyListId : Long ) :
    Fragment(R.layout.add_word_activity),CreateEditFragment.Callback{

    @Inject
    lateinit var createWordForStudyListViewModelFragment:  CreateWordForStudyListViewModelFragment.Builder
    private lateinit var viewModel : CreateWordForStudyListViewModel

    private var toolbar : Toolbar? = null
    private var viewPager : ViewPager2? = null
    private var actionButton : Button? = null


    private var createEditFragment : CreateEditFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        PocketApplication.getAppComponent().injectCreateWordForListFragment(this)
        viewModel = ViewModelProvider(viewModelStore, createWordForStudyListViewModelFragment.create(studyListId)).get(CreateWordForStudyListViewModel::class.java)

        toolbar = view.findViewById(R.id.toolbar)
        viewPager = view.findViewById(R.id.view_pager)
        actionButton = view.findViewById(R.id.fab_start)

        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager!!.isUserInputEnabled = false

        createEditFragment = CreateEditFragment()
        createEditFragment!!.callback = this

        viewPager!!.adapter = ViewPagerAdapter(WeakReference(createEditFragment),this)

        actionButton!!.setOnClickListener {
            onCreateClicked()
        }

        toolbar!!.setNavigationOnClickListener {
            requireActivity().finish()
        }

        subscribeViewModer()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        viewPager = null
        actionButton = null
        createEditFragment!!.callback = null
        createEditFragment = null
    }

    fun subscribeViewModer(){
        viewModel.chinese.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.CHINESE,it)
        }
        viewModel.pinyin.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.PINYIN,it)
        }
        viewModel.translation.observe(viewLifecycleOwner){
            createEditFragment!!.setText(Language.RUSSIAN,it)
        }
        viewModel.errorChinese.observe(viewLifecycleOwner){
            when(it){
                CreateWordForStudyListViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.CHINESE)
                CreateWordForStudyListViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.CHINESE, true)
                CreateWordForStudyListViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.CHINESE,true)
                CreateWordForStudyListViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.CHINESE,true)
            }
        }
        viewModel.errorPinyin.observe(viewLifecycleOwner){
            when(it){
                CreateWordForStudyListViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.PINYIN)
                CreateWordForStudyListViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.PINYIN, true)
                CreateWordForStudyListViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.PINYIN,true)
                CreateWordForStudyListViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.PINYIN,true)
            }
        }
        viewModel.errorTranslation.observe(viewLifecycleOwner){
            when(it){
                CreateWordForStudyListViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.RUSSIAN)
                CreateWordForStudyListViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.RUSSIAN, true)
                CreateWordForStudyListViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.RUSSIAN,true)
                CreateWordForStudyListViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.RUSSIAN,true)
            }
        }
        viewModel.updateStudyList()
    }

    override fun afterTextChanged(language: Language, text: String) {
        when(language){
            Language.CHINESE -> viewModel.onChineseTextChanged(text)
            Language.PINYIN -> viewModel.onPinyinTextChanged(text)
            Language.RUSSIAN -> viewModel.onTranslationTextChanged(text)
        }
    }

    override fun chineseLostFocus() {

    }

    private fun onCreateClicked(){
        val chinese = createEditFragment!!.getText(Language.CHINESE)
        val pinyin = createEditFragment!!.getText(Language.PINYIN)
        val translation = createEditFragment!!.getText(Language.RUSSIAN)
        actionButton!!.isEnabled = false
        viewModel.add(chinese,pinyin, translation).observe(viewLifecycleOwner){
            when(it){
                CreateWordForStudyListViewModel.AddResult.ADDED -> requireActivity().finish()
                CreateWordForStudyListViewModel.AddResult.NO_REQUIRE -> {
                    YoYo.with(Techniques.Shake).duration(100L).playOn(actionButton)
                    vibrate(Durations.ERROR_DURATION, requireContext())
                    actionButton!!.isEnabled = true
                }
                CreateWordForStudyListViewModel.AddResult.ERROR ->{

                }
            }
        }
    }

    class ViewPagerAdapter(
        private val createEditFragment : WeakReference<CreateEditFragment>,
        fragment: Fragment): FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> createEditFragment.get()!!
                else -> throw RuntimeException()
            }
        }
    }
}