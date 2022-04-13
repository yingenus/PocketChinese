package com.yingenus.pocketchinese.presentation.views.suggestist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.activity.getSuggestActivityIntent
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import com.yingenus.pocketchinese.view.HeadersRecyclerViewAdapter
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import javax.inject.Inject

class NewSuggestListsFragment : Fragment(R.layout.suggest_lists_layout), SuggestListAdapter.OnSuggestListClicked {

    @Inject
    lateinit var viewModelFactory : ViewModelFactory

    private lateinit var viewModel: SuggestListsViewModel

    private var chipGroup: ChipGroup? = null
    private var recyclerView : RecyclerView? = null
    private val chips : MutableMap<String,Int> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        viewModel = ViewModelProvider(this, viewModelFactory).get(SuggestListsViewModel::class.java)

        chipGroup = view.findViewById(R.id.tags_group)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView!!.adapter = SuggestListAdapter().also { it.setOnClickListener(this) }
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())

        subscribeToAll()

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateSuggestLists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chipGroup = null
        (recyclerView!!.adapter as SuggestListAdapter).deleteOnClickListener(this)
        recyclerView = null
    }

    override fun onClicked(showedStudyList: SuggestListsViewModel.ShovedSuggestList) {
        val intent = getSuggestActivityIntent(requireContext(),showedStudyList.name)
        startActivity(intent)
    }

    private fun subscribeToAll(){
        viewModel.newSuggestLists.observe(viewLifecycleOwner){
            val adapter = (recyclerView!!.adapter as SuggestListAdapter)
            adapter.setNew(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.otherSuggestLists.observe(viewLifecycleOwner){
            val adapter = (recyclerView!!.adapter as SuggestListAdapter)
            adapter.setOther(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.tags.observe(viewLifecycleOwner){
            it.forEach {
                if (!chips.containsKey(it.second)){
                    val id = View.generateViewId()
                    val chip = layoutInflater.inflate(
                        R.layout.cat_chip_group_item_choice,
                        chipGroup,
                        false
                    ) as Chip
                    chip.text = tag
                    chip.id = id
                    chips[it.second] = id
                }
                val chip = chipGroup!!.findViewById(chips[it.second]!!) as Chip
                chip.isChecked = it.first
            }
            for( key in chips.keys){
                if (!it.any { it.second == key }){
                    val view = chipGroup!!.findViewById(chips[key]!!) as Chip
                    chipGroup!!.removeView(view)
                }
            }
        }
    }
}

class SuggestListAdapter : HeadersRecyclerViewAdapter<SuggestListViewHolder>(
    listOf(TAG_NEW, TAG_OTHER),
    false
){
    companion object{
        private const val TAG_NEW = "new"
        private const val TAG_OTHER = "other"
    }

    interface OnSuggestListClicked{
        fun onClicked( showedStudyList: SuggestListsViewModel.ShovedSuggestList)
    }

    private val listeners : MutableList<OnSuggestListClicked> = mutableListOf()

    private var newList : List<SuggestListsViewModel.ShovedSuggestList> = emptyList()
    private var otherList : List<SuggestListsViewModel.ShovedSuggestList> = emptyList()

    fun setNew(lists : List<SuggestListsViewModel.ShovedSuggestList>){
        newList = lists

    }

    fun setOther(lists : List<SuggestListsViewModel.ShovedSuggestList>){
        otherList = lists
    }

    fun setOnClickListener(listener : OnSuggestListClicked){
        listeners.add(listener)
    }

    fun deleteOnClickListener(listener : OnSuggestListClicked){
        listeners.remove(listener)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): SuggestListViewHolder {
        return SuggestListViewHolder(
            parent.context.getSystemService(LayoutInflater::class.java.name) as LayoutInflater,
            parent
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewViewHolder(R.layout.list_header,
            parent.context.getSystemService(LayoutInflater::class.java.name) as LayoutInflater)
    }

    override fun onBindItemViewHolder(
        holder: SuggestListViewHolder,
        position: Int,
        tag: String
    ) {
        when(tag){
            TAG_NEW -> {
                val item = newList[position]
                holder.bind(item)
                holder.itemView.setOnClickListener { listeners.forEach { it.onClicked(item) } }
            }
            TAG_OTHER -> {
                val item = otherList[position]
                holder.bind(otherList[position])
                holder.itemView.setOnClickListener { listeners.forEach { it.onClicked(item) } }
            }
            else -> throw RuntimeException("unexpected tag : $tag")
        }
    }

    override fun onBindHeaderViewHolder(holder: ViewViewHolder, tag: String) {
        when(tag){
            TAG_NEW -> holder.itemView.findViewById<TextView>(R.id.title).text =
                holder.itemView.context.getString(R.string.suggest_lists_new)
            TAG_OTHER -> holder.itemView.findViewById<TextView>(R.id.title).text =
                holder.itemView.context.getString(R.string.suggest_lists_other)
        }
    }

    override fun getItemCount(tag: String): Int {
        return when(tag){
            TAG_NEW -> newList.size
            TAG_OTHER -> otherList.size
            else -> throw RuntimeException("unexpected tag : $tag")
        }
    }
}

class SuggestListViewHolder : RecyclerView.ViewHolder{

    private val imageView : ImageView
    private val name : TextView
    private val words : TextView

    constructor( inflater: LayoutInflater, viewGroup: ViewGroup)
            : super(inflater.inflate(R.layout.suggest_list_holder,viewGroup, false))
    {
        imageView = itemView.findViewById(R.id.image)
        name = itemView.findViewById(R.id.name)
        words = itemView.findViewById(R.id.words)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item : SuggestListsViewModel.ShovedSuggestList){
        item.image ?: imageView.setImageURI(item.image)
        name.text = item.name
        words.text = super.itemView.context.getString(R.string.count_words) + item.words.toString()
    }

}