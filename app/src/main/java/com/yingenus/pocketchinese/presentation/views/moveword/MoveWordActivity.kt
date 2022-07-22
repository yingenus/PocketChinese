package com.yingenus.pocketchinese.presentation.views.moveword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.presentation.views.addword.ChooseListFragment
import com.yingenus.pocketchinese.view.Durations
import com.yingenus.pocketchinese.view.vibrate
import java.lang.ref.WeakReference
import javax.inject.Inject

class MoveWordActivity : AppCompatActivity() {

    companion object {
        private val MOVEWORDACTIVITY_STUDYWORD_ID = "com.yingenus.pocketchinese.presentation.views.moveword.MoveWordActivity.studyWordID"

        fun getIntent(context : Context, studyWord: ShowedStudyWord): Intent {
            val intent = Intent(context, MoveWordActivity::class.java)
            intent.putExtra(MOVEWORDACTIVITY_STUDYWORD_ID, studyWord.id)
            return intent
        }

        private fun getStudyWordId( bundle: Bundle): Long?{
            return if(!bundle.containsKey(MOVEWORDACTIVITY_STUDYWORD_ID)) null
            else bundle.getLong(MOVEWORDACTIVITY_STUDYWORD_ID)
        }

        private fun getStudyWordId( intent : Intent): Long?{
            return if(!intent.hasExtra(MOVEWORDACTIVITY_STUDYWORD_ID)) null
            else intent.getLongExtra(MOVEWORDACTIVITY_STUDYWORD_ID,0)
        }

    }

    @Inject
    lateinit var moveWordViewModelFactory: MoveWordViewModelFactory.Factory
    lateinit var viewModel : MoveWordViewModel

    private var toolbar : Toolbar? = null
    private var viewPager : ViewPager2? = null
    private var actionButton : Button? = null

    private var chooseListFragment: ChooseListFragment? = null
    private var moveWordOptionsFragment : MoveWordOptionsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val studyWordId = getStudyWordId(intent)
        requireNotNull(studyWordId){ "cant extract StudyWordId" }

        PocketApplication.getAppComponent().injectMoveWordActivity(this)
        viewModel = ViewModelProvider(viewModelStore,moveWordViewModelFactory.create(studyWordId)).get(MoveWordViewModel::class.java)

        setContentView(R.layout.add_word_activity)

        toolbar = findViewById(R.id.toolbar)
        viewPager = findViewById(R.id.view_pager)
        actionButton = findViewById(R.id.fab_start)

        viewPager!!.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager!!.isUserInputEnabled = false

        chooseListFragment = ChooseListFragment()
        moveWordOptionsFragment = MoveWordOptionsFragment()

        viewPager!!.adapter = MoveWordActivityViewPagerAdapter(
            WeakReference(chooseListFragment),
            WeakReference(moveWordOptionsFragment),
            this
        )

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

        toolbar!!.setNavigationOnClickListener { onNavigationClicked() }

        actionButton?.setOnClickListener { onActionButtonClicked() }

        subscribeViewModel()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager!!.adapter = null
        chooseListFragment = null
        moveWordOptionsFragment = null
        toolbar = null
        viewPager = null
        actionButton = null
    }

    private fun subscribeViewModel(){
        viewModel.showedUserLists.observe(this){
            chooseListFragment!!.studyLists = it
        }
        viewModel.updateView()
    }

    private fun onActionButtonClicked(){
        when(viewPager!!.currentItem){
            0 -> tryGoToOptions()
            1 -> completeMove()
        }
    }

    private fun onNavigationClicked(){
        when(viewPager!!.currentItem){
            0 -> finish()
            1 -> viewPager!!.currentItem = 0
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
            }else{
                viewPager!!.currentItem = 1
            }
            actionButton!!.isEnabled = true
        }
    }

    private fun completeMove(){
        val isnew = !chooseListFragment!!.isListSelected()
        actionButton!!.isEnabled = false

        if (isnew){
            viewModel
                .moveToNewStudyList(chooseListFragment!!.getNewListName()!!, moveWordOptionsFragment!!.saveStatistics)
                .observe(this){
                    when(it){
                        MoveWordViewModel.AddResult.ADDED -> onAdded()
                        MoveWordViewModel.AddResult.NO_REQUIRE -> {
                            YoYo.with(Techniques.Shake).duration(100L).playOn(actionButton)
                            vibrate(Durations.ERROR_DURATION, this)
                            actionButton!!.isEnabled = true
                        }
                        MoveWordViewModel.AddResult.ERROR -> showDialog()
                    }
                }
        }else{
            viewModel
                .moveToExistingStudyList(chooseListFragment!!.getSelectedListId(),moveWordOptionsFragment!!.saveStatistics)
                .observe(this){
                    when(it){
                        MoveWordViewModel.AddResult.ADDED -> onAdded()
                        MoveWordViewModel.AddResult.NO_REQUIRE -> {
                            YoYo.with(Techniques.Shake).duration(100L).playOn(actionButton)
                            vibrate(Durations.ERROR_DURATION, this)
                            actionButton!!.isEnabled = true
                        }
                        MoveWordViewModel.AddResult.ERROR -> showDialog()
                    }
                }
        }
    }

    private fun showDialog(){
        MaterialAlertDialogBuilder(this)
            .setMessage(getString(R.string.error_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> finish()}
            .show()
    }

    private fun onAdded() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.success_insert_words))
            .setPositiveButton(android.R.string.cancel) { _, _-> finish()}
            .show()
    }

    class MoveWordActivityViewPagerAdapter(
        private val chooseListFragment: WeakReference<ChooseListFragment>,
        private val moveWordOptionsFragment: WeakReference<MoveWordOptionsFragment>,
        activity: MoveWordActivity
    ) : FragmentStateAdapter(activity){
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> chooseListFragment.get()!!
                1 -> moveWordOptionsFragment.get()!!
                else -> throw RuntimeException()
            }
        }
    }

}