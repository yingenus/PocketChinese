package com.yingenus.pocketchinese.presentation.views.creteeditword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.usecase.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class EditWordViewModelFactory @AssistedInject constructor(
    @Assisted("studyList_id") private val studyListId: Long,
    @Assisted("studyWord_id") private val studyWordId: Long
) : ViewModelProvider.Factory{

    @AssistedFactory
    interface Factory{
        fun create( @Assisted("studyList_id") studyListId: Long, @Assisted("studyWord_id") studyWordId: Long) : EditWordViewModelFactory
    }

    @Inject
    lateinit var editWordViewModel: EditWordViewModel.Factory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            EditWordViewModel::class.java -> editWordViewModel.create(studyListId,studyWordId)
            else -> throw IllegalArgumentException()
        } as T
    }
}

class EditWordViewModel @AssistedInject constructor(
    @Assisted("studyList_id") private val studyListId: Long,
    @Assisted("studyWord_id") private val studyWordId: Long,
    private val studyWordInfoUseCase: WordInfoUseCase,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase,
    private val modifyStudyWordUseCase: ModifyStudyWordUseCase,
    private val findDictionaryItemForStudyWord: FindDictionaryItemForStudyWord
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create( @Assisted("studyList_id") studyListId: Long,@Assisted("studyWord_id") studyWordId: Long) : EditWordViewModel
    }

    enum class WordsError{
        ZERO_LENGTH, TOO_LONG, INVALID_CHARS, NOTHING
    }

    enum class AddResult{
        ADDED, NO_REQUIRE, ERROR
    }

    companion object{
        const val MAX_PINYIN = 28
        const val MAX_CHINESE = 10
        const val MAX_TRANSLATION = 28
    }

    private val _studyListName : MutableLiveData<String> = MutableLiveData()
    val studyListName : LiveData<String>
        get() = _studyListName

    private val _chinese : MutableLiveData<String> = MutableLiveData()
    val chinese : LiveData<String>
        get() = _chinese

    private val _pinyin : MutableLiveData<String> = MutableLiveData()
    val pinyin : LiveData<String>
        get() = _pinyin

    private val _translation : MutableLiveData<String> = MutableLiveData()
    val translation : LiveData<String>
        get() = _translation

    private val _errorChinese : MutableLiveData<WordsError> = MutableLiveData(WordsError.NOTHING)
    val errorChinese : LiveData<WordsError>
        get() = _errorChinese

    private val _errorPinyin : MutableLiveData<WordsError> = MutableLiveData(WordsError.NOTHING)
    val errorPinyin : LiveData<WordsError>
        get() = _errorPinyin

    private val _errorTranslation : MutableLiveData<WordsError> = MutableLiveData(WordsError.NOTHING)
    val errorTranslation : LiveData<WordsError>
        get() = _errorTranslation

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error

    private val chinesePublish : PublishSubject<String> = PublishSubject.create()
    private val disposables : CompositeDisposable = CompositeDisposable()

    init {
        val disposable = chinesePublish
            .observeOn(Schedulers.computation())
            .debounce(500, TimeUnit.MILLISECONDS)
            .flatMapSingle { chinese ->
                checkChinese(chinese)
                    .map { error -> chinese to error }
            }
            .filter { it.second == WordsError.NOTHING }
            .flatMapSingle {
                findDictionaryItemForStudyWord.findDictionaryItem(it.first, Language.CHINESE)
                    .subscribeOn(Schedulers.io())
            }
            .retry()
            .withLatestFrom( chinesePublish){ dicItem : DictionaryItem, chinese : String ->
                dicItem to findDictionaryItemForStudyWord.canInsert(chinese,dicItem, Language.CHINESE)
            }
            .filter { it.second }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({onNext ->
                tryInsertSuggestWord(onNext.first, Language.CHINESE)
            }, {onError ->
                //to do nothing
            })

        disposables.add(disposable)
    }

    fun updateView(){
        studyListInfoUseCase
            .getStudyList(studyListId)
            .switchIfEmpty(Single.error(Throwable("no such studylist id")))
            .subscribe { studyList ->
                _studyListName.postValue(studyList.name)
            }

        studyWordInfoUseCase
            .getShowedStudyWord(studyWordId)
            .switchIfEmpty(Single.error(Throwable("no such studyword id")))
            .subscribe { studyList ->
                _chinese.postValue(studyList.chinese)
                _pinyin.postValue(studyList.pinyin)
                _translation.postValue(studyList.translate)
            }
    }

    fun onChineseTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess { _chinese.postValue(newText) }
            .flatMap { checkChinese(it) }
            .subscribe { error ->
                _errorChinese.postValue(error)
            }
        //chinesePublish.onNext(newText) // autofill do not working for edit view
    }

    fun onPinyinTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess { _pinyin.postValue(newText) }
            .flatMap { checkPinyin(it) }
            .subscribe { error ->
                _errorPinyin.postValue(error)
            }
    }

    fun onTranslationTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess { _translation.postValue(newText) }
            .flatMap { checkTranslation(it) }
            .subscribe { error ->
                _errorTranslation.postValue(error)
            }
    }

    fun update( chinese : String, pinyin : String, translation : String) : LiveData<AddResult> {
        val isSuccess: MutableLiveData<AddResult> = MutableLiveData()

        Single
            .zip(
                checkChinese(chinese).doOnSuccess {
                    _errorChinese.postValue(it)
                },
                checkPinyin(pinyin).doOnSuccess {
                    _errorPinyin.postValue(it)
                },
                checkTranslation(translation).doOnSuccess {
                    _errorTranslation.postValue(it)
                }
            )
            { chn, pin, trn ->
                Triple(chn, pin, trn)
            }
            .flatMap<AddResult> {
                if (it.first != WordsError.NOTHING || it.second != WordsError.NOTHING || it.third != WordsError.NOTHING)
                    Single.just<AddResult>(AddResult.NO_REQUIRE)
                else
                    modifyStudyWordUseCase.changeAll(
                        studyWordId,
                        chinese,
                        pinyin,
                        translation
                    ).toSingle<AddResult> {
                        AddResult.ADDED
                    }
            }
            .subscribe { success ->
                isSuccess.postValue(success)
            }

        return isSuccess
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    private fun tryInsertSuggestWord( dictionaryItem: DictionaryItem, language: Language){
        val word = when( language){
            Language.CHINESE -> chinese.value.orEmpty()
            Language.PINYIN -> pinyin.value.orEmpty()
            Language.RUSSIAN -> translation.value.orEmpty()
        }

        if (findDictionaryItemForStudyWord.canInsert(word,dictionaryItem,language)){
            _chinese.postValue(dictionaryItem.chinese)
            _pinyin.postValue(dictionaryItem.pinyin)
            _translation.postValue(dictionaryItem.translation.first())
        }

    }

    private fun checkChinese(chinese : String): Single<WordsError>{
        return Single
            .just(chinese)
            .observeOn(Schedulers.computation())
            .map {
                if (it.length> MAX_CHINESE) WordsError.TOO_LONG
                else if (it.isEmpty()) WordsError.ZERO_LENGTH
                else if (!modifyStudyWordUseCase.checkCorrect(it,Language.CHINESE)) WordsError.INVALID_CHARS
                else WordsError.NOTHING
            }

    }

    private fun checkPinyin( pinyin : String): Single<WordsError>{
        return Single
            .just(pinyin)
            .observeOn(Schedulers.computation())
            .map {
                if (it.length> MAX_PINYIN) WordsError.TOO_LONG
                else if (it.isEmpty()) WordsError.ZERO_LENGTH
                else if (!modifyStudyWordUseCase.checkCorrect(it,Language.PINYIN)) WordsError.INVALID_CHARS
                else WordsError.NOTHING
            }
    }

    private fun checkTranslation( translation : String): Single<WordsError>{
        return Single
            .just(translation)
            .observeOn(Schedulers.computation())
            .map {
                if (it.length> MAX_TRANSLATION) WordsError.TOO_LONG
                else if (it.isEmpty()) WordsError.ZERO_LENGTH
                else if (!modifyStudyWordUseCase.checkCorrect(it,Language.RUSSIAN)) WordsError.INVALID_CHARS
                else WordsError.NOTHING
            }
    }

}