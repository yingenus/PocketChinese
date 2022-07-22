package com.yingenus.pocketchinese.presentation.views.moveword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyListUseCase
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyWordUseCase
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import com.yingenus.pocketchinese.domain.usecase.WordInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.IllegalArgumentException
import javax.inject.Inject

class MoveWordViewModelFactory @AssistedInject constructor(
    @Assisted private val studyWordId: Long
): ViewModelProvider.Factory{

    @AssistedFactory
    interface Factory{
        fun create(studyWordId: Long): MoveWordViewModelFactory
    }

    @Inject
    lateinit var createMoveWordViewModel : MoveWordViewModel.Factory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            MoveWordViewModel::class.java -> createMoveWordViewModel.create(studyWordId)
            else -> throw IllegalArgumentException()
        } as T
    }
}

class MoveWordViewModel @AssistedInject constructor(
    @Assisted private val studyWordId : Long,
    private val studyWordInfoUseCase: WordInfoUseCase,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val modifyStudyWordUseCase: ModifyStudyWordUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase
): ViewModel(){

    @AssistedFactory
    interface Factory{
        fun create(studyWordId: Long): MoveWordViewModel
    }

    enum class AddResult{
        ADDED, // when word moved successfully
        NO_REQUIRE, // when some of the requirement not satisfy
        ERROR // when critic error occurred
    }

    private val _showedUserLists : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedUserLists : LiveData<List<ShowedStudyList>>
        get() = _showedUserLists

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error


    fun updateView(){
        Single.zip(
                studyListInfoUseCase.getAllStudyLists(),
                studyListInfoUseCase.getStudyListOfWord(studyWordId).switchIfEmpty(Single.error(NoSuchElementException()))
            ){ list, studyList ->
                list.toMutableList().apply {
                    val excessList = first { it.id == studyList.id }
                    remove(excessList)
                }
            }
            .subscribe({onSuccess ->
                _showedUserLists.postValue(onSuccess)
            }, {onError ->
                _error.postValue(onError.message)
            })
    }

    fun moveToExistingStudyList( studyListId: Long, clearStatistic : Boolean ): LiveData<AddResult>{

        val addResult : MutableLiveData<AddResult> = MutableLiveData()

        studyListInfoUseCase
            .getStudyList(studyListId)
            .subscribeOn(Schedulers.io())
            .switchIfEmpty(Single.error(NoSuchElementException("studyList with id $studyListId not found")))
            .flatMapCompletable {
                modifyStudyWordUseCase
                    .moveStudyWord(studyWordId, it.id,clearStatistic)
                    .subscribeOn(Schedulers.io())
            }
            .subscribe({
                addResult.postValue(AddResult.ADDED)
            },{onError ->
                if (onError is NoSuchElementException)
                    addResult.postValue(AddResult.NO_REQUIRE)
                else
                    addResult.postValue(AddResult.ERROR)
            })

        return addResult
    }

    fun moveToNewStudyList( studyListName : String, clearStatistic : Boolean ): LiveData<AddResult>{

        val addResult : MutableLiveData<AddResult> = MutableLiveData()

        val createAndAddCompletable = modifyStudyListUseCase
            .createStudyList(studyListName)
            .subscribeOn(Schedulers.io())
            .andThen(
                studyListInfoUseCase
                    .getStudyList(studyListName)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable {
                        modifyStudyWordUseCase
                            .moveStudyWord(studyWordId,it.id,clearStatistic)
                            .subscribeOn(Schedulers.io())
                    }
            )

        modifyStudyListUseCase
            .containsName(studyListName)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                if (!it) Completable.complete()
                else Completable.error(IllegalArgumentException("studyList with name - $studyListName exist"))
            }
            .andThen(createAndAddCompletable)
            .subscribe({
                addResult.postValue(AddResult.ADDED)
            },{onError ->
                if (onError is NoSuchElementException)
                    addResult.postValue(AddResult.NO_REQUIRE)
                else
                    addResult.postValue(AddResult.ERROR)
            })

        return addResult
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

}