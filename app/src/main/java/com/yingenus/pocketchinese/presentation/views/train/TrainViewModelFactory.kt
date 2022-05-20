package com.yingenus.pocketchinese.presentation.views.train

import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import com.yingenus.pocketchinese.presentation.ViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider

class TrainViewModelFactory @AssistedInject constructor(
    @Assisted private val trainingConf: TrainingConf
) : ViewModelFactory() {

    @AssistedFactory
    interface Builder{
        fun create(trainingConf: TrainingConf): TrainViewModelFactory
    }

    @Inject
    lateinit var trainingViewModel: Provider<TrainingViewModel.Factory>

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            TrainingViewModel::class.java -> trainingViewModel.get().create(trainingConf)
            else -> throw  IllegalArgumentException()
        } as T
    }
}