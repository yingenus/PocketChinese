package com.yingenus.pocketchinese.presentation.views.stydylist

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yingenus.multipleprogressbar.MultipleProgressBar
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.CardBoundLeftRight
import com.yingenus.pocketchinese.controller.CardBoundTopBottom
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams.resolveColorAttr
import com.yingenus.pocketchinese.presentation.dialogs.ActioneSheetDialog
import com.yingenus.pocketchinese.presentation.dialogs.RenameDialog
import com.yingenus.pocketchinese.presentation.dialogs.StartTrainingSheetDialog
import com.yingenus.pocketchinese.presentation.dialogs.UserListRenameDialog
import com.yingenus.pocketchinese.presentation.views.addword.CreateWordForList
import com.yingenus.pocketchinese.presentation.views.addword.EditWordActivity
import com.yingenus.pocketchinese.presentation.views.userlist.StudyListAdapter
import com.yingenus.pocketchinese.view.HeadersRecyclerViewAdapter
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import java.util.*
import javax.inject.Inject

class StudyListActivity : AppCompatActivity(),WordAdapter.OnWordClicked,WordAdapter.OnWordLongClicked, AppBarLayout.OnOffsetChangedListener {

    companion object{
        private const val INNER_STUDY_LIST="com.pocketchinese.com.studylist.studyword"

        fun getIntent(context: Context, studyListId: Long): Intent {
            val intent= Intent(context, StudyListActivity::class.java)
            intent.putExtra(INNER_STUDY_LIST, studyListId)
            return intent
        }

        private fun getStudyListIdFromIntent(intent : Intent) : Long{
            val id =  intent.getLongExtra(INNER_STUDY_LIST, -1)
            return id
        }
    }

    class StartTrainFF( var studyListId : Long) : FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return if (className == StartTrainingSheetDialog::class.java.name) StartTrainingSheetDialog(
                studyListId
            ) else super.instantiate(classLoader, className)
        }
    }

    @Inject
    lateinit var userStudyListViewModelFactoryBuilder: UserStudyListViewModelFactory.Builder
    private lateinit var viewModel: UserStudyListViewModel

    @DrawableRes
    private val notifyIcon = R.drawable.on_off_notify

    private var toolbar : Toolbar? = null
    private var progressBar: MultipleProgressBar? = null
    private var percentChn : TextView? = null
    private var percentTrn : TextView? = null
    private var percentPin : TextView? = null
    private var extendedButton : ExtendedFloatingActionButton? = null
    private var clearButton : Button? = null
    private var addButton : Button? = null
    private var lastRepeat: TextView? = null
    private var nextRepeat: TextView? = null
    private var repeatCount: TextView? = null
    private var addedWords: TextView? = null
    private var shouldRepeatNotify : View? = null
    private var appBarLayout : AppBarLayout? = null


    private var wordsRecyclerView : RecyclerView? = null

    private var studyListId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {

        val id = getStudyListIdFromIntent(intent)
        studyListId = if (id == -1L &&savedInstanceState != null) {
            savedInstanceState.getLong(INNER_STUDY_LIST)
        }else{
            id
        }

        supportFragmentManager.setFragmentFactory(StartTrainFF(studyListId))

        super.onCreate(savedInstanceState)

        setContentView(R.layout.study_list_fragment)

        PocketApplication.getAppComponent().injectStudyListActivity(this)
        viewModel = ViewModelProvider(viewModelStore, userStudyListViewModelFactoryBuilder.create(id)).get(UserStudyListViewModel::class.java)

        toolbar = findViewById(R.id.toolbar)
        progressBar = findViewById(R.id.progress_bar)
        percentChn = findViewById(R.id.chn_success)
        percentPin = findViewById(R.id.pin_success)
        percentTrn = findViewById(R.id.trn_success)
        lastRepeat = findViewById(R.id.last_repeat)
        nextRepeat = findViewById(R.id.next_repeat)
        repeatCount = findViewById(R.id.repeat_count)
        addedWords = findViewById(R.id.added_words)
        clearButton = findViewById(R.id.clear_statistic)
        addButton  = findViewById(R.id.add)
        shouldRepeatNotify = findViewById(R.id.notify_view)
        wordsRecyclerView = findViewById(R.id.expanded_recyclerview)
        extendedButton = findViewById(R.id.fab_start)

        appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout!!.addOnOffsetChangedListener( this)

        toolbar!!.setNavigationOnClickListener{ view: View? ->
            onNavigationClicked(view)
        }
        toolbar!!.setOnMenuItemClickListener { item : MenuItem ->
            onMenuItemClick(item)
        }

        wordsRecyclerView!!.layoutManager = LinearLayoutManager(this)
            .also { it.orientation = RecyclerView.VERTICAL }
        wordsRecyclerView!!.adapter = WordAdapter().also {
            it.setOnClickListener(this)
            it.setOnLongClickListener(this)
        }

        extendedButton!!.setOnClickListener { onStartTrainCkicked(it) }

        addButton!!.setOnClickListener {
            val intent = CreateWordForList.getIntent(studyListId,this)
            startActivity(intent)
        }
        clearButton!!.setOnClickListener {
            onStatisticClearClicked()
        }

        subscribeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStudyListInfo()
        viewModel.updateStudyWords()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val ff: FragmentFactory = supportFragmentManager.fragmentFactory
        if (ff is StartTrainFF) {
            val id = ff.studyListId
            outState.putLong(INNER_STUDY_LIST, id)
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        when(verticalOffset){
            0 -> setElevation(0)
            in -1 downTo -10 -> setElevation(1)
            in -11 downTo -20 -> setElevation(2)
            in -21 downTo -30 ->setElevation(3)
            else -> setElevation(4)
        }
    }

    fun setElevation( offset : Int){
        val pix = dp2px(offset, this@StudyListActivity)

        if (toolbar!!.elevation.toInt() != pix){
            toolbar!!.elevation = pix.toFloat()
        }
    }

    private fun subscribeViewModel(){
        viewModel.studyListName.observe(this){
            toolbar!!.title = it
        }
        viewModel.progressChinese.observe(this){
            if (it == -1) {
                progressBar!!.getProgressItemById(R.id.progress_chn)!!.setProgress(1)
                percentChn!!.text = "NoN"
            }
            else {
                progressBar!!.getProgressItemById(R.id.progress_chn)!!.setProgress(it)
                percentChn!!.text = "$it%"
            }
        }
        viewModel.progressPinyin.observe(this){
            if (it == -1) {
                progressBar!!.getProgressItemById(R.id.progress_pin)!!.setProgress(1)
                percentPin!!.text = "NoN"
            }
            else {
                progressBar!!.getProgressItemById(R.id.progress_pin)!!.setProgress(it)
                percentPin!!.text = "$it%"
            }
        }
        viewModel.progressTranslation.observe(this){
            if (it == -1) {
                progressBar!!.getProgressItemById(R.id.progress_trn)!!.setProgress(1)
                percentTrn!!.text = "NoN"
            }
            else {
                progressBar!!.getProgressItemById(R.id.progress_trn)!!.setProgress(it)
                percentTrn!!.text = "$it%"
            }
        }
        viewModel.showProgressChinese.observe(this){
            if (!it) percentChn!!.text = "--%"
        }
        viewModel.showProgressPinyin.observe(this){
            if (!it) percentPin!!.text = "--%"
        }
        viewModel.showProgressTranslation.observe(this){
            if (!it) percentTrn!!.text = "--%"
        }
        viewModel.addedWords.observe(this){
            addedWords!!.text = it.toString()
        }
        viewModel.lastRepeat.observe(this){
            lastRepeat!!.text = it
        }
        viewModel.nextRepeat.observe(this){
            nextRepeat!!.text = it
        }
        viewModel.repeatedWords.observe(this){
            repeatCount!!.text = it.toString()
        }
        viewModel.repeatRecomend.observe(this){
            if (it == RepeatRecomend.DONT_NEED) {
                shouldRepeatNotify!!.visibility = View.GONE
                UtilsVariantParams.apply {
                    nextRepeat!!.setTextColor( this@StudyListActivity.resolveColorAttr(android.R.attr.textColorPrimary))
                }
            }
            else{
                shouldRepeatNotify!!.visibility = View.VISIBLE
                if(it != RepeatRecomend.FIRST) nextRepeat!!.setTextColor( this.resources.getColor(R.color.notify_color) )
            }

        }
        viewModel.notifyUsers.observe(this){
            setMenuItemNotify(it)
            if (toolbar != null) {
                val item = toolbar!!.menu.findItem(R.id.notify)
                if (item != null) {
                    item.isChecked = it
                }
            }
        }
        viewModel.repeadRecomedStudyWords.observe(this){
            val adapter = (wordsRecyclerView!!.adapter as WordAdapter)
            adapter.setNeed(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.otherRecomedStudyWords.observe(this){
            val adapter = (wordsRecyclerView!!.adapter as WordAdapter)
            adapter.setOther(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        progressBar = null
        percentChn = null
        percentTrn = null
        percentPin = null
        lastRepeat = null
        nextRepeat = null
        repeatCount = null
        addedWords = null
        clearButton = null
        addButton  = null
        shouldRepeatNotify = null
        extendedButton = null
        appBarLayout!!.removeOnOffsetChangedListener(this)
        (wordsRecyclerView!!.adapter as WordAdapter).also {
            it.deleteOnClickListener(this)
            it.deleteOnLongClickListener(this)
        }
        appBarLayout = null
        wordsRecyclerView = null
    }

    fun onStatisticClearClicked(){
        val dialog= AlertDialog.Builder(this)
        var showedDialog : AlertDialog? = null

        dialog.setMessage(getString(R.string.clear_statistic_msg))

        dialog.setPositiveButton(R.string.delete) { _: DialogInterface, _:Int ->
            val isCleared = viewModel.clearStatistics()
            isCleared.observe(this){
                if (it) showedDialog?.dismiss()
                else showedDialog?.dismiss()
            }
        }
        dialog.setNegativeButton(R.string.cancel) { _: DialogInterface, _:Int ->
            showedDialog?.dismiss()
        }

        showedDialog = dialog.show()
    }

    fun onStartTrainCkicked( v : View){
        val ff: FragmentFactory = supportFragmentManager.fragmentFactory

        ff.instantiate(this.classLoader, StartTrainingSheetDialog::class.java.name)
        if (ff is StartTrainFF) {
            ff.studyListId = studyListId
        }
        val dialog = ff.instantiate(
            this.classLoader,
            StartTrainingSheetDialog::class.java.name
        ) as DialogFragment
        dialog.show(supportFragmentManager,"training_dialog")
    }

    override fun onLongClicked(showedStudyWord: ShowedStudyWord) {
        val bottomSheetDialog = ActioneSheetDialog()
        bottomSheetDialog.show(supportFragmentManager, "action_dialog")

        bottomSheetDialog.observer = object : ActioneSheetDialog.EditSheetDialogCallback {
            override fun onRemove() {
                viewModel.deleteStudyWord(showedStudyWord)
            }

            override fun onRename() {
                val intent = EditWordActivity.getIntent(studyListId,showedStudyWord.id,this@StudyListActivity)
                startActivity(intent)
            }
        }
    }

    override fun onClicked(showedStudyList: ShowedStudyWord) {

    }

    private fun onNavigationClicked(view: View?) {
        finish()
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.notify) {
            val notify = item.isChecked
            viewModel.setNotify(!notify).observe(this){
                if (it){
                    item.isChecked = !notify
                    setMenuItemNotify(!notify)
                }
            }
            return true
        }
        if (item.itemId == R.id.rename){
            val dialog = UserListRenameDialog(viewModel)
            dialog.show(supportFragmentManager, "rename_dialog")
        }
        return false
    }

    private fun setMenuItemNotify(isChecked: Boolean) {
        val stateDrawable =
            resources.getDrawable(notifyIcon, getTheme()) as StateListDrawable
        val state =
            intArrayOf(if (isChecked) android.R.attr.state_checked else android.R.attr.state_empty)
        stateDrawable.state = state
        val item = toolbar!!.menu.findItem(R.id.notify)
        if (item != null) {
            item.icon = stateDrawable.current
        }
    }
}

class WordAdapter : HeadersRecyclerViewAdapter<WordViewHolder>(
    listOf(TAG_NEED, TAG_OTHER),
    false
){
    companion object{
        private const val TAG_NEED = "need"
        private const val TAG_OTHER = "other"
    }

    interface OnWordLongClicked{
        fun onLongClicked( showedStudyWord: ShowedStudyWord)
    }
    interface OnWordClicked{
        fun onClicked( showedStudyList: ShowedStudyWord)
    }

    private val longListeners : MutableList<OnWordLongClicked> = mutableListOf()
    private val listeners : MutableList<OnWordClicked> = mutableListOf()

    private var needList : List<ShowedStudyWord> = emptyList()
    private var otherList : List<ShowedStudyWord> = emptyList()

    fun setNeed(lists : List<ShowedStudyWord>){
        needList = lists
    }

    fun setOther(lists : List<ShowedStudyWord>){
        otherList = lists
    }

    fun setOnLongClickListener(listener : OnWordLongClicked){
        longListeners.add(listener)
    }

    fun deleteOnLongClickListener(listener : OnWordLongClicked){
        longListeners.remove(listener)
    }

    fun setOnClickListener(listener : OnWordClicked){
        listeners.add(listener)
    }

    fun deleteOnClickListener(listener : OnWordClicked){
        listeners.remove(listener)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): WordViewHolder {
        return WordViewHolder(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            parent
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewViewHolder(R.layout.list_header,
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    }

    override fun onBindItemViewHolder(
        holder: WordViewHolder,
        position: Int,
        tag: String
    ) {
        when(tag){
            TAG_NEED -> {
                val item = needList[position]
                holder.bind(item)
                holder.itemView.setOnLongClickListener {
                    longListeners.forEach { it.onLongClicked(item) }
                    true
                }
                holder.itemView.setOnClickListener {
                    listeners.forEach { it.onClicked(item) }
                }
            }
            TAG_OTHER -> {
                val item = otherList[position]
                holder.bind(otherList[position])
                holder.itemView.setOnLongClickListener {
                    longListeners.forEach { it.onLongClicked(item) }
                    true
                }
                holder.itemView.setOnClickListener {
                    listeners.forEach { it.onClicked(item) }
                }
            }
            else -> throw RuntimeException("unexpected tag : $tag")
        }
    }

    override fun onBindHeaderViewHolder(holder: ViewViewHolder, tag: String) {
        when(tag){
            TAG_NEED -> holder.itemView.findViewById<TextView>(R.id.title).text =
                holder.itemView.context.getString(R.string.need_repeat_list)
            TAG_OTHER -> holder.itemView.findViewById<TextView>(R.id.title).text =
                holder.itemView.context.getString(R.string.words_other)
        }
    }

    override fun getItemCount(tag: String): Int {
        return when(tag){
            TAG_NEED -> needList.size
            TAG_OTHER -> otherList.size
            else -> throw RuntimeException("unexpected tag : $tag")
        }
    }
}

class WordViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.study_word,parent,false)){

        private val chin : TextView
        private val pin : TextView
        private val trn : TextView
        private val success : TextView
        private val check : CheckBox

        init {
            chin = itemView.findViewById(R.id.dictionary_item_chin_text)
            pin = itemView.findViewById(R.id.dictionary_item_pin_text)
            trn = itemView.findViewById(R.id.dictionary_item_second_language)
            success = itemView.findViewById(R.id.counter)
            check = itemView.findViewById(R.id.selected_box)
        }
        fun bind(studyWord: ShowedStudyWord){
            chin.text = studyWord.chinese
            pin.text = studyWord.pinyin
            trn.text = studyWord.translate
            success.text = studyWord.wordSuccess.toString() +"/10"
        }
}