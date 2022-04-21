package com.yingenus.pocketchinese.presentation.dialogs

import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class StartTrainingViewModelFactory @AssistedInject constructor(
    @Assisted private val studyListId : Long
) : ViewModelFactory() {

    @AssistedFactory
    interface Builder{
        fun create(studyListId: Long): StartTrainingViewModelFactory
    }

    @Inject
    lateinit var startTrainingViewModel: StartTrainingViewModel.Builder

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            StartTrainingViewModel::class.java-> startTrainingViewModel.build(studyListId)
            else -> super.create(modelClass)
        } as T
    }
}