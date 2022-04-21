package com.yingenus.pocketchinese.presentation.views.stydylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class UserStudyListViewModelFactory @AssistedInject constructor(
    @Assisted val studyListId : Long) : ViewModelProvider.Factory {

    @AssistedFactory
    interface Builder{
        fun create(studyListId : Long) : UserStudyListViewModelFactory
    }

    @Inject
    lateinit var userStudyListViewModelBuilder: UserStudyListViewModel.Builder

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            UserStudyListViewModel::class.java -> userStudyListViewModelBuilder.create(studyListId)
            else -> null
        } as T
    }
}