package com.yingenus.pocketchinese.presentation.views.addword

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.BoundsDecoratorBottom
import com.yingenus.pocketchinese.controller.CardBoundTopBottom

class OptionsAddFragment : Fragment(R.layout.options_layout){

    private var romanization : RecyclerView? = null
    private var options : RecyclerView? = null

    private var _mixWords : Boolean = false
    val mixWords : Boolean
        get() = _mixWords
    private var _simplifyPinyin : Boolean = false
    val simplifyPinyin : Boolean
        get() = _simplifyPinyin

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        romanization = view.findViewById(R.id.recyclerview_pinyin)
        options = view.findViewById(R.id.recyclerview_optionse)

        romanization!!.layoutManager = LinearLayoutManager(requireContext())
        options!!.layoutManager = LinearLayoutManager(requireContext())
        romanization!!.adapter = PinyinAdapter()
        options!!.adapter = MixAdapter()
        romanization!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        romanization!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))
        options!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        options!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))

        val pinSelection : SelectionTracker.SelectionObserver<Long> = object : SelectionTracker.SelectionObserver<Long>(){
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                if (selected){
                    when(key){
                        0L -> _simplifyPinyin = false
                        1L -> _simplifyPinyin = true
                    }
                }
            }
        }
        val mixSelection : SelectionTracker.SelectionObserver<Long> = object : SelectionTracker.SelectionObserver<Long>(){
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                if (selected){
                    when(key){
                        0L -> _mixWords = true
                        1L -> _mixWords = false
                    }
                }
            }
        }

        val pinTracker = SelectionTracker.Builder<Long>(
            "pin_selection",
            romanization!!,
            OptionsKeyProvider(),
            OptionsDetailsLookup(romanization!!),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build();
        val mixTracker = SelectionTracker.Builder<Long>(
            "pin_selection",
            options!!,
            OptionsKeyProvider(),
            OptionsDetailsLookup(options!!),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build();

        pinTracker.addObserver(pinSelection)
        mixTracker.addObserver(mixSelection)

        (romanization!!.adapter as OptionsAdapter).selectionTracker = pinTracker
        (options!!.adapter as OptionsAdapter).selectionTracker = mixTracker

        pinTracker.select(0)
        mixTracker.select(0)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        romanization = null
        options = null
    }

    class PinyinAdapter : OptionsAdapter(){
        override fun getTitle(position: Int, context: Context): String {
            return when(position){
                0 -> context.resources.getString(R.string.correct_pinyin_type_1)
                1 -> context.resources.getString(R.string.correct_pinyin_type_2)
                else -> throw RuntimeException()
            }

        }
        override fun getItemCount(): Int {
            return 2
        }
    }

    class MixAdapter : OptionsAdapter(){
        override fun getTitle(position: Int, context: Context): String {
            return when(position){
                0 -> context.resources.getString(R.string.add_words_mix_words)
                1 -> context.resources.getString(R.string.add_words_not_mix_words)
                else -> throw RuntimeException()
            }

        }
        override fun getItemCount(): Int {
            return 2
        }
    }

}

abstract class OptionsAdapter : RecyclerView.Adapter<OptionsHolder>(){

    var selectionTracker : SelectionTracker<Long>? = null
        set(value) {
            field = value
            //field?.addObserver(SelectionObserver())
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsHolder {
        return OptionsHolder(parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater, parent)
    }

    override fun onBindViewHolder(holder: OptionsHolder, position: Int) {
        holder.bind(getTitle(position, holder.itemView.context), position.toLong(), selectionTracker)
    }

    abstract fun getTitle(position: Int, context: Context): String

    inner class SelectionObserver : SelectionTracker.SelectionObserver<Long>(){
        override fun onItemStateChanged(key: Long, selected: Boolean) {
            if (selected){
                val items  = 0L.rangeTo(itemCount).toMutableList()
                items.remove(key)
                selectionTracker!!.setItemsSelected(items, false)
            }
        }
    }

}

class OptionsHolder(layoutInflater: LayoutInflater, root : ViewGroup) :
    RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.add_optionse_holder, root, false)){

    val details = OptionsDetails()

    fun bind(text : String, position : Long, selectionTracker: SelectionTracker<Long>?){
        itemView.findViewById<TextView>(R.id.title).text = text
        details.position = position
        selectionTracker ?.let { bindSelection(selectionTracker) }
    }

    private fun bindSelection(selectionTracker: SelectionTracker<Long>){
        if (itemView is MaterialCardView)
            (itemView as MaterialCardView).isChecked = selectionTracker.isSelected(details.selectionKey)
    }

}

class OptionsDetails : ItemDetailsLookup.ItemDetails<Long>(){

    var position : Long = -1L


    override fun getPosition(): Int {
        return position.toInt()
    }

    override fun getSelectionKey(): Long {
        return position
    }

    override fun inSelectionHotspot(e: MotionEvent): Boolean {
        return false
    }

    override fun inDragRegion(e: MotionEvent): Boolean {
        return true
    }
}

class OptionsDetailsLookup( private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x,e.y)
        if (view != null ){
            val holder  = recyclerView.getChildViewHolder(view)
            if (holder is OptionsHolder) return  holder.details
        }
        return null
    }
}

class OptionsKeyProvider : ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED){
    override fun getKey(position: Int): Long? {
        return position.toLong()
    }

    override fun getPosition(key: Long): Int {
        return key.toInt()
    }
}