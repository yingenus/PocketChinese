package com.yingenus.pocketchinese.presentation.views.train

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.TrainingConf
import com.yingenus.pocketchinese.domain.usecase.TrainingWordsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class TrainingViewModel @AssistedInject constructor(
    @Assisted private val trainingConf: TrainingConf,
    private val trainingWordsUseCase: TrainingWordsUseCase
) : ViewModel(){

    @AssistedFactory
    interface Factory{
        fun create(trainingConf: TrainingConf) : TrainingViewModel
    }

    private val _good : MutableLiveData<Int> = MutableLiveData()
    val good : LiveData<Int>
        get() = _good

    private val _bed : MutableLiveData<Int> = MutableLiveData()
    val bed : LiveData<Int>
        get() = _bed

    private val _all : MutableLiveData<Int> = MutableLiveData()
    val all : LiveData<Int>
        get() = _all

    private val _trainingStudyWord : MutableLiveData<StudyWord> = MutableLiveData()
    val trainingStudyWord : LiveData<StudyWord>
        get() = _trainingStudyWord

    private val _finish : MutableLiveData<Boolean> = MutableLiveData(false)
    val finish : LiveData<Boolean>
        get() = _finish

    private val _start : MutableLiveData<Boolean> = MutableLiveData(true)
    val start : LiveData<Boolean>
        get() = _start

    private var canAccept = true


    private var trainedWords : MutableList<Pair<Boolean,StudyWord>> = mutableListOf()
    private var showedWord : StudyWord? = null

    private var adds : CompositeDisposable = CompositeDisposable()

    fun startTraining(){
        trainingWordsUseCase
            .init(trainingConf)
            .andThen(trainingWordsUseCase.getTrainingWords())
            .observeOn(Schedulers.single())
            .doOnSuccess { trainedWords = it.map { false to it }.toMutableList() }
            .ignoreElement()
            .observeOn(Schedulers.computation())
            .doOnComplete {
                adds.add(
                    trainingWordsUseCase.getTrainingStatistic().subscribe {
                        _all.postValue(it.all)
                        _bed.postValue(it.bed)
                        _good.postValue(it.good)
                    }
                )
            }
            .subscribe()
    }

    fun skipWord(studyWord: StudyWord){
        goNext().subscribe()
    }

    fun postAnswer( answer : String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        getShowed().flatMap {
            trainingWordsUseCase.postAnswer(answer, it)
                .toMaybe()
        }
            .switchIfEmpty(Single.error(Throwable("not inited")))
            .doOnSuccess {
                isSuccess.postValue(it)
            }
            .flatMapCompletable { if (it) goNext() else Completable.complete() }
            .subscribe()

        return isSuccess
    }

    fun showAnswer ( ) : LiveData<String>{
        val answer : MutableLiveData<String> = MutableLiveData()
        getShowed().switchIfEmpty(Single.error(Throwable("not inited")))
            .flatMap {
                trainingWordsUseCase.showAnswer(it)
            }.subscribe({
                answer.postValue(it)
            },{ error ->

            })

        return answer
    }

    private fun getShowed() : Maybe<StudyWord>{
        return Maybe.defer { Maybe.create<StudyWord> {
            if (showedWord != null) it.onSuccess(showedWord!!)
            it.onComplete()
        } }.subscribeOn(Schedulers.single())
    }

    private fun goNext() : Completable{
        return Completable.defer {
            Completable.create { it.onComplete() }
        }
            .observeOn(Schedulers.single())
            .andThen(Single.create<Boolean> {
                    require(trainedWords.isNotEmpty())
                    var position : Int = if (showedWord != null)
                        trainedWords.indexOfFirst { it.second == showedWord }
                    else
                        0

                    if ( trainedWords.lastIndex == position) position = -1

                    val before = if (position != -1 ) trainedWords.subList(0, position) else mutableListOf()
                    val after = if (position != -1) trainedWords.subList(position, trainedWords.lastIndex) else trainedWords
                    after.addAll(before)
                    val next = after.firstOrNull { !it.first }

                    it.onSuccess(next == null)
                }
            ).flatMapCompletable {
                if (it) finish()
                else Completable.complete()
            }

    }

    private fun finish() : Completable{
        return Completable
            .complete()
            .doOnComplete {
                _finish.postValue(true)
                adds.dispose()
            }
    }

}