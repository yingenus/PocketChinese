package com.yingenus.pocketchinese.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.presentation.views.suggestist.SuggestListsViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject constructor() : ViewModelProvider.Factory{

    @Inject
    lateinit var suggestListsViewModelProvider : Provider<SuggestListsViewModel>

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            SuggestListsViewModel::class.java -> suggestListsViewModelProvider.get()
            else -> throw IllegalArgumentException("ViewModelFactory cant create ${modelClass.name}")
        } as T
    }
}