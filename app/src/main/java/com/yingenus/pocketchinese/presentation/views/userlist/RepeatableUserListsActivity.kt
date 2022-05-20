package com.yingenus.pocketchinese.presentation.views.userlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import javax.inject.Inject


class RepeatableUserListsActivity : AppCompatActivity(),StudyListAdapter.OnUserListClicked {

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, RepeatableUserListsActivity::class.java)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: RepeatableUserListViewModel

    private var toolbar: Toolbar? = null
    private var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        PocketApplication.setupApplication()

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(RepeatableUserListViewModel::class.java)

        super.onCreate(savedInstanceState)
        PocketApplication.postStartActivity(this,false)
        setContentView(R.layout.repeatable_list_layout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar!!.setNavigationOnClickListener { finish() }
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = UserListAdapter()

        viewModel.showedNeedRepeatUserList.observe(this){
            val adapter = (recyclerView!!.adapter as UserListAdapter)
            adapter.setStudyLists(it)
            adapter.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.updateStudyLists()
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbar = null
        (recyclerView!!.adapter as UserListAdapter).deleteOnClickListener(this)
    }

    override fun onClicked(showedStudyList: ShowedStudyList) {
        TODO("Not yet implemented")
    }

    class UserListAdapter : RecyclerView.Adapter<UserListViewHolder>(){
        private var userLists : List<ShowedStudyList> = emptyList()
        private val listeners : MutableList<StudyListAdapter.OnUserListClicked> = mutableListOf()

        fun setOnClickListener(listener : StudyListAdapter.OnUserListClicked){
            listeners.add(listener)
        }

        fun deleteOnClickListener(listener : StudyListAdapter.OnUserListClicked){
            listeners.remove(listener)
        }

        fun setStudyLists(lists : List<ShowedStudyList>){
            userLists = lists
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
            return UserListViewHolder(parent.context.getSystemService(LayoutInflater::class.java.name) as LayoutInflater, parent)
        }

        override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
            holder.bind(userLists[position])
            holder.itemView.setOnClickListener {
                listeners.forEach { it.onClicked(userLists[position]) }
            }
        }

        override fun getItemCount(): Int {
            return userLists.size
        }
    }


}