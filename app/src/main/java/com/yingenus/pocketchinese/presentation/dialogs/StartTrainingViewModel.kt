package com.yingenus.pocketchinese.presentation.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.TrainedWords
import com.yingenus.pocketchinese.domain.usecase.TrainedWordsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class StartTrainingViewModel @AssistedInject constructor(
    @Assisted private val studyListId : Long,
    private val trainedWordsUseCase: TrainedWordsUseCase,
    private val settings : ISettings
) : ViewModel()
{
    @AssistedFactory
    interface Builder{
        fun build(studyListId : Long): StartTrainingViewModel
    }

    private val _showChinese : MutableLiveData<Boolean> = MutableLiveData()
    val showChinese : LiveData<Boolean>
        get() = _showChinese

    private val _showPinyin : MutableLiveData<Boolean> = MutableLiveData()
    val showPinyin : LiveData<Boolean>
        get() = _showPinyin

    private val _showTranslation : MutableLiveData<Boolean> = MutableLiveData()
    val showTranslation : LiveData<Boolean>
        get() = _showTranslation

    private val _statisticChinese : MutableLiveData<TrainedWords> = MutableLiveData()
    val statisticChinese : LiveData<TrainedWords>
        get() = _statisticChinese

    private val _statisticPinyin : MutableLiveData<TrainedWords> = MutableLiveData()
    val statisticPinyin : LiveData<TrainedWords>
        get() = _statisticPinyin

    private val _statisticTranslation : MutableLiveData<TrainedWords> = MutableLiveData()
    val statisticTranslation : LiveData<TrainedWords>
        get() = _statisticTranslation

    fun updateStatistic(){
        updateChinese()
            .andThen{
                updateTranslation()
                    .andThen {
                        updatePinyin().subscribe()
                    }
                    .subscribe()
            }
            .subscribe()
    }

    private fun updateChinese(): Completable{
        return trainedWordsUseCase.getTrainedWords(Language.CHINESE, studyListId)
            .doOnSuccess {
                _showChinese.postValue(!settings.getRepeatType().ignoreCHN)
                _statisticChinese.postValue(it)
            }.ignoreElement()
    }

    private fun updatePinyin(): Completable{
        return trainedWordsUseCase.getTrainedWords(Language.PINYIN, studyListId)
            .doOnSuccess {
                _showPinyin.postValue(!settings.getRepeatType().ignorePIN)
                _statisticPinyin.postValue(it)
            }.ignoreElement()
    }

    private fun updateTranslation(): Completable{
        return trainedWordsUseCase.getTrainedWords(Language.RUSSIAN, studyListId)
            .doOnSuccess {
                _showTranslation.postValue(!settings.getRepeatType().ignoreTRN)
                _statisticTranslation.postValue(it)
            }.ignoreElement()
    }

}