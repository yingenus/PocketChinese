package com.yingenus.pocketchinese.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.presentation.views.suggestist.SuggestListsViewModel
import com.yingenus.pocketchinese.presentation.views.userlist.RepeatableUserListViewModel
import com.yingenus.pocketchinese.presentation.views.userlist.UserListsViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

open class ViewModelFactory @Inject constructor() : ViewModelProvider.Factory{

    @Inject
    lateinit var suggestListsViewModelProvider : Provider<SuggestListsViewModel>
    @Inject
    lateinit var userListsViewModel: Provider<UserListsViewModel>
    @Inject
    lateinit var repeatableUserListViewModel: Provider<RepeatableUserListViewModel>

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            SuggestListsViewModel::class.java -> suggestListsViewModelProvider.get()
            UserListsViewModel::class.java -> userListsViewModel.get()
            RepeatableUserListViewModel::class.java -> repeatableUserListViewModel.get()
            else -> throw IllegalArgumentException("ViewModelFactory cant create ${modelClass.name}")
        } as T
    }
}