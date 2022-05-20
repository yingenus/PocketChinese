package com.yingenus.pocketchinese.presentation.views.creteeditword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyListUseCase
import com.yingenus.pocketchinese.domain.usecase.ModifyStudyWordUseCase
import com.yingenus.pocketchinese.domain.usecase.StudyListInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CreateWordForStudyListViewModel @AssistedInject constructor(
    @Assisted private val studyListId: Long,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase,
    private val modifyStudyWordUseCase: ModifyStudyWordUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create( studyListId: Long) : CreateWordForStudyListViewModel
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

    fun updateStudyList(){
        studyListInfoUseCase
            .getStudyList(studyListId)
            .switchIfEmpty(Single.error(Throwable("no such studylist id")))
            .subscribe { studyList ->
                _studyListName.postValue(studyList.name)
            }
    }

    fun onChineseTextChanged( newText : String){
        Single
            .just(newText)
            .flatMap { checkChinese(it) }
            .subscribe { error ->
                _errorChinese.postValue(error)
            }
    }

    fun onPinyinTextChanged( newText : String){
        Single
            .just(newText)
            .flatMap { checkPinyin(it) }
            .subscribe { error ->
                _errorPinyin.postValue(error)
            }
    }

    fun onTranslationTextChanged( newText : String){
        Single
            .just(newText)
            .flatMap { checkTranslation(it) }
            .subscribe { error ->
                _errorTranslation.postValue(error)
            }
    }

    fun add( chinese : String, pinyin : String, translation : String) : LiveData<AddResult> {
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
                if (it.first != WordsError.NOTHING || it.second != WordsError.NOTHING || it.third != WordsError.NOTHING){

                    Single.just<AddResult>(AddResult.NO_REQUIRE)
                }
                else
                    modifyStudyWordUseCase.createStudyWord(
                        studyListId,
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
                if (it.length> MAX_CHINESE) WordsError.TOO_LONG
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
                if (it.length> MAX_CHINESE) WordsError.TOO_LONG
                else if (it.isEmpty()) WordsError.ZERO_LENGTH
                else if (!modifyStudyWordUseCase.checkCorrect(it,Language.RUSSIAN)) WordsError.INVALID_CHARS
                else WordsError.NOTHING
            }
    }

}

class CreateWordForStudyListViewModelFragment @AssistedInject constructor(
    @Assisted private val studyListId: Long
) : ViewModelProvider.Factory{

    @AssistedFactory
    interface Builder{
        fun create(studyListId: Long): CreateWordForStudyListViewModelFragment
    }

    @Inject
    lateinit var createWordForStudyListViewModel : CreateWordForStudyListViewModel.Factory

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass){
            CreateWordForStudyListViewModel::class.java -> createWordForStudyListViewModel.create(studyListId)
            else -> throw IllegalArgumentException()
        } as T
    }
}