package com.yingenus.pocketchinese.presentation.views.addword

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.BoundsDecoratorBottom
import com.yingenus.pocketchinese.controller.CardBoundTopBottom
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams
import com.yingenus.pocketchinese.view.holders.ViewViewHolder

class ChooseListFragment : Fragment(R.layout.chouse_list_layout){

    interface Callbacks{
        fun sumSelected(isit : Boolean)
    }

    val callbacks : Callbacks? = null

    private var recyclerView : RecyclerView? = null
    private var selectionTracker : SelectionTracker<Long>? = null

    var studyLists : List<ShowedStudyList> = emptyList()
    set(value) {
        field = value
        recyclerView?.let {
            (recyclerView!!.adapter as ChooseListAdapter).showedStudyList = studyLists
            (recyclerView!!.adapter as ChooseListAdapter).notifyDataSetChanged()
        }
    }

    var editError: String? = null
    set(value){
        field = value
        recyclerView?.let {
            (recyclerView!!.adapter as ChooseListAdapter).errorMsg= value
            (recyclerView!!.adapter as ChooseListAdapter).notifyDataSetChanged()
        }
    }

    fun isSmhSelected(): Boolean{
        return ! (selectionTracker?.selection?.isEmpty?:true)
    }

    fun isListSelected(): Boolean{
        val item  = selectionTracker?.selection?.toList()?.first()
        return item!= null && item >= 3L
    }

    fun getNewListName(): String?{
        val isNew  = selectionTracker?.isSelected(1L)?: false
        if (isNew){
            return (recyclerView!!.adapter as ChooseListAdapter).newListName
        }
        return null
    }

    fun getSelectedListId(): Long{
        val isNew  = selectionTracker?.isSelected(1L)?: false
        val item  = selectionTracker?.selection?.toList()?.first()
        if (!isNew && item != null){
            return (recyclerView!!.adapter as ChooseListAdapter).showedStudyList.get(item.toInt() -3).id
        }
        return -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view =  super.onCreateView(inflater, container, savedInstanceState)!!

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        recyclerView!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))
        recyclerView!!.adapter = ChooseListAdapter()
        (recyclerView!!.adapter as ChooseListAdapter).showedStudyList = studyLists

        selectionTracker = SelectionTracker.Builder<Long>(
            "list_selection",
            recyclerView!!,
            ListKeyProvider(),
            ListDetailsLookup(recyclerView!!),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(ListSelectionPredicate())
            .build()

        (recyclerView!!.adapter as ChooseListAdapter).selectionTracker = selectionTracker

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView = null
    }
}

class ChooseListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        private const val TITLE_1 = 0
        private const val NEW_LIST = 1
        private const val TITLE_2 = 2
        private const val LISTS = 3
    }

    var showedStudyList : List<ShowedStudyList> = emptyList()
    var newListName : String = ""
    var errorMsg : String? = null
    var selectionTracker : SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return when(viewType){
            TITLE_1 -> HeaderViewHolder(layoutInflater,parent)
            NEW_LIST -> CreateListVewHolder(layoutInflater,parent)
            TITLE_2 -> HeaderViewHolder(layoutInflater,parent)
            LISTS -> UserListViewHolder(layoutInflater,parent)
            else -> throw RuntimeException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is HeaderViewHolder ->{
                val resources = holder.itemView.context.resources
                holder.bind( if (position== 0) resources.getString(R.string.choose_list_new) else resources.getString(R.string.choose_list_exist),position)
            }
            is CreateListVewHolder ->{
                holder.bind(newListName,position,selectionTracker)
                holder.editText.editText?.addTextChangedListener {
                    newListName = it.toString()
                }
                holder.editText.error = errorMsg
            }
            is UserListViewHolder ->{
                holder.bind(showedStudyList.get(position-3),position,selectionTracker)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TITLE_1
            1 -> NEW_LIST
            2 -> TITLE_2
            else -> LISTS
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return 3 + showedStudyList.size
    }
}

class HeaderViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
    ViewViewHolder(R.layout.list_header,inflater,viewGroup){

    val details : ListDetails = ListDetails(false)

    fun bind(text : String, position: Int){
        itemView.findViewById<TextView>(R.id.title).text = text
        details.position = position.toLong()
    }
}

class CreateListVewHolder(inflater: LayoutInflater, viewGroup: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.create_word_card,viewGroup, false)){

        val editText : TextInputLayout
        val details : ListDetails = ListDetails(true)

        init {
            editText = itemView.findViewById(R.id.edit_name)
        }

        fun bind(text: String,position: Int, selectionTracker: SelectionTracker<Long>?){
            details.position = position.toLong()
            editText.editText?.text?.clear()
            editText.editText?.text?.insert(0,text)
            selectionTracker?.let { bindSelection(selectionTracker) }
        }

        private fun bindSelection(selectionTracker: SelectionTracker<Long>){
            if (itemView is MaterialCardView)
                (itemView as MaterialCardView).isChecked = selectionTracker.isSelected(details.position)
        }



}

class UserListViewHolder : RecyclerView.ViewHolder{

    private val name : TextView
    private val words : TextView
    private val progress : LinearProgressIndicator
    private val progressPercent : TextView
    private val lastRepeat : TextView
    private val notify : View

    val details : ListDetails = ListDetails(true)

    constructor(inflater: LayoutInflater, viewGroup: ViewGroup)
            : super(inflater.inflate(R.layout.user_list_holder,viewGroup, false))
    {
        notify = itemView.findViewById(R.id.icon_notify)
        name = itemView.findViewById(R.id.name)
        words = itemView.findViewById(R.id.words)
        progress = itemView.findViewById(R.id.progress)
        progressPercent = itemView.findViewById(R.id.percent)
        lastRepeat = itemView.findViewById(R.id.last_repeat)
        lastRepeat.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    fun bind(item : ShowedStudyList, position : Int, selectionTracker: SelectionTracker<Long>?){
        name.text = item.name
        words.text = super.itemView.context.getString(R.string.count_words) + item.words.toString()
        progress.progress = item.percentComplete
        progressPercent.text = item.percentComplete.toString()+"%"
        lastRepeat.text = UtilsVariantParams.getLstRepeat(itemView.resources,item.repeatDate)
        lastRepeat.setTextColor(UtilsVariantParams.getLstRepeatColor(itemView,item.repeat))
        details.position = position.toLong()
        selectionTracker?.let {  bindSelection(selectionTracker) }
    }

    private fun bindSelection(selectionTracker: SelectionTracker<Long>){
        if (itemView is MaterialCardView)
            (itemView as MaterialCardView).isChecked = selectionTracker.isSelected(details.selectionKey)
    }

}

class ListDetails( val isItem : Boolean) : ItemDetailsLookup.ItemDetails<Long>(){

    var position : Long = -1


    override fun getPosition(): Int {
        return position.toInt()
    }

    override fun getSelectionKey(): Long? {
        return position
    }

    override fun inSelectionHotspot(e: MotionEvent): Boolean {
        return false
    }

    override fun inDragRegion(e: MotionEvent): Boolean {
        return true
    }
}

class ListKeyProvider : ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED){
    override fun getKey(position: Int): Long {
        return position.toLong()
    }

    override fun getPosition(key: Long): Int {
        return key.toInt()
    }
}

class ListDetailsLookup( private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x,e.y)
        if (view != null ){
            val holder  = recyclerView.getChildViewHolder(view)
            when(holder){
                is UserListViewHolder -> return holder.details
                is CreateListVewHolder -> return holder.details
                is HeaderViewHolder -> return holder.details
            }
        }
        return null
    }
}

class ListSelectionPredicate : SelectionTracker.SelectionPredicate<Long>(){
    override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
        return when(key){
            0L,2L -> false
            else -> true
        }
    }

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
        return when(position){
            0,2 -> false
            else -> true
        }
    }

    override fun canSelectMultiple(): Boolean {
        return false
    }
}