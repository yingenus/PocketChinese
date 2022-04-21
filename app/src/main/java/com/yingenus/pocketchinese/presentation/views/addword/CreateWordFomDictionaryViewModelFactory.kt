package com.yingenus.pocketchinese.presentation.views.addword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordFomDictionaryViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class CreateWordFomDictionaryViewModelFactory @AssistedInject constructor(
   @Assisted private val item : DictionaryItem
): ViewModelProvider.Factory {
    @AssistedFactory
    interface Builder{
        fun create(item : DictionaryItem): CreateWordFomDictionaryViewModelFactory
    }

    @Inject
    lateinit var createWordFomDictionaryViewModel: CreateWordFomDictionaryViewModel.Factory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            CreateWordFomDictionaryViewModel::class -> createWordFomDictionaryViewModel.create(item)
            else -> throw IllegalArgumentException()
        } as T
    }
}