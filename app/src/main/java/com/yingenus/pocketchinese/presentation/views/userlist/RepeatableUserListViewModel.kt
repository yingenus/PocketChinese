package com.yingenus.pocketchinese.presentation.views.userlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.domain.dto.RepeatRecomend
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import javax.inject.Inject

class RepeatableUserListViewModel @Inject constructor(
    private val studyListInfoUseCase: StudyListInfoUseCase
): ViewModel(){

    private val _showedNeedRepeatUserList : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedNeedRepeatUserList : LiveData<List<ShowedStudyList>>
        get() = _showedNeedRepeatUserList

    fun updateStudyLists(){
        studyListInfoUseCase
            .getAllStudyLists()
            .subscribe { onSuccess ->
                val needRepead = onSuccess.filter { it.repeat != RepeatRecomend.DONT_NEED }.toMutableList()
                needRepead.sortBy { it.createDate.time }
                _showedNeedRepeatUserList.postValue(needRepead)
            }
    }
}