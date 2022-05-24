package com.yingenus.pocketchinese.presentation.views.userlist

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.BoundsDecoratorBottom
import com.yingenus.pocketchinese.controller.CardBoundTopBottom
import com.yingenus.pocketchinese.controller.dialog.CreateNewListDialog
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams
import com.yingenus.pocketchinese.domain.entitiys.UtilsVariantParams.resolveColorAttr
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import com.yingenus.pocketchinese.presentation.dialogs.ActioneSheetDialog
import com.yingenus.pocketchinese.presentation.dialogs.ActioneSheetDialog.EditSheetDialogCallback
import com.yingenus.pocketchinese.presentation.dialogs.RenameDialog
import com.yingenus.pocketchinese.presentation.views.stydylist.StudyListActivity
import com.yingenus.pocketchinese.view.HeadersRecyclerViewAdapter
import com.yingenus.pocketchinese.view.holders.ViewViewHolder
import javax.inject.Inject

class UserListsFragment : Fragment(R.layout.user_lists_fragment), StudyListAdapter.OnUserListLongClicked, StudyListAdapter.OnUserListClicked{

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: UserListsViewModel

    private var repeated : TextView? = null
    private var added : TextView? = null
    private var successChnProgress : LinearProgressIndicator? = null
    private var successPinProgress : LinearProgressIndicator? = null
    private var successTrnProgress : LinearProgressIndicator? = null
    private var successChnPercent : TextView? = null
    private var successPinPercent : TextView? = null
    private var successTrnPercent : TextView? = null
    private var toolbar : Toolbar? = null

    private var recyclerView : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)!!

        PocketApplication.getAppComponent().injectUserListFragment(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(UserListsViewModel::class.java)

        repeated = view.findViewById(R.id.repeated_words)
        added = view.findViewById(R.id.added_words)
        successChnPercent = view.findViewById(R.id.percent_chn)
        successChnProgress = view.findViewById(R.id.progress_chn)
        successPinPercent = view.findViewById(R.id.percent_pin)
        successPinProgress = view.findViewById(R.id.progress_pin)
        successTrnPercent = view.findViewById(R.id.percent_trn)
        successTrnProgress = view.findViewById(R.id.progress_trn)
        toolbar = view.findViewById(R.id.toolbar)
        toolbar!!.setOnMenuItemClickListener {
            if(it.itemId == R.id.create_new){
                onCreteUserListClicked()
                true
            }
            else false
        }

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(requireContext())
        recyclerView!!.adapter = StudyListAdapter().also {
            it.setOnLongClickListener(this)
            it.setOnClickListener(this)
        }
        recyclerView!!.addItemDecoration(CardBoundTopBottom(requireContext(),4))
        recyclerView!!.addItemDecoration(BoundsDecoratorBottom(requireContext()))

        initListeners()

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStudyLists()
        viewModel.updateStatistic()
    }

    private fun initListeners(){
        viewModel.addedWords.observe(viewLifecycleOwner){
            added!!.text = it.toString()
        }
        viewModel.repeatedWords.observe(viewLifecycleOwner){
            repeated!!.text = it.toString()
        }
        viewModel.progressChinese.observe(viewLifecycleOwner){
            successChnProgress?.progress = it
            successChnPercent?.text = "$it%"
        }
        viewModel.progressPinyin.observe(viewLifecycleOwner){
            successPinProgress?.progress = it
            successPinPercent?.text = "$it%"
        }
        viewModel.progressTranslation.observe(viewLifecycleOwner){
            successTrnProgress?.progress = it
            successTrnPercent?.text = "$it%"
        }
        viewModel.showedNeedRepeatUserList.observe(viewLifecycleOwner){
            val adapter = (recyclerView!!.adapter as StudyListAdapter)
            adapter.setNew(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.showedOtherUserList.observe(viewLifecycleOwner){
            val adapter = (recyclerView!!.adapter as StudyListAdapter)
            adapter.setOther(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repeated = null
        added = null
        successChnPercent = null
        successChnProgress = null
        successPinPercent = null
        successPinProgress = null
        successTrnPercent = null
        successTrnProgress = null
        (recyclerView?.adapter as StudyListAdapter).deleteOnLongClickListener(this)
        (recyclerView?.adapter as StudyListAdapter).deleteOnClickListener(this)
        recyclerView = null
        toolbar = null
    }

    override fun onLongClicked(showedStudyList: ShowedStudyList) {
        val bottomSheetDialog = ActioneSheetDialog()
        bottomSheetDialog.show(childFragmentManager, "action_dialog")

        bottomSheetDialog.observer = object : EditSheetDialogCallback {
            override fun onRemove() {
                bottomSheetDialog.dismiss()
                viewModel.deleteStudyList(showedStudyList.name)
            }

            override fun onRename() {
                val dialog = RenameDialog(showedStudyList,viewModel)
                dialog.show(childFragmentManager, "rename_dialog")
            }
        }
    }

    override fun onClicked(showedStudyList: ShowedStudyList) {
        val intent = StudyListActivity.getIntent(requireContext(),showedStudyList.id)
        startActivity(intent)
    }

    private fun onCreteUserListClicked(){
        val dialog = CreateNewListDialog(viewModel)
        dialog.show(childFragmentManager, "action_dialog")
    }

}
class StudyListAdapter : HeadersRecyclerViewAdapter<UserListViewHolder>(
    listOf(TAG_NEW, TAG_OTHER),
    false
){
    companion object{
        private const val TAG_NEW = "new"
        private const val TAG_OTHER = "other"
    }

    interface OnUserListLongClicked{
        fun onLongClicked( showedStudyList: ShowedStudyList)
    }
    interface OnUserListClicked{
        fun onClicked( showedStudyList: ShowedStudyList)
    }

    private val longListeners : MutableList<OnUserListLongClicked> = mutableListOf()
    private val listeners : MutableList<OnUserListClicked> = mutableListOf()

    private var newList : List<ShowedStudyList> = emptyList()
    private var otherList : List<ShowedStudyList> = emptyList()

    fun setNew(lists : List<ShowedStudyList>){
        newList = lists
    }

    fun setOther(lists : List<ShowedStudyList>){
        otherList = lists
    }

    fun setOnLongClickListener(listener : OnUserListLongClicked){
        longListeners.add(listener)
    }

    fun deleteOnLongClickListener(listener : OnUserListLongClicked){
        longListeners.remove(listener)
    }

    fun setOnClickListener(listener : OnUserListClicked){
        listeners.add(listener)
    }

    fun deleteOnClickListener(listener : OnUserListClicked){
        listeners.remove(listener)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): UserListViewHolder {
        return UserListViewHolder(
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            parent
        )
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewViewHolder(R.layout.list_header,
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
    }

    override fun onBindItemViewHolder(
        holder: UserListViewHolder,
        position: Int,
        tag: String
    ) {
        when(tag){
            TAG_NEW -> {
                val item = newList[position]
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
            TAG_NEW ->{
                val textView = holder.itemView.findViewById<TextView>(R.id.title)
                textView.text = holder.itemView.context.getString(R.string.need_repeat_list)
                 UtilsVariantParams.apply { textView.setTextColor( holder.itemView.context.resolveColorAttr(android.R.attr.colorError)) }
            }
            TAG_OTHER -> holder.itemView.findViewById<TextView>(R.id.title).text =
                holder.itemView.context.getString(R.string.user_lists_other)
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

class UserListViewHolder : RecyclerView.ViewHolder{

    private val name : TextView
    private val words : TextView
    private val progress : LinearProgressIndicator
    private val progressPercent : TextView
    private val lastRepeat : TextView
    private val notify : View

    constructor( inflater: LayoutInflater, viewGroup: ViewGroup)
            : super(inflater.inflate(R.layout.user_list_holder,viewGroup, false))
    {
        notify = itemView.findViewById(R.id.icon_notify)
        name = itemView.findViewById(R.id.name)
        words = itemView.findViewById(R.id.words)
        progress = itemView.findViewById(R.id.progress)
        progressPercent = itemView.findViewById(R.id.percent)
        lastRepeat = itemView.findViewById(R.id.last_repeat)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item : ShowedStudyList){
        name.text = item.name
        words.text = super.itemView.context.getString(R.string.count_words) + item.words.toString()
        progress.progress = item.percentComplete
        progressPercent.text = item.percentComplete.toString()+"%"
        lastRepeat.text = UtilsVariantParams.getLstRepeat(itemView.resources,item.repeatDate)
        lastRepeat.setTextColor(UtilsVariantParams.getLstRepeatColor(itemView,item.repeat))
        if (!item.notifyUser)
            notify.visibility = View.VISIBLE
        else
            notify.visibility = View.GONE
    }

}