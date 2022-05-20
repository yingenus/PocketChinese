package com.yingenus.pocketchinese.presentation.views.addword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class AddWordsViewModelFactory @AssistedInject constructor(
    @Assisted private val addedWords : List<SuggestWord>)
    : ViewModelProvider.Factory {

    @AssistedFactory
    interface Builder{
        fun create(addedWords : List<SuggestWord>): AddWordsViewModelFactory
    }

    @Inject
    lateinit var addWordsViewModel : AddWordsViewModel.Factory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            AddWordsViewModel::class.java -> addWordsViewModel.create(addedWords)
            else -> throw IllegalArgumentException()
        } as T
    }
}
