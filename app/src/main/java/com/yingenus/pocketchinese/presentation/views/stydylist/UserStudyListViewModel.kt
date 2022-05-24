package com.yingenus.pocketchinese.presentation.views.stydylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.entities.toTimeOrDate
import com.yingenus.pocketchinese.domain.usecase.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class UserStudyListViewModel @AssistedInject constructor(
    @Assisted private val studyListId : Long,
    private val wordInfoUseCase: WordInfoUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase,
    private val modifyStudyWordUseCase: ModifyStudyWordUseCase,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val settings : ISettings
): ViewModel(){

    @AssistedFactory
    interface Builder{
        fun create( studyListId: Long) : UserStudyListViewModel
    }

    private val _studyListName : MutableLiveData<String> = MutableLiveData()
    val studyListName : LiveData<String>
        get() = _studyListName

    private val _notifyUsers : MutableLiveData<Boolean> = MutableLiveData()
    val notifyUsers : LiveData<Boolean>
        get() = _notifyUsers

    private val _addedWords : MutableLiveData<Int> = MutableLiveData()
    val addedWords : LiveData<Int>
        get() = _addedWords

    private val _repeatedWords : MutableLiveData<Int> = MutableLiveData()
    val repeatedWords : LiveData<Int>
        get() = _repeatedWords

    private val _lastRepeat : MutableLiveData<String> = MutableLiveData()
    val lastRepeat : LiveData<String>
        get() = _lastRepeat

    private val _nextRepeat : MutableLiveData<String> = MutableLiveData()
    val nextRepeat : LiveData<String>
        get() = _nextRepeat

    private val _repeatRecomend : MutableLiveData<RepeatRecomend> = MutableLiveData()
    val repeatRecomend : LiveData<RepeatRecomend>
        get() = _repeatRecomend

    private val _progressChinese : MutableLiveData<Int> = MutableLiveData()
    val progressChinese : LiveData<Int>
        get() = _progressChinese

    private val _progressPinyin : MutableLiveData<Int> = MutableLiveData()
    val progressPinyin : LiveData<Int>
        get() = _progressPinyin

    private val _progressTranslation : MutableLiveData<Int> = MutableLiveData()
    val progressTranslation : LiveData<Int>
        get() = _progressTranslation

    private val _showProgressChinese : MutableLiveData<Boolean> = MutableLiveData()
    val showProgressChinese : LiveData<Boolean>
        get() = _showProgressChinese

    private val _showProgressPinyin : MutableLiveData<Boolean> = MutableLiveData()
    val showProgressPinyin : LiveData<Boolean>
        get() = _showProgressPinyin

    private val _showProgressTranslation : MutableLiveData<Boolean> = MutableLiveData()
    val showProgressTranslation : LiveData<Boolean>
        get() = _showProgressTranslation

    private val _repeadRecomedStudyWords : MutableLiveData<List<ShowedStudyWord>> = MutableLiveData()
    val repeadRecomedStudyWords : LiveData<List<ShowedStudyWord>>
        get() = _repeadRecomedStudyWords

    private val _otherRecomedStudyWords : MutableLiveData<List<ShowedStudyWord>> = MutableLiveData()
    val otherRecomedStudyWords : LiveData<List<ShowedStudyWord>>
        get() = _otherRecomedStudyWords

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error

    fun updateStudyListInfo(){
        studyListInfoUseCase
            .getStudyListStatistic(studyListId)
            .flatMap { statistic ->
                studyListInfoUseCase
                    .getStudyList(studyListId)
                    . map { it to statistic } }
            .switchIfEmpty( Single.error(Throwable("cant find study list")) )
            .subscribe({ info ->
                _studyListName.postValue(info.first.name)
                _notifyUsers.postValue(info.first.notifyUser)
                _addedWords.postValue(info.second.addedWords)
                _repeatedWords.postValue(info.second.repeatedWords)
                _lastRepeat.postValue(info.second.lastRepeat?.toTimeOrDate()?:"--")
                _nextRepeat.postValue(info.second.nextRepeat?.toTimeOrDate()?:"--")
                _repeatRecomend.postValue(info.second.repeat)
                _progressChinese.postValue(info.second.successChn)
                _progressPinyin.postValue(info.second.successPin)
                _progressTranslation.postValue(info.second.successTrn)
                val repeatType = settings.getRepeatType()
                _showProgressChinese.postValue( !repeatType.ignoreCHN)
                _showProgressPinyin.postValue( !repeatType.ignorePIN)
                _showProgressTranslation.postValue( !repeatType.ignoreTRN)
            },{ error ->
                _error.postValue(error.message)
            })
    }

    fun updateStudyWords(){
        wordInfoUseCase
            .getShowedStudyWords(studyListId)
            .subscribe({ words ->
                val repeatRecomend = words.filter { it.recomend != RepeatRecomend.DONT_NEED }.toMutableList()
                val other = words.filter { it.recomend == RepeatRecomend.DONT_NEED }.toMutableList()

                repeatRecomend.sortBy { it.createDate.time }
                other.sortBy { it.createDate.time }

                _repeadRecomedStudyWords.postValue(repeatRecomend)
                _otherRecomedStudyWords.postValue(other)
            },{ error ->
                _error.postValue(error.message)
            })
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

    fun renameStudyList(studyListName : String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        studyListInfoUseCase
            .getStudyList(studyListId)
            .switchIfEmpty( Single.error(Throwable("cant find study list")) )
            .flatMapCompletable { modifyStudyListUseCase.renameStudyList(it,studyListName) }
            .subscribe {
                isSuccess.postValue(true)
                updateStudyListInfo()
            }
        return isSuccess
    }

    fun setNotify( notify : Boolean) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        studyListInfoUseCase
            .getStudyList(studyListId)
            .switchIfEmpty( Single.error(Throwable("cant find study list")) )
            .flatMapCompletable { modifyStudyListUseCase.notifyUsers(it,notify) }
            .subscribe {
                isSuccess.postValue(true)
            }
        updateStudyListInfo()
        return isSuccess
    }

    fun deleteStudyWord( studyWord: ShowedStudyWord) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyWordUseCase
            .deleteStudyWord(studyWord.id)
            .subscribe {
                isSuccess.postValue(true)
                updateStudyListInfo()
                updateStudyWords()
            }
        return isSuccess
    }

    fun deleteStudyWordS( studyWords: List<ShowedStudyWord>) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyWordUseCase
            .deleteStudyWords(studyWords.map { it.id })
            .subscribe {
                isSuccess.postValue(true)
                updateStudyListInfo()
                updateStudyWords()
            }
        return isSuccess
    }

}