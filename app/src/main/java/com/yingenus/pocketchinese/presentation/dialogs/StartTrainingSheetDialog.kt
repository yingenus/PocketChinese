package com.yingenus.pocketchinese.presentation.dialogs

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewpager2.widget.ViewPager2
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.presentation.views.train.TrainActivity
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.controller.getDisplayHeight
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import com.yingenus.pocketchinese.domain.entitiys.LanguageCase
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.TrainedWords
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import java.util.*
import javax.inject.Inject

class StartTrainingSheetDialog(val studyListId: Long) : BottomSheetDialogFragment() {
    private object Helper{
        const val OccupiHeight = 0.9f
    }

    private val pagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            this@StartTrainingSheetDialog.onPageSelected(position)
        }
    }

    @Inject
    lateinit var startTrainingViewModelFactory : StartTrainingViewModelFactory.Builder
    private lateinit var viewModel: StartTrainingViewModel

    private lateinit var toolbar: Toolbar
    private lateinit var viewPager : ViewPager2

    //private lateinit var statistic: StudyListRepeatStatistic
    //private lateinit var repeatType: RepeatType

    //private lateinit var wordDAO: StudyWordDAO

    private var wordsAdapter: ChooseWordsAdapter? = null
    private var typeAdapter: ChooseTypeAdapter? = null

    private var language : Language = Language.CHINESE
    private var mTrainedWords : TrainingConf.TrainingWords = TrainingConf.TrainingWords.ALL
    private var mBlock : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PocketApplication.getAppComponent().injectStartTrainingDialog(this)
        viewModel = ViewModelProvider(viewModelStore, startTrainingViewModelFactory.create(studyListId)).get(StartTrainingViewModel::class.java)

        //val sqlDb = PocketDBOpenManger.getHelper(requireContext()).writableDatabase
        //wordDAO = StudyWordDAO(sqlDb)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        toolbar = Toolbar(inflater.context)
        toolbar.setNavigationIcon(R.drawable.ic_chevrone_left)
        toolbar.setNavigationOnClickListener { v -> onNavigationClicked(v) }
        toolbar.elevation = dp2px(1,requireContext()).toFloat()

        viewPager = ViewPager2(inflater.context)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(pagerCallback)

        val containerLayout = LinearLayout(inflater.context)
        containerLayout.orientation = LinearLayout.VERTICAL

        val toolbarLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(56,inflater.context))

        containerLayout.addView(toolbar,toolbarLayoutParam)

        val viewPagerLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getPagerOccupiHeight(dp2px(56,inflater.context)))

        containerLayout.addView(viewPager,viewPagerLayoutParams)

        val nestedScrollView = NestedScrollView(inflater.context)
        nestedScrollView.setBackgroundResource(R.drawable.sheet_dialog_shape)

        val containerLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)

        nestedScrollView.addView(containerLayout,containerLayoutParams)
        nestedScrollView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        //loadStatistic()

        initViewPagerAdapter()

        val  behavior = (dialog as BottomSheetDialog).behavior
        behavior.peekHeight = getPagerOccupiHeight(dp2px(0,inflater.context))
        behavior.isDraggable = false

        subscribeViewModel()

        return nestedScrollView
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStatistic()
    }

    fun subscribeViewModel(){
        viewModel.showChinese.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.showChinese = it
            adapter.notifyDataSetChanged()
        }
        viewModel.showPinyin.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.showPinyin = it
            adapter.notifyDataSetChanged()
        }
        viewModel.showTranslation.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.showTranslation = it
            adapter.notifyDataSetChanged()
        }
        viewModel.statisticChinese.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.trainedChinese = it
            adapter.notifyDataSetChanged()
        }
        viewModel.statisticPinyin.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.trainedPinyin = it
            adapter.notifyDataSetChanged()
        }
        viewModel.statisticTranslation.observe(viewLifecycleOwner){
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter
            adapter!!.trainedTranslation = it
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        typeAdapter?.onClickListener = null
        //blockAdapter?.onClickListener = null
        typeAdapter = null
        //blockAdapter = null
        viewPager.unregisterOnPageChangeCallback(pagerCallback)
        //wordDAO.finish()
        //PocketDBOpenManger.releaseHelper()
    }

    fun onPageSelected(position: Int){
        if (position == 0)
            toolbar.title = getString(R.string.chose_type)
        if (position ==1) {
            toolbar.title = getString(R.string.chose_block)
            //updateBlocks()
        }
    }

    private fun getPagerOccupiHeight(marginTopPix : Int): Int{
        val displayHeight = getDisplayHeight(requireContext())
        val statusBarHeight = getStatusBarHeight()
        val availableSpace = displayHeight - statusBarHeight
        val dialogHeight = (availableSpace* Helper.OccupiHeight).toInt()

        return dialogHeight - marginTopPix
    }

    private fun getStatusBarHeight(): Int{
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun onWordClicked( v: View){
       val position = v.getTag(R.id.holder_position)
        if (position != null){
            if (position == 0) mTrainedWords = TrainingConf.TrainingWords.ALL
            if (position == 1) mTrainedWords = TrainingConf.TrainingWords.ONLY_NEED
            //mBlock = position as Int
            startTraining()
        }
    }


    //private fun onBlockClicked( v: View){
     //   val position = v.getTag(R.id.holder_position)
    //    if (position != null){
    //        mBlock = position as Int
    //        startTraining()
    //    }
    //}
    private fun onTypeClicked(v: View){
        val position = v.getTag(R.id.holder_position)

        if(position != null){
            when(position as Int){
                0 -> language = Language.CHINESE
                1 -> language = Language.PINYIN
                2 -> language = Language.RUSSIAN
            }
            val adapter = (viewPager.adapter as ViewPagerAdapter).chooseTypeAdapter!!

            val trainedWords =
                when(position){
                    0 -> adapter.trainedChinese
                    1 -> adapter.trainedPinyin
                    2 -> adapter.trainedTranslation
                    else -> throw java.lang.RuntimeException()
                }
            if (trainedWords.filed + trainedWords.repeatable == 0){
                startTraining()
            }
            else{
                (viewPager.adapter as ViewPagerAdapter).chooseWordsAdapter!!.trainedWords = trainedWords
                viewPager.currentItem = 1
            }
        }
    }

    private fun onNavigationClicked(v: View){
        if(viewPager.currentItem == 1)
            viewPager.currentItem = 0
        else
            dialog!!.dismiss()
    }

    private fun startTraining(){

        val config : TrainingConf = TrainingConf(language,mTrainedWords,studyListId)

        val intent = TrainActivity.getIntent(config,requireContext())
        requireActivity().startActivity(intent)
        dialog!!.dismiss()
    }

    //private fun loadStatistic(){
     //   repeatType = Settings.getRepeatType(requireContext())

    //    val observer =
    //            Single.create { emitter: SingleEmitter<Map<Int, List<StudyWord>>> ->
    //        emitter.onSuccess(wordDAO.getAllInSorted(studyListUUID)) }
     //   observer.observeOn(Schedulers.io())
     //   val statisticObserver = observer
     //           .map { integerListMap -> createStatistic(integerListMap) }
    //        statisticObserver
     //           .subscribeOn(AndroidSchedulers.mainThread())
    //            .subscribe{ onSuccess ->
     //               statistic = onSuccess
    //                initViewPagerAdapter()
    //            }
   //}

    //private fun updateBlocks(){
    //    blockAdapter?.blocksStat = getBlockStat()
    //    blockAdapter?.notifyDataSetChanged()
    //}

    private fun initViewPagerAdapter(){
        //val typeStat = listOf(statistic.chnState, statistic.pinState, statistic.trnState)
        //val blocksStat =getBlockStat()
        //val repeatType = repeatType

        typeAdapter = ChooseTypeAdapter()
        typeAdapter?.onClickListener = View.OnClickListener { v -> onTypeClicked(v) }
        wordsAdapter = ChooseWordsAdapter(TrainedWords(0,0,0))
        wordsAdapter?.onClickListener = View.OnClickListener { v -> onWordClicked(v) }

        viewPager.adapter = ViewPagerAdapter(typeAdapter,wordsAdapter)
    }

    //private fun getBlockStat():ArrayList<StudyListRepeatStatistic.State>{
    //   val blocksStat = ArrayList<StudyListRepeatStatistic.State>()
    //    when (mLang) {
    //        LanguageCase.Chin -> {
    //            blocksStat.add(statistic.chnState)
    //            blocksStat.addAll(statistic.chnBlockState)
    //        }
    //        LanguageCase.Pin -> {
    //            blocksStat.add(statistic.pinState)
    //            blocksStat.addAll(statistic.pinBlockState)
    //        }
    //        LanguageCase.Trn -> {
    //            blocksStat.add(statistic.trnState)
    //            blocksStat.addAll(statistic.trnBlockState)
    //        }
    //    }
    //    return blocksStat
    //}

    //private fun createStatistic(map: Map<Int, List<StudyWord>>): StudyListRepeatStatistic {
    //    return StudyListRepeatStatistic(map,
    //            StudyAnalyzer(FibRepeatHelper()))
    //}

    private class ViewPagerAdapter(var chooseTypeAdapter: ChooseTypeAdapter?, var chooseWordsAdapter: ChooseWordsAdapter?)
        : RecyclerView.Adapter<ViewViewHolder>(){
        val TYPE = 0
        val BLOCK = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewViewHolder {
            val context = parent.context

            val recyclerViewLayoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT)

            val recyclerView = RecyclerView(context)
            recyclerView.layoutParams = recyclerViewLayoutParams
            recyclerView.layoutManager = LinearLayoutManager(context)

            if (viewType == TYPE) {
                recyclerView.adapter = chooseTypeAdapter
                recyclerView.addItemDecoration(BoundsTypeDecorator())
            }
            else if (viewType == BLOCK) {
                recyclerView.adapter = chooseWordsAdapter
                recyclerView.addItemDecoration(BoundsBlockDecorator())
            }
            else
                throw java.lang.RuntimeException("invalid view type")

            return ViewViewHolder(recyclerView)
        }

        override fun getItemCount(): Int {
            return 2
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0)
                return TYPE
            if (position ==1)
                return BLOCK
            return -1
        }

        override fun onBindViewHolder(holder: ViewViewHolder, position: Int) {}

    }
    private class ChooseTypeAdapter()
        : HeaderItemClickedAdapter() {

        var trainedChinese : TrainedWords = TrainedWords(0,0,0)
        var showChinese : Boolean = false
        var trainedPinyin : TrainedWords = TrainedWords(0,0,0)
        var showPinyin : Boolean = false
        var trainedTranslation : TrainedWords = TrainedWords(0,0,0)
        var showTranslation : Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TypeHolder(120,parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val isActive =
                when (position) {
                    0 -> showChinese
                    1 -> showPinyin
                    2 -> showTranslation
                    else -> throw RuntimeException("illegal argument position:$position")
                }

            val trained =
                when (position) {
                    0 -> trainedChinese
                    1 -> trainedPinyin
                    2 -> trainedTranslation
                    else -> throw RuntimeException("illegal argument position:$position")
                }

            (holder as TypeHolder).bind(trained.all - trained.filed - trained.repeatable, trained.filed, trained.all, getItemText(position,holder.itemView.context), position, isActive)

            if (isActive)
                holder.itemView.setOnClickListener(onClickListener)
            else
                holder.itemView.setOnClickListener(null)
        }

        override fun getItemCount(): Int {
            return 3
        }

        fun getItemText( position: Int, context: Context):String {
            val description = context.resources.getStringArray(R.array.train_type)
            return if (description.size > position) description[position] else ""
        }

    }

    private class ChooseWordsAdapter(var trainedWords : TrainedWords) : HeaderItemClickedAdapter(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TypeHolder(120,parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (position == 0) (holder as TypeHolder)
                .bind(
                    trainedWords.all - trainedWords.filed - trainedWords.repeatable,
                    trainedWords.filed,
                    trainedWords.all,
                    getItemText(position,holder.itemView.context),
                    position, true)
            if (position ==1) (holder as TypeHolder)
                .bind(
                    0,
                    trainedWords.filed,
                    trainedWords.repeatable+ trainedWords.filed,
                    getItemText(position,holder.itemView.context),
                    position, trainedWords.repeatable+ trainedWords.filed > 0)
        }

        override fun getItemCount(): Int {
            return 2
        }

        fun getItemText( position: Int, context: Context) =
            if (position == 0) context.getString(R.string.all)
            else context.getString(R.string.only_needed)
    }

    /*
    private class ChooseBlockAdapter(var blocksStat : List<StudyListRepeatStatistic.State>)
        : HeaderItemClickedAdapter() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TypeHolder(80,parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val (bed, good, common) = blocksStat[position]
            (holder as TypeHolder).bind(good, bed, common, getItemText(position,holder.itemView.context) , position, true)
            holder.itemView.setOnClickListener(onClickListener)
        }

        override fun getItemCount(): Int {
            return blocksStat.size
        }

        fun getItemText( position: Int, context: Context) =
                if (position == 0) context.getString(R.string.all)
                else context.getString(R.string.block, position)

    }

     */

    private abstract class HeaderItemClickedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var onClickListener: View.OnClickListener? = null
    }
    private class TypeHolder(viewHeight: Int, inflater: LayoutInflater, parent: ViewGroup?)
        : TrainPartsHolder(inflater, parent){
        private var textView: TextView? = null

        fun bind(progressGreen: Int, progressRed: Int, maxProgress: Int, text: String?, position: Int, isActive: Boolean) {
            super.bind(progressGreen, progressRed, maxProgress)
            textView!!.text = text
            itemView.setTag(R.id.holder_position,position)
            if (isActive) {
                foggingView.visibility = View.INVISIBLE
            } else {
                foggingView.visibility = View.VISIBLE
            }
        }
        private fun createInnerView(context: Context, viewHeight : Int) {
            val layout = FrameLayout(context)
            layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT)
            textView = TextView(context)
            textView!!.textSize = 25f
            val textViewParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT)
            textViewParams.gravity = Gravity.CENTER
            textViewParams.leftMargin = 16
            textViewParams.topMargin = viewHeight/2
            textViewParams.bottomMargin = viewHeight/2
            layout.addView(textView, textViewParams)

            super.addInnerView(layout)
        }

        init {
            createInnerView(inflater.context,viewHeight)
        }
    }

    private class BoundsTypeDecorator : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val parentSize = parent.height
            val height = View.resolveSize(0, View.MeasureSpec.AT_MOST)
            val width = View.resolveSize(0,View.MeasureSpec.AT_MOST)
            view.measure(width,height)
            val viewSize = view.measuredHeight
            val bounds = (parentSize - viewSize*3)/4

            when(parent.getChildAdapterPosition(view)){
                0 -> {
                    outRect.top = bounds
                    outRect.bottom = bounds/2
                }
                parent.adapter!!.itemCount - 1 ->{
                    outRect.top = bounds/2
                    outRect.bottom = bounds
                }
                else ->{
                    outRect.top = bounds/2
                    outRect.bottom = bounds/2
                }
            }
        }
    }
    private class BoundsBlockDecorator : ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val parentSize = parent.height
            val height = View.resolveSize(0, View.MeasureSpec.AT_MOST)
            val width = View.resolveSize(0,View.MeasureSpec.AT_MOST)
            view.measure(width,height)
            val viewSize = view.measuredHeight
            val minBounds  = (viewSize*0.8).toInt()

            val viewCount = parent.adapter!!.itemCount

            val bounds = if (viewSize*viewCount + minBounds*(viewCount + 1) >= parentSize){
                minBounds
            } else{
                (parentSize - viewSize*viewCount)/(viewCount + 1)
            }

            when(parent.getChildAdapterPosition(view)){
                0 -> {
                    outRect.top = bounds
                    outRect.bottom = bounds/2
                }
                viewCount - 1 ->{
                    outRect.top = bounds/2
                    outRect.bottom = bounds
                }
                else ->{
                    outRect.top = bounds/2
                    outRect.bottom = bounds/2
                }
            }
        }
    }

}