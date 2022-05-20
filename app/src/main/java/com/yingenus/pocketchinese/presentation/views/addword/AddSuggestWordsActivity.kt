package com.yingenus.pocketchinese.presentation.views.addword

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.view.Durations
import com.yingenus.pocketchinese.view.vibrate
import java.lang.ref.WeakReference
import javax.inject.Inject

class AddSuggestWordsActivity : AppCompatActivity(), ChooseListFragment.Callbacks {

    companion object{
        private var words : List<SuggestWord> = emptyList()

        fun getIntent(addedWords : List<SuggestWord>, context: Context): Intent {
            words = addedWords
            return Intent(context, AddSuggestWordsActivity::class.java)
        }

        private fun getWordsFromIntent(intent: Intent): List<SuggestWord>{
            return words
        }
    }

    @Inject
    lateinit var addWordsViewModelFactory: AddWordsViewModelFactory.Builder
    lateinit var viewModel: AddWordsViewModel

    private var toolbar : Toolbar? = null
    private var viewPager : ViewPager2? = null
    private var actionButton : Button? = null

    private var chooseListFragment : ChooseListFragment? = null
    private var optionsAddFragment : OptionsAddFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val words = getWordsFromIntent(intent)

        PocketApplication.getAppComponent().injectAddSuggestWordsActivity(this)
        viewModel = ViewModelProvider(viewModelStore, addWordsViewModelFactory.create(words)).get(AddWordsViewModel::class.java)

        setContentView(R.layout.add_word_activity)

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.view_pager)
        actionButton = findViewById(R.id.fab_start)

        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager!!.isUserInputEnabled = false

        chooseListFragment = ChooseListFragment()
        optionsAddFragment = OptionsAddFragment()

        viewPager!!.adapter = ViewPagerAdapter(WeakReference(chooseListFragment),WeakReference(optionsAddFragment),this)

        actionButton?.setOnClickListener { onActionButtonClicked() }

        toolbar?.setNavigationOnClickListener { onNavigationClicked() }

        viewPager!!.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                when(position){
                    1 -> {
                        toolbar!!.setNavigationIcon(R.drawable.ic_next_pr_2)
                    }
                    0 -> {
                       toolbar!!.setNavigationIcon(R.drawable.ic_close_primary)
                    }
                }

            }
        })

        subscribeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStudyLists()
    }

    private fun subscribeViewModel(){
        viewModel.showedUserLists.observe(this){
            chooseListFragment?.studyLists = it
        }
        viewModel.error.observe(this){
            showDialog()
        }
    }

    private fun onNavigationClicked(){
        when(viewPager!!.currentItem){
            0 -> finish()
            1 -> viewPager!!.currentItem = 0
        }
    }

    private fun onActionButtonClicked(){
        when(viewPager!!.currentItem){
            0 -> tryGoToOptions()
            1 -> copleteAdd()
        }
    }

    private fun tryGoToOptions(){

        if (!chooseListFragment!!.isSmhSelected()){
            YoYo.with(Techniques.Shake).duration(100L).playOn(actionButton)
            vibrate(Durations.ERROR_DURATION, this)
        }
        else{
            actionButton!!.isEnabled = false

            val isNew = !chooseListFragment!!.isListSelected()

            if (isNew){
                viewModel.checkUseName(chooseListFragment!!.getNewListName()!!).observe(this){
                    if (it){
                        chooseListFragment!!.editError = getString(R.string.notifi_busy_list_name)
                    }
                    else{
                        viewPager!!.currentItem = 1
                    }
                }
            }
            else{
                viewPager!!.currentItem = 1
            }
            actionButton!!.isEnabled = true
        }

    }

    private fun copleteAdd(){
        val isnew = !chooseListFragment!!.isListSelected()
        viewModel.mixWords(optionsAddFragment!!.mixWords)
        actionButton!!.isEnabled = false
        viewModel.simplifyPinyin(optionsAddFragment!!.simplifyPinyin)
        if (isnew){
            viewModel.addToNewStudyList(chooseListFragment!!.getNewListName()!!).observe(this){
                if (it) onAdded()
                else showDialog()
            }
        }else{
            viewModel.addToExisting(chooseListFragment!!.getSelectedListId()).observe(this){
                if (it) onAdded()
                else showDialog()
            }
        }
    }

    private fun showDialog(){
        AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_PocketChinese_AlertDialog))
            .setMessage(getString(R.string.error_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> this.finish()}
            .show()
    }

    private fun onAdded() {
        AlertDialog.Builder(ContextThemeWrapper(this,R.style.Theme_PocketChinese_AlertDialog))
            .setTitle(getString(R.string.success_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> finish()}
            .show()
    }

    override fun sumSelected(isit: Boolean) {
        val disable= viewPager?.currentItem == 0 && !isit
        actionButton!!.isEnabled = !disable
    }

    class ViewPagerAdapter(val chooseListFragment: WeakReference<Fragment>, val optionsAddFragment: WeakReference<OptionsAddFragment>, activity: FragmentActivity) :
        FragmentStateAdapter(activity){
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> chooseListFragment.get()!!
                1 -> optionsAddFragment.get()!!
                else -> throw RuntimeException()
            }
        }
    }

}

