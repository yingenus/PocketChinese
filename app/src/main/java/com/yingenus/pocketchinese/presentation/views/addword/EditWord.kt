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
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordForStudyListViewModel
import com.yingenus.pocketchinese.presentation.views.creteeditword.EditWordViewModel
import com.yingenus.pocketchinese.presentation.views.creteeditword.EditWordViewModelFactory
import com.yingenus.pocketchinese.view.Durations
import com.yingenus.pocketchinese.view.activity.SingleFragmentActivityWithKeyboard
import com.yingenus.pocketchinese.view.vibrate
import io.reactivex.rxjava3.core.Single
import java.lang.ref.WeakReference
import javax.inject.Inject

class EditWordActivity : SingleFragmentActivityWithKeyboard()  {

    companion object{
        private const val STUDY_LIST_ID = "com.yingenus.pocketchinese.presentation.views.addword.studyListId"
        private const val STUDY_WORD_ID = "com.yingenus.pocketchinese.presentation.views.addword.studyWordId"

        fun getIntent(studyListId: Long, studyWordId: Long, context: Context): Intent{
            val intent = Intent(context, EditWordActivity::class.java)
            intent.putExtra(STUDY_LIST_ID,studyListId)
            intent.putExtra(STUDY_WORD_ID,studyWordId)
            return intent
        }

        private fun getStudyListId(intent: Intent): Long{
            return intent.getLongExtra(STUDY_LIST_ID, -1L)
        }

        private fun getStudyWordId(intent: Intent): Long{
            return intent.getLongExtra(STUDY_WORD_ID, -1L)
        }
    }

    override fun createFragment(): Fragment {
        val list = getStudyListId(intent)
        val word = getStudyWordId(intent)

        return EditWordFragment(list,word)
    }
}

class EditWordFragment( val studyListId: Long,val  studyWordId: Long) : Fragment(R.layout.add_word_activity),CreateEditFragment.Callback{

    @Inject
    lateinit var editWordViewModelFactory : EditWordViewModelFactory.Factory
    private lateinit var viewModel : EditWordViewModel

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

        PocketApplication.getAppComponent().injectEditWordFragment(this)
        viewModel = ViewModelProvider(viewModelStore, editWordViewModelFactory.create(studyListId,studyWordId)).get(EditWordViewModel::class.java)

        toolbar = view.findViewById(R.id.toolbar)
        viewPager = view.findViewById(R.id.view_pager)
        actionButton = view.findViewById(R.id.fab_start)

        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager!!.isUserInputEnabled = false

        createEditFragment = CreateEditFragment()
        createEditFragment!!.callback = this

        viewPager!!.adapter =
            ViewPagerAdapter(WeakReference(createEditFragment), this)

        actionButton!!.setOnClickListener {
            onUpdateClicked()
        }

        toolbar!!.setNavigationOnClickListener {
            onNavigationClicked()
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
                EditWordViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.CHINESE)
                EditWordViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.CHINESE, true)
                EditWordViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.CHINESE,true)
                EditWordViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.CHINESE,true)
            }
        }
        viewModel.errorPinyin.observe(viewLifecycleOwner){
            when(it){
                EditWordViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.PINYIN)
                EditWordViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.PINYIN, true)
                EditWordViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.PINYIN,true)
                EditWordViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.PINYIN,true)
            }
        }
        viewModel.errorTranslation.observe(viewLifecycleOwner){
            when(it){
                EditWordViewModel.WordsError.NOTHING -> createEditFragment!!.hideError(Language.RUSSIAN)
                EditWordViewModel.WordsError.ZERO_LENGTH -> createEditFragment!!.showEmtFieldMes(Language.RUSSIAN, true)
                EditWordViewModel.WordsError.TOO_LONG -> createEditFragment!!.showMaxCharsMes(Language.RUSSIAN,true)
                EditWordViewModel.WordsError.INVALID_CHARS -> createEditFragment!!.showInvalCharsMes(Language.RUSSIAN,true)
            }
        }
        viewModel.updateView()
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

    private fun onNavigationClicked(){
        requireActivity().finish()
    }

    fun onUpdateClicked(){
        actionButton!!.isEnabled = false
        val chinese = createEditFragment!!.getText(Language.CHINESE)
        val pinyin = createEditFragment!!.getText(Language.PINYIN)
        val translation = createEditFragment!!.getText(Language.RUSSIAN)
        viewModel.update(chinese, pinyin, translation).observe(viewLifecycleOwner){
            when(it){
                EditWordViewModel.AddResult.ADDED -> requireActivity().finish()
                EditWordViewModel.AddResult.NO_REQUIRE ->{
                    YoYo.with(Techniques.Shake).duration(100L).playOn(actionButton)
                    vibrate(Durations.ERROR_DURATION, requireContext())
                    actionButton!!.isEnabled = true
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