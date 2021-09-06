package com.yingenus.pocketchinese.controller.dialog

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.activity.CreateWordActivity
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.controller.getDisplayHeight
import com.yingenus.pocketchinese.controller.holders.ViewViewHolder
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.presenters.CharacterPresenter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButtonToggleGroup
import java.lang.IllegalArgumentException
import com.yingenus.pocketchinese.model.database.dictionaryDB.Example as DbExample

class CharacterSheetDialog(chinChar: ChinChar?) :BottomSheetDialogFragment(), CharacterInterface {

    private val presenter = CharacterPresenter(this,chinChar?.id?:1)

    private lateinit var chiCharacters: TextView
    private lateinit var pinyin: TextView
    private lateinit var translation: TextView
    private lateinit var charTags : TextView
    private lateinit var linksChars: TextView
    private lateinit var addButton: Button
    private lateinit var viewPager: ViewPager2
    private lateinit var buttonGroup: MaterialButtonToggleGroup
    private lateinit var viewSepearator: View

    private lateinit var mTranslations: List<String>
    private lateinit var mExamles: List<DbExample>
    private lateinit var mCharacters: List<ChinChar>

    private var isFirstCall = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?
                              , savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.character_fragment,container)

        chiCharacters = view.findViewById(R.id.character_chin_char)
        pinyin = view.findViewById(R.id.character_pinyin)
        translation = view.findViewById(R.id.character_fragment_translate)
        addButton = view.findViewById(R.id.add_button)
        viewPager = view.findViewById(R.id.view_pager)
        buttonGroup = view.findViewById(R.id.button_group)
        linksChars = view.findViewById(R.id.links_chin_char)
        charTags = view.findViewById(R.id.chin_tags)
        viewSepearator = view.findViewById(R.id.separator)

        addButton.setOnClickListener(this::onAddClicked)
        buttonGroup.check(R.id.transition)
        viewPager.isUserInputEnabled = false

        setOnButtonSelectListener()

        presenter.onCreate(context!!)

        viewPager.adapter = PagerAdapter()
        viewPager.offscreenPageLimit = 2
        initPagerCallback()

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun setChin(text: String) {
        chiCharacters.text = text
    }

    override fun setLinked(links: List<String>) {
        val str = "(${links.joinToString(separator = "，")})"

        linksChars.text = str
        linksChars.visibility = View.VISIBLE
    }

    override fun setPinyin(text: String) {
        pinyin.text = text
    }

    override fun setTags(tags: List<String>) {
        val str = tags.joinToString(separator = ", ") { it.toUpperCase() }

        charTags.text = str
        charTags.visibility = View.VISIBLE
    }

    override fun startAddNewStudy(word: ChinChar) {
        val intent= CreateWordActivity.getIntent(context!!,word)
        context!!.startActivity(intent)
    }

    override fun setTranslations(trns: List<String>) {
        mTranslations = trns
        reInitPages()
    }

    override fun setExamples(exampls: List<DbExample>) {
        mExamles = exampls
        reInitPages()
    }

    override fun setCharacters(entrysChars: List<ChinChar>) {
        mCharacters = entrysChars
        reInitPages()
    }

    private fun onAddClicked(v: View){
        presenter.addCardClicked()
    }

    private fun setOnButtonSelectListener(){
        buttonGroup.addOnButtonCheckedListener( this::onButtonSelected)
    }

    private fun onButtonSelected(group : MaterialButtonToggleGroup, checkedId : Int, isChecked : Boolean){
        if (!isChecked)
            return

        val adapter = viewPager.adapter as PagerAdapter

        val position = if (checkedId == R.id.transition && isChecked){
            adapter.getPosition(adapter.TRANSLATIONS)
        } else if (checkedId == R.id.example && isChecked){
            adapter.getPosition(adapter.EXAMPLES)
        }else if(checkedId == R.id.characters && isChecked){
            adapter.getPosition(adapter.CHARACTER)
        }else
            throw RuntimeException(" no initialized holders in CharacterSheetDialog")

        viewPager.setCurrentItem(position,true)

    }

    private fun reInitPages(){
        if (enableTranslations()){
            if (enableExamples()){
                buttonGroup.findViewById<Button>(R.id.example).visibility = View.VISIBLE
                buttonGroup.visibility = View.VISIBLE
                viewSepearator.visibility = View.GONE
            }
            if (enableCharacters()){
                buttonGroup.findViewById<Button>(R.id.characters).visibility = View.VISIBLE
                buttonGroup.visibility = View.VISIBLE
                viewSepearator.visibility = View.GONE
            }
            viewPager.adapter?.notifyDataSetChanged()
        }
    }

    private fun initPagerCallback(){
       viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
           override fun onPageSelected(position: Int) {

               if (isFirstCall){
                   isFirstCall = false
                   return
               }

               val view = (viewPager.adapter as PagerAdapter).recyclerView?.
               layoutManager?.findViewByPosition(viewPager.currentItem)?: return



               val measureSpeckW = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
               val measureSpeckH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
               view.measure(measureSpeckW, measureSpeckH)

               val viewPagerMarginTop = viewPager.top
               val displayHeight = getDisplayHeight(activity!!)
               val statusBarHeight = getStatusBarHeight()
               val absolutePosition = IntArray(2)
               viewPager.getLocationOnScreen(absolutePosition)
               val absoluteTop = absolutePosition[1]

               val pagerVisibleHeight = displayHeight - absoluteTop + statusBarHeight

               var pagerFinalVisibleHeight = 0

               val minTop = Math.min(viewPagerMarginTop, absoluteTop)

               if (minTop+view.measuredHeight < displayHeight){
                   pagerFinalVisibleHeight = view.measuredHeight
               }else {
                   pagerFinalVisibleHeight = displayHeight - minTop
               }

               if (pagerVisibleHeight < pagerFinalVisibleHeight){
                   transferAnimator(viewPager, pagerVisibleHeight, pagerFinalVisibleHeight ,  view.measuredHeight)
               }
               else if (pagerVisibleHeight > pagerFinalVisibleHeight){

                   transferAnimator(viewPager, pagerVisibleHeight, pagerFinalVisibleHeight, view.measuredHeight)
               }

           }

       })
    }

    fun transferAnimator(view: View, initHeight: Int, finalHeight : Int,endAnimationHeight : Int){
        val speedCf = 0.5

        val params = view.layoutParams

        val animator = ValueAnimator.ofInt(initHeight, finalHeight)

        animator.setDuration((Math.abs(finalHeight - initHeight)*speedCf).toLong())
        animator.addUpdateListener { animation: ValueAnimator? ->
            params.height = animation!!.animatedValue as Int
            view.requestLayout()
            view.invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                jumpToLast()
            }

            override fun onAnimationCancel(animation: Animator?) {
                jumpToLast()
            }

            override fun onAnimationStart(animation: Animator?) {

            }
            private fun jumpToLast(){
                if (finalHeight != endAnimationHeight){
                    params.height = endAnimationHeight
                    view.requestLayout()
                    view.invalidate()
                }

            }
        })

        animator.start()
    }

    private fun getStatusBarHeight(): Int{
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun enableExamples() =
            ::mExamles.isInitialized && mExamles.isNotEmpty()
    private fun enableCharacters() =
            ::mCharacters.isInitialized && mCharacters.isNotEmpty()
    private fun enableTranslations() =
            ::mTranslations.isInitialized && mTranslations.isNotEmpty()

    private inner class PagerAdapter: RecyclerView.Adapter<ViewViewHolder>(){
        val TRANSLATIONS = 1
        val EXAMPLES = 2
        val CHARACTER = 3

        var recyclerView : RecyclerView? = null

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)

            this.recyclerView = recyclerView
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)

            this.recyclerView = null
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewViewHolder {
            if (viewType == TRANSLATIONS)
                return TranslationsHolder(parent, mTranslations)
            if (viewType == EXAMPLES)
                return ExamplesHolder(parent, mExamles)
            if (viewType == CHARACTER)
                return CharactersHolder(parent,mCharacters)

            throw RuntimeException("invalid ViewType in PagerAdapter")
        }

        override fun onBindViewHolder(holder: ViewViewHolder, position: Int) {

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            if (position == 0)
                return TRANSLATIONS

            if (position == 1){
                return if (enableCharacters())
                    CHARACTER
                else
                    EXAMPLES
            }

            if (position == 2)
                return  EXAMPLES

            return super.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            var count = 0

            if (enableTranslations()){
                count++
            }
            if (enableExamples()){
                count++
            }
            if (enableCharacters()){
                count++
            }

            return count
        }

        fun getPosition(viewType : Int): Int{
            if (viewType == TRANSLATIONS && enableTranslations())
                return  0
            if (viewType == EXAMPLES && enableExamples()){
                if (enableCharacters())
                    return 2
                else
                    return 1
            }
            if (viewType == CHARACTER && enableCharacters()){
                return 1
            }
            return -1
        }

    }

    private class TranslationsHolder: ViewViewHolder{
        constructor( parent: ViewGroup, translations : List<String>):
                super(LinearLayout(parent.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
                }) {

            val view = itemView as LinearLayout

            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


            val itemLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)

            val density = parent.context.resources.displayMetrics.density

            itemLayoutParams.topMargin= (density * 16).toInt()
            itemLayoutParams.bottomMargin = (density * 16).toInt()
            itemLayoutParams.marginStart = (density * 16).toInt()
            itemLayoutParams.marginEnd = (density * 16).toInt()

            var counter = 1
            for (translation in translations){
                view.addView(createTextView("${counter}) $translation",inflater.context),itemLayoutParams)
                counter++
            }

        }

        @SuppressLint("ResourceAsColor")
        private fun createTextView(text : String, context: Context): TextView{
            val textColor = TypedValue()
            if(!context.theme.resolveAttribute(android.R.attr.textColorPrimary,textColor,true)){
                throw IllegalArgumentException("cant found attribute android.R.attr.textColorPrimary")
            }

            val textView = TextView(context)
            textView.text = text
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
            textView.setTextColor(textColor.data)

            return textView
        }
    }

    private class CharactersHolder: ViewViewHolder{
        constructor( parent: ViewGroup, chars : List<ChinChar>):
                super(LinearLayout(parent.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
                }) {

            val view = itemView as LinearLayout


            val itemLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            itemLayoutParams.topMargin= 4
            itemLayoutParams.bottomMargin = 8

            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            for (char in chars){
                view.addView(createItemView(inflater,char),itemLayoutParams)
            }

        }
        @SuppressLint("ResourceAsColor")
        private fun createItemView(inflater: LayoutInflater, char : ChinChar): View{
            val view = inflater.inflate(R.layout.inner_char_item, null)

            val chn = view.findViewById<TextView>(R.id.character)
            val pin = view.findViewById<TextView>(R.id.pinyin)
            val tgs = view.findViewById<TextView>(R.id.tags)
            val linerLayout = view.findViewById<LinearLayout>(R.id.translation_layout)

            chn.text = char.chinese
            pin.text = char.pinyin
            if (char.generalTag.isNotEmpty()){
                tgs.text = char.generalTag
                tgs.visibility = View.VISIBLE
            }

            val translationLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)

            var counter = 1
            for (translation in char.translations){
                if (translation.contains("\$link"))
                    continue

                val str = "${counter}) $translation"

                linerLayout.addView(createTextView(str, inflater.context),translationLayoutParam)
                counter++
            }
            return view
        }

        @SuppressLint("ResourceAsColor")
        private fun createTextView(text : String, context : Context): TextView{
            val textColor = TypedValue()
            if(!context.theme.resolveAttribute(android.R.attr.textColorPrimary,textColor,true)){
                throw IllegalArgumentException("cant found attribute android.R.attr.textColorPrimary")
            }

            val textView = TextView(context)
            textView.text = text
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
            textView.setTextColor(textColor.data)

            return textView
        }
    }

    private class ExamplesHolder: ViewViewHolder{
        constructor( parent: ViewGroup, examples: List<DbExample>):
                super(LinearLayout(parent.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT)
                }) {

            val view = itemView as LinearLayout
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val messageLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            messageLayoutParams.topMargin = dp2px(4, parent.context)
            messageLayoutParams.bottomMargin = dp2px(4, parent.context)
            messageLayoutParams.marginStart = dp2px(16, parent.context)

            view.addView(createTextView(parent.context.getString(R.string.error_in_examples),inflater.context!!), messageLayoutParams)


            val itemLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
            examples.forEachIndexed { index, example ->

                val isLast = (index+ 1 == examples.size)
                view.addView(createItemView(inflater,example,isLast),itemLayoutParams)

            }

        }

        @SuppressLint("ResourceAsColor")
        private fun createTextView(text : String, context : Context): TextView{
            val textView = TextView(context)
            textView.text = text
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
            textView.setTextColor(context.resources.getColor(R.color.attention_color))

            return textView
        }

        @SuppressLint("ResourceAsColor")
        private fun createItemView(inflater: LayoutInflater, examp : DbExample, isLast : Boolean): View{

            val view = inflater.inflate(R.layout.example_holder_dictionary,null)

            val chin = view.findViewById<TextView>(R.id.chin)
            val pin = view.findViewById<TextView>(R.id.pinyin)
            val trn = view.findViewById<TextView>(R.id.language)
            val separator = view.findViewById<View>(R.id.separator)

            chin.text = examp.chinese
            pin.text = examp.pinyin
            trn.text = examp.translation.trim()

            if (!isLast)
                separator.visibility = View.VISIBLE

            return view
        }
    }
}
