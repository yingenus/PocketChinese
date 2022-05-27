package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.controller.getDisplayHeight
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import com.yingenus.pocketchinese.presentation.views.findViewByClass
import com.yingenus.pocketchinese.presentation.views.findViewsByClass
import com.yingenus.pocketchinese.presentation.views.grammar.GrammarCaseActivity
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class RadicalSearchDialog() : BottomSheetDialogFragment(), RadicalSearchInterface  {

    private companion object{

        const val radicalBoxSize = 68
        val boxSizePx: (Context) -> Int = {c -> dp2px(radicalBoxSize,c)}

    }

    private object Helper{
        const val OccupiHeight = 0.7f
    }

    interface RadicalSearchCallback{
        fun onCharacterSelected(character: String)
    }

    private var radicalSearchCallback : RadicalSearchCallback? = null

    @Inject
    lateinit var radicalSearchPresenterFactory: RadicalSearchPresenter.Factory
    private lateinit var presenter: RadicalSearchPresenter

    private var recyclerView : RecyclerView? = null

    private var showedRadicals :  Map<Int, List<RadicalSearchInterface.Character>>? = null
    private var title = ""
    private var showBack = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        PocketApplication.getAppComponent().injectRadicalSearchDialog(this)
        presenter = radicalSearchPresenterFactory.create(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        title = requireContext().resources.getString(R.string.radical)

        recyclerView = RecyclerView(requireContext())
        recyclerView!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)

        val displayWight = requireContext().resources.displayMetrics.widthPixels
        val viewWight = boxSizePx(requireContext())

        val color : Int

        UtilsVariantParams.apply { color = requireContext().resolveColorAttr(android.R.attr.textColorSecondary) }

        recyclerView!!.addItemDecoration(Decorator(dp2px(1,requireContext()),color,65, viewWight,displayWight))

        val displayHeight = getDisplayHeight(requireContext())
        val statusBarHeight = getStatusBarHeight()
        val availableSpace = displayHeight - statusBarHeight

        recyclerView!!.minimumHeight = availableSpace

        recyclerView!!.setBackgroundResource(R.drawable.sheet_dialog_shape)
        recyclerView!!.adapter = RadicalsAdapter()
                .also {
                    it.setRadicalClickListener(View.OnClickListener { v -> onRadicalClicked(v) })
                    it.setTollbarButtonClickListener(View.OnClickListener { v -> onTitleButtonClicked(v) })
                }

        val columns  = displayWight / viewWight

        recyclerView!!.layoutManager = GridLayoutManager(context, columns)
                .apply {
                    spanSizeLookup = TitleSizeLookup(recyclerView!!)
                }

        updateRadicals()

        val  behavior = (dialog as BottomSheetDialog).behavior
        behavior.peekHeight = getPagerOccupiHeight(dp2px(0,inflater.context))
        behavior.isDraggable = true

        return recyclerView
    }

    override fun onDestroy() {
        super.onDestroy()
        radicalSearchCallback = null
    }

    override fun setRadicals(radicals: Map<Int, List<RadicalSearchInterface.Character>>) {
        showedRadicals = radicals
        title = requireContext().resources.getString(R.string.radical)
        showBack = false
        updateRadicals()
    }

    override fun setCharacters(characters: Map<Int, List<RadicalSearchInterface.Character>>) {
        showedRadicals = characters
        title = requireContext().resources.getString(R.string.characters)
        showBack = true
        updateRadicals()
    }

    override fun publishCharacter(character: RadicalSearchInterface.Character) {
        radicalSearchCallback?.onCharacterSelected(character.zi)
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

    private fun onRadicalClicked(v : View?){
        val viewholder = recyclerView?.findContainingViewHolder(v!!)
        if (viewholder is RadicalViewHolder){
            if (viewholder.character != null && viewholder.character!!.isEnabled)
                presenter.radicalSelected(viewholder.character!!)

        }
        //presenter.radicalSelected(RadicalSearchInterface.Character(v!!.findViewByClass(TextView::class.java)!!.text.toString(),true))
    }


    private fun onTitleButtonClicked(v : View?){
        if (v!!.id == R.id.back_button){
            presenter.onBackPressed()
        }
        if (v!!.id == R.id.help_button){
            val intent = GrammarCaseActivity.getIntent(requireContext(),"howToRadicals")
            startActivity(intent)
        }
    }

    private fun updateRadicals(){
        if (showedRadicals != null){
            if (recyclerView?.adapter is RadicalsAdapter){
                val adapter = recyclerView!!.adapter as RadicalsAdapter
                adapter.showBackButton(showBack)
                adapter.setHeaderTitle(title)
                adapter.setItems(
                        showedRadicals!!.entries
                                .sortedBy{ it.key }
                                .flatMap { entry ->
                                    mutableListOf<AdapterItems>(AdapterItems.Title(entry.key.toString())).also {
                                        it.addAll(entry.value.map { AdapterItems.Radical(it) })

                                }
                                })
                recyclerView!!.adapter!!.notifyDataSetChanged()
                recyclerView!!.scrollToPosition(0)
            }
        }
    }

    fun setCallback(callback: RadicalSearchCallback){
        radicalSearchCallback = callback
    }

    class RadicalsAdapter( ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        companion object{
            private const val HEADER = 2
            private const val TITLE = 1
            private const val RADICAL = 0
        }

        var itemList : MutableList<AdapterItems> = mutableListOf()
        var header : String =""
        var showBack = false

        private var radicalClickListener : View.OnClickListener? = null
        private var tollbarButtonClickListener : View.OnClickListener? = null


        fun setItems(items : List<AdapterItems>){
            itemList = mutableListOf<AdapterItems>(AdapterItems.Title(header)).also { it.addAll(items) }
        }

        fun setRadicalClickListener(listener: View.OnClickListener){
            radicalClickListener = listener
        }

        fun setTollbarButtonClickListener(listener: View.OnClickListener){
            tollbarButtonClickListener = listener
        }

        fun setHeaderTitle(title : String){
            header = title
            if (itemList.isNotEmpty()){
                itemList[0] = AdapterItems.Title(header)
                notifyDataSetChanged()
            }
        }

        fun showBackButton(show : Boolean){
            showBack = show
            notifyItemChanged(0)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
                when(viewType){
                    HEADER -> ViewViewHolder(R.layout.radical_select_dialog, parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                            .also {
                                it.itemView.findViewsByClass(Button::class.java).forEach { it.setOnClickListener{ v -> tollbarButtonClickListener?.onClick(v)} }
                            }
                    TITLE -> TitleViewHolder(parent)
                    RADICAL -> RadicalViewHolder(parent)
                            .also {
                                it.itemView.setOnClickListener{ v -> radicalClickListener?.onClick(v)}
                            }
                    else -> throw RuntimeException("invalid view type")
                }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is ViewViewHolder -> {
                    holder.itemView.findViewById<TextView>(R.id.text)!!.text = (itemList[position] as AdapterItems.Title).title
                    holder.itemView.findViewById<Button>(R.id.back_button).visibility =
                            if(showBack)
                                View.VISIBLE
                            else
                                View.GONE
                }
                is TitleViewHolder -> holder.bindTitle((itemList[position] as AdapterItems.Title).title)
                is RadicalViewHolder ->{
                    val radical = (itemList[position] as AdapterItems.Radical)
                    holder.bindRadical(radical.content)
                }
            }
        }

        override fun getItemViewType(position: Int): Int =
                if (position == 0 ) HEADER
                else when(itemList[position]){
                        is AdapterItems.Title -> TITLE
                        is AdapterItems.Radical -> RADICAL
                }

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getItemCount(): Int = itemList.size

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            radicalClickListener = null
            tollbarButtonClickListener = null
        }
    }

    sealed class AdapterItems{

        class Title(val title: String) : AdapterItems()

        class Radical(val content: RadicalSearchInterface.Character) : AdapterItems()

    }

    class TitleViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(inflateTitleView(parent)){

        val textView : TextView = super.itemView.findViewByClass(TextView::class.java)!!

        companion object{
            fun inflateTitleView(parent : ViewGroup): View{

                val rootParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val frameLayout = FrameLayout(parent.context)
                        .apply {
                            layoutParams = rootParams
                        }

                val textViewParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.START)
                textViewParams.marginStart = dp2px(16, parent.context)
                val textView = TextView(parent.context)
                        .apply {
                            textSize = 26f
                            textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START

                            typeface = ResourcesCompat.getFont(parent.context,R.font.bebasneue_regular)

                            val colorValue = TypedValue()
                            context.theme.resolveAttribute(android.R.attr.colorPrimary,colorValue,true)
                            setTextColor(colorValue.data)
                        }

                frameLayout.addView(textView,textViewParams)

                return frameLayout
            }
        }

        fun bindTitle(title : String){
            textView.text = title
        }

    }

    class TitleSizeLookup(val recyclerView: RecyclerView) : GridLayoutManager.SpanSizeLookup(){

        override fun getSpanSize(position: Int): Int {
            val result =  if((recyclerView.adapter as RadicalsAdapter).itemList[position] is AdapterItems.Title)
                (recyclerView.layoutManager as GridLayoutManager).spanCount
            else
                1
            return result
        }

    }

    class RadicalViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(inflateRadicalView(parent)) {

        val textView : TextView = super.itemView.findViewByClass(TextView::class.java)!!
        @ColorInt val tintColor : Int
        @AnyRes val clikedBackground : Int
        var character : RadicalSearchInterface.Character?= null

        init {
            val typedValue = TypedValue()
            parent.context.theme.resolveAttribute(android.R.attr.textColorSecondary,typedValue,true)
            @ColorInt val onSurface = typedValue.data
            tintColor = Color.argb((0.1*255).toInt(), Color.red(onSurface), Color.green(onSurface), Color.blue(onSurface))
            val typedValue2 = TypedValue()
            parent.context.theme.resolveAttribute(android.R.attr.selectableItemBackground,typedValue,true)
            clikedBackground = typedValue2.resourceId
        }

        companion object{
            fun inflateRadicalView(parent : ViewGroup): View{
                val context = parent.context

                val rootParams = ViewGroup.LayoutParams(boxSizePx(context), boxSizePx(context))
                val frameLayout = FrameLayout(context)
                        .apply {
                            layoutParams = rootParams

                            //val typedValue = TypedValue()
                            //context.theme.resolveAttribute(android.R.attr.selectableItemBackground,typedValue,true)
                            //this.setBackgroundResource(typedValue.resourceId)
                        }

                val textViewParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
                val textView = TextView(parent.context)
                        .apply {
                            textSize = 32f
                            typeface = ResourcesCompat.getFont(parent.context, R.font.mulish_semibold)
                            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        }

                frameLayout.addView(textView,textViewParams)

                return frameLayout
            }
        }

        fun bindRadical( character: RadicalSearchInterface.Character){
            this.character = character
            textView.text = character.zi
            if (character.isEnabled){
                itemView.setBackgroundResource(clikedBackground)
            }else{
                itemView.setBackgroundColor(tintColor)
            }
        }

    }

    class Decorator(val separatorSize : Int, @ColorInt val separatorColor : Int, val separatorFilling : Int,val viewSize : Int, val parentWight : Int) : RecyclerView.ItemDecoration( ){

        private companion object{
            const val LEFT = 1;
            const val BOTTOM = 2;
            const val LEFT_BOTTOM = 3;
        }


        private val paint = Paint().also { it.color = separatorColor }



        override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDrawOver(c, parent, state)

            if (parent.childCount != 0) {

                val layoutManager = parent.layoutManager as GridLayoutManager

                for (childIndex in 0 until parent.childCount) {
                    val child = parent.getChildAt(childIndex)

                    val code = child.getTag(R.id.radical_search_dialog_item_decorator_tag)

                    if (code == null || code == -1)
                        continue

                    val drawLeft = code == LEFT || code == LEFT_BOTTOM
                    val drawBottom = code == BOTTOM || code == LEFT_BOTTOM

                    val outRect = Rect()

                    parent.getDecoratedBoundsWithMargins(child,outRect)

                    val top = outRect.top
                    val right = outRect.right
                    val bottom = outRect.bottom
                    val left = outRect.left

                    if ( drawLeft ){
                        val sepTop = calculateSeparatorStart(top,bottom)
                        val sepBottom = sepTop + calculateSeparatorWidth(bottom - top)

                        c.drawRect((right - separatorSize).toFloat(), sepTop.toFloat(), right.toFloat(),sepBottom.toFloat(),paint)
                    }

                    if (drawBottom){
                        val sepLeft = calculateSeparatorStart(left,right)
                        val sepRight = sepLeft + calculateSeparatorWidth(right - left)

                        c.drawRect(sepLeft.toFloat(), (bottom - separatorSize).toFloat(), sepRight.toFloat(), bottom.toFloat(),paint)
                    }

                }
            }

        }

        private fun calculateSeparatorWidth(viewWidth : Int): Int{
            return viewWidth* min(separatorFilling,100)/100
        }

        private fun calculateSeparatorStart(viewStart : Int, viewEnd : Int): Int{
            val width = viewEnd - viewStart
            val center = viewStart + width/2

            return center - calculateSeparatorWidth(width)/2
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.getChildViewHolder(view) !is TitleViewHolder){

                val layoutManager = parent.layoutManager as GridLayoutManager
                val adapter = parent.adapter as RadicalsAdapter

                val position = layoutManager.getPosition(view)
                val columns = layoutManager.spanCount
                val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position,columns)
                val beforeEndRadicals = adapter.itemList.subList(position,adapter.itemList.size).takeWhile { it is AdapterItems.Radical }.size

                val viewWidth = viewSize
                val recyclerWidth = parentWight
                val margin = max(((recyclerWidth - viewWidth * columns) / (columns + 1)) / 2 , separatorSize)


                outRect.set(margin, margin, margin, margin)

                val left =
                if (spanIndex == 0 && beforeEndRadicals > 1 )
                    true
                else
                    spanIndex < columns -1 && beforeEndRadicals > 1

                val beforeEndRow = columns - spanIndex
                val nextLine = beforeEndRadicals - beforeEndRow

                val bottom = nextLine >= spanIndex+1

                val code =
                        if (left && bottom)
                            LEFT_BOTTOM
                        else if (left)
                            LEFT
                        else if( bottom)
                            BOTTOM
                        else -1

                view.setTag(R.id.radical_search_dialog_item_decorator_tag,code)
            }else{
                super.getItemOffsets(outRect, view, parent, state)
            }
        }
    }
}