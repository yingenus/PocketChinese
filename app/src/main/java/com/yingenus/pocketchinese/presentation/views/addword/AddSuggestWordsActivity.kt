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
import com.yingenus.pocketchinese.domain.dto.SuggestWord
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


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

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

        subscribeViewModel()

    }

    private fun subscribeViewModel(){
        viewModel.showedUserLists.observe(this){
            chooseListFragment?.studyLists = it
        }
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
            viewModel.checkUseName(chooseListFragment!!.getNewListName()!!).observe(this){
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
        MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.error_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> this.finish()}
            .show()
    }

    private fun onAdded() {
        MaterialAlertDialogBuilder(applicationContext)
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

