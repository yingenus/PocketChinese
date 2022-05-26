package com.yingenus.pocketchinese.presentation.views.suggestist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.BoundsDecoratorBottom
import com.yingenus.pocketchinese.controller.CardBoundTopBottom
import com.yingenus.pocketchinese.controller.dp2px
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams.resolveColorAttr
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import com.yingenus.pocketchinese.view.HeadersRecyclerViewAdapter
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import java.lang.ref.WeakReference
import javax.inject.Inject

class SuggestListsFragment : Fragment(R.layout.suggest_lists_layout), SuggestListAdapter.OnSuggestListClicked, AppBarLayout.OnOffsetChangedListener {

    @Inject
    lateinit var viewModelFactory : ViewModelFactory

    lateinit var viewModel: SuggestListsViewModel

    private var chipGroup: ChipGroup? = null
    private var recyclerView : RecyclerView? = null
    private var toolbar : Toolbar? = null
    private var appBarLayout : AppBarLayout? = null
    private val chips : MutableMap<String,Int> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        PocketApplication.getAppComponent().injectSuggestListsFragment(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SuggestListsViewModel::class.java)

        chipGroup = view.findViewById(R.id.tags_group)
        recyclerView = view.findViewById(R.id.recyclerview)
        appBarLayout = view.findViewById(R.id.app_bar_layout)
        toolbar = view.findViewById(R.id.toolbar)

        appBarLayout!!.addOnOffsetChangedListener(this)

        recyclerView!!.adapter = SuggestListAdapter(this).also { it.setOnClickListener(this) }
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        recyclerView!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))

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
        appBarLayout!!.removeOnOffsetChangedListener(this)
        appBarLayout = null
        toolbar = null
        (recyclerView!!.adapter as SuggestListAdapter).deleteOnClickListener(this)
        recyclerView = null
    }

    override fun onClicked(showedStudyList: SuggestListsViewModel.ShovedSuggestList) {
        val intent = getSuggestActivityIntent(requireContext(),showedStudyList.name)
        startActivity(intent)
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
        val pix = dp2px(offset, requireContext())

        if (toolbar!!.elevation.toInt() != pix){
            toolbar!!.elevation = pix.toFloat()
        }
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
                    chip.text = it.second
                    chip.id = id
                    chips[it.second] = id
                    chipGroup!!.addView(chip)
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
            chipGroup?.requestLayout()
        }
    }
}

class SuggestListAdapter(val suggestListsFragment: SuggestListsFragment) : HeadersRecyclerViewAdapter<SuggestListViewHolder>(
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

    private val observableLiveData = mutableMapOf<Int,LiveData<Bitmap>>()

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
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            parent
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewViewHolder(R.layout.list_header,
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
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
                subscribeHolder(item.image,holder)
            }
            TAG_OTHER -> {
                val item = otherList[position]
                holder.bind(otherList[position])
                holder.itemView.setOnClickListener { listeners.forEach { it.onClicked(item) } }
                subscribeHolder(item.image,holder)
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

    private fun subscribeHolder(image : String, holder : SuggestListViewHolder){
        observableLiveData[holder.hashCode()]?.removeObservers(suggestListsFragment)
        val liveData = suggestListsFragment.viewModel.getImageBitmap(image)
        liveData.observe(suggestListsFragment,holder)
        observableLiveData[holder.hashCode()] = liveData
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        observableLiveData[holder.hashCode()]?.removeObservers(suggestListsFragment)
    }
}

class SuggestListViewHolder : RecyclerView.ViewHolder, Observer<Bitmap>{

    private val imageView : ImageView
    private val name : TextView
    private val words : TextView

    constructor( inflater: LayoutInflater, viewGroup: ViewGroup)
            : super(inflater.inflate(R.layout.suggest_list_holder,viewGroup, false))
    {
        imageView = itemView.findViewById(R.id.icone)
        name = itemView.findViewById(R.id.name)
        words = itemView.findViewById(R.id.words)
    }

    override fun onChanged(t: Bitmap?) {
        imageView.setImageBitmap(t)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item : SuggestListsViewModel.ShovedSuggestList){
        //item.image ?: imageView.setImageURI(item.image)
        name.text = item.name
        words.text = super.itemView.context.getString(R.string.count_words) + item.words.toString()
    }

}