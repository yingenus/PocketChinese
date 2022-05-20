package com.yingenus.pocketchinese.view

import android.view.View
import android.view.ViewGroup
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

abstract class  HeadersRecyclerViewAdapter<T : RecyclerView.ViewHolder> constructor(
    private val tags : List<String>,
    private val showEmptyTags : Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        private const val HEADER = 0
        private const val ITEM = 1
    }


    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)){
            HEADER
        }else{
            ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            HEADER -> onCreateHeaderViewHolder(parent);
            ITEM -> onCreateItemViewHolder(parent)
            else -> throw RuntimeException("invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            HEADER -> onBindHeaderViewHolder(holder as ViewViewHolder, getTagForHeader(position))
            ITEM ->{
                val tag_pos = getPositionAndTagForItem(position)
                onBindItemViewHolder(holder as T, tag_pos.second,tag_pos.first)
            }
        }
    }

    override fun getItemCount(): Int {
        return tags.map {
            val items = getItemCount(it)
            if (items!= 0 || showEmptyTags) items + 1
            else 0
        }
            .reduce {
                    acc, i -> acc + i
            }
    }

    private fun isHeader(position: Int): Boolean{
        var position_h = 0
        var index = 0
        while (index <= tags.lastIndex) {
            if (position == position_h) return true
            val items = getItemCount(tags[index])
            position_h+= if(items == 0 ) if (showEmptyTags) 1 else 0 else items + 1
            index++
        }
        return false
    }

    private fun getTagForHeader(position : Int): String{
        var position_h = 0
        var index = 0
        while (index <= tags.lastIndex) {
            val items = getItemCount(tags[index])
            if (position == position_h && (items >0 || showEmptyTags)) return tags[index]
            position_h+= if(items == 0) if (showEmptyTags) 1 else 0 else items + 1
            index++
        }
        throw IllegalArgumentException("is not header")
    }

    private fun getPositionAndTagForItem(position: Int): Pair<String,Int>{
        var position_h = 0
        var index = 0
        while (index <= tags.lastIndex) {
            val items = getItemCount(tags[index])
            val position_h_next =
                if (items == 0 ){
                    if (showEmptyTags) position_h + 1
                    else position_h
                }
                else position_h + items + 1
            if (position in (position_h + 1) until position_h_next){
                return tags[index] to position - position_h - 1
            }
            position_h = position_h_next
            index++
        }
        throw IllegalArgumentException("cant find item for this position: $position")
    }

    abstract fun onCreateItemViewHolder(parent: ViewGroup) : T
    abstract fun onCreateHeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
    abstract fun onBindItemViewHolder(holder: T, position: Int, tag: String)
    abstract fun onBindHeaderViewHolder(holder : ViewViewHolder, tag : String)
    abstract fun getItemCount(tag: String) : Int
}