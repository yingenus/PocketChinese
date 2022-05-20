package com.yingenus.pocketchinese.presentation.views.addword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.domain.dto.AddWordsConfig
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.domain.usecase.AddSuggestWordsToStudyList
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyListUseCase
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe

class AddWordsViewModel @AssistedInject constructor(
    @Assisted private val addedWords : List<SuggestWord>,
    private val addSuggestWordsToStudyList: AddSuggestWordsToStudyList,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase
) : ViewModel(){

    @AssistedFactory
    interface Factory{
        fun create( addedWords: List<SuggestWord>) : AddWordsViewModel
    }

    private var needMixWords = false
        set(value) {
            _mixWords.postValue(value)
            field = value
        }
    private var needSimplifyPinYin = false
        set(value) {
            _simplifyPinYin.postValue(value)
            field = value
        }

    private val _mixWords : MutableLiveData<Boolean> = MutableLiveData(false)
    val mixWords : LiveData<Boolean>
        get() = _mixWords

    private val _simplifyPinYin : MutableLiveData<Boolean> = MutableLiveData(false)
    val simplifyPinYin : LiveData<Boolean>
        get() = _simplifyPinYin

    private val _showedUserLists : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedUserLists : LiveData<List<ShowedStudyList>>
        get() = _showedUserLists

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error

    fun updateStudyLists(){
        studyListInfoUseCase
            .getAllStudyLists()
            .subscribe { onSuccess ->
                _showedUserLists.postValue(onSuccess)
            }
    }

    fun checkUseName(studyListName: String) : LiveData<Boolean>{
        val isUsed : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyListUseCase
            .containsName(studyListName)
            .subscribe { used ->
                isUsed.postValue(used)
            }
        return isUsed
    }

    fun mixWords( needMix : Boolean){
        needMixWords = needMix
    }

    fun simplifyPinyin( needSimplify : Boolean){
        needSimplifyPinYin = needSimplify
    }

    fun addToNewStudyList( studyListName: String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyListUseCase
            .createStudyList(studyListName)
            .andThen(Completable.defer {
                studyListInfoUseCase
                    .getStudyList(studyListName)
                    .flatMapCompletable {
                        addSuggestWordsToStudyList.addSuggestWords(getAddConfig(it.id),addedWords)
                    }
            })
            .subscribe({
                   isSuccess.postValue(true)
            },{
                isSuccess.postValue(false)
                _error.postValue(it.message)
            })
        return isSuccess
    }

    fun addToExisting( studyListId: Long) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()

        addSuggestWordsToStudyList
            .addSuggestWords(getAddConfig(studyListId),addedWords)
            .subscribe({
                isSuccess.postValue(true)
            },{
                isSuccess.postValue(false)
                _error.postValue(it.message)
            })
        return isSuccess
    }

    private fun getAddConfig( studyListId : Long): AddWordsConfig =
        AddWordsConfig(
            studyListId,
            if (needSimplifyPinYin) AddWordsConfig.Romanization.LETTER_PINYIN else AddWordsConfig.Romanization.TONE_PINYIN,
            needMixWords
        )

}