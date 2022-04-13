package com.yingenus.pocketchinese.presentation.views.userlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyListUseCase
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import com.yingenus.pocketchinese.domain.usecase.UserStatisticUseCase
import java.util.jar.Attributes
import javax.inject.Inject

class UserListsViewModel @Inject constructor(
    private val modifyStudyListUseCase: ModifyStudyListUseCase,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val userStatisticUseCase: UserStatisticUseCase,
    private val settings : ISettings
) : ViewModel() {

    private val _addedWords : MutableLiveData<Int> = MutableLiveData()
    val addedWords : LiveData<Int>
        get() = _addedWords

    private val _repeatedWords : MutableLiveData<Int> = MutableLiveData()
    val repeatedWords : LiveData<Int>
        get() = _repeatedWords

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

    private val _showedOtherUserList : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedOtherUserList : LiveData<List<ShowedStudyList>>
        get() = _showedOtherUserList

    private val _showedNeedRepeatUserList : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedNeedRepeatUserList : LiveData<List<ShowedStudyList>>
        get() = _showedNeedRepeatUserList


    fun updateStudyLists(){
        studyListInfoUseCase
            .getAllStudyLists()
            .subscribe { onSuccess ->
                val needRepead = onSuccess.filter { it.repeat != RepeatRecomend.DONT_NEED }
                val other = onSuccess.filter { it.repeat == RepeatRecomend.DONT_NEED }
                _showedNeedRepeatUserList.postValue(needRepead)
                _showedOtherUserList.postValue(other)
        }
    }

    fun updateStatistic(){
        userStatisticUseCase
            .getStatistic()
            .subscribe { statistic->
                _addedWords.postValue(statistic.added)
                _repeatedWords.postValue(statistic.added)
                _progressChinese.postValue(statistic.passedChn*100 / (statistic.passedChn + statistic.failedChn))
                _progressPinyin.postValue(statistic.passedPin*100 / (statistic.passedPin + statistic.failedPin))
                _progressTranslation.postValue(statistic.passedTrn*100 / (statistic.passedTrn + statistic.failedTrn))
                val repeatType = settings.getRepeatType()
                _showProgressChinese.postValue( !repeatType.ignoreCHN)
                _showProgressPinyin.postValue( !repeatType.ignorePIN)
                _showProgressTranslation.postValue( !repeatType.ignoreTRN)
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

    fun createStudyList(studyListName : String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyListUseCase.createStudyList(studyListName).subscribe {
            isSuccess.postValue(true)
            updateStudyLists()
        }
        return isSuccess
    }

    fun deleteStudyList(studyListName : String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyListUseCase.deleteStudyList(studyListName).subscribe {
            isSuccess.postValue(true)
            updateStudyLists()
        }
        return isSuccess
    }

    fun renameStudyList(studyListName : String, studyList: ShowedStudyList) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        modifyStudyListUseCase.renameStudyList(studyList,studyListName).subscribe {
            isSuccess.postValue(true)
            updateStudyLists()
        }
        return isSuccess
    }

}