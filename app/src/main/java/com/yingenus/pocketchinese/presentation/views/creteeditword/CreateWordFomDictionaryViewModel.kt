package com.yingenus.pocketchinese.presentation.views.creteeditword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

class CreateWordFomDictionaryViewModel @AssistedInject constructor(
    @Assisted private val dictionaryItem: DictionaryItem?,
    private val studyListInfoUseCase: StudyListInfoUseCase,
    private val modifyStudyListUseCase: ModifyStudyListUseCase,
    private val modifyStudyWordUseCase: ModifyStudyWordUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory{
        fun create( dictionaryItem: DictionaryItem?) : CreateWordFomDictionaryViewModel
    }

    enum class WordsError{
        ZERO_LENGTH, TOO_LONG, INVALID_CHARS, NOTHING
    }

    companion object{
        const val MAX_PINYIN = 28
        const val MAX_CHINESE = 10
        const val MAX_TRANSLATION = 28
    }

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

    private val _showedUserLists : MutableLiveData<List<ShowedStudyList>> = MutableLiveData()
    val showedUserLists : LiveData<List<ShowedStudyList>>
        get() = _showedUserLists

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error

    init {
        if(dictionaryItem != null){
            _chinese.postValue(dictionaryItem.chinese)
            _pinyin.postValue(dictionaryItem.pinyin)
            _translation.postValue(dictionaryItem.translation.first())
        }
    }

    fun onChineseTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess {
                _chinese.postValue(newText)
            }
            .flatMap { checkChinese(it) }
            .subscribe { error ->
                _errorChinese.postValue(error)
            }
    }

    fun onPinyinTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess {
                _pinyin.postValue(newText)
            }
            .flatMap { checkPinyin(it) }
            .subscribe { error ->
                _errorPinyin.postValue(error)
            }
    }

    fun onTranslationTextChanged( newText : String){
        Single
            .just(newText)
            .doOnSuccess {
                _translation.postValue(newText)
            }
            .flatMap { checkTranslation(it) }
            .subscribe { error ->
                _errorTranslation.postValue(error)
            }
    }

    fun updateStudyLists(){
        studyListInfoUseCase
            .getAllStudyLists()
            .subscribe { onSuccess ->
                _showedUserLists.postValue(onSuccess)
            }
    }

    fun addToNewStudyList( studyListName: String, chinese : String, pinyin : String, translation : String) : LiveData<Boolean>{
        val isSuccess : MutableLiveData<Boolean> = MutableLiveData()
        Single
            .zip(
            checkChinese(chinese),
            checkPinyin(pinyin),
            checkTranslation(translation))
            { chn, pin, trn ->
                Triple(chn,pin,trn)
            }
            .flatMap<Boolean> {
                if (it.first != WordsError.NOTHING || it.second != WordsError.NOTHING || it.third != WordsError.NOTHING)
                    Single.just<Boolean>(false)
                else
                    modifyStudyListUseCase
                        .containsName(studyListName)
                        .flatMap {
                            if (it) {
                                _error.postValue("name exist")
                                Single.just<Boolean>(false)
                            } else {
                                modifyStudyListUseCase
                                    .createStudyList(studyListName)
                                    .toSingle{
                                        studyListInfoUseCase
                                            .getStudyList(studyListName)
                                    }
                                    .flatMapMaybe { it }
                                    .flatMapCompletable {
                                        modifyStudyWordUseCase.createStudyWord(it.id, chinese, pinyin, translation)
                                    }.toSingle<Boolean>{
                                        true
                                    }
                            }
                        }
            }
            .subscribe { success ->
                isSuccess.postValue(success)
            }

        return isSuccess
    }

    fun addToExisting( studyList: ShowedStudyList, chinese : String, pinyin : String, translation : String) : LiveData<Boolean> {
        val isSuccess: MutableLiveData<Boolean> = MutableLiveData()

        Single
            .zip(
                checkChinese(chinese),
                checkPinyin(pinyin),
                checkTranslation(translation)
            )
            { chn, pin, trn ->
                Triple(chn, pin, trn)
            }
            .flatMap<Boolean> {
                if (it.first != WordsError.NOTHING || it.second != WordsError.NOTHING || it.third != WordsError.NOTHING)
                    Single.just<Boolean>(false)
                else
                    modifyStudyWordUseCase.createStudyWord(
                        studyList.id,
                        chinese,
                        pinyin,
                        translation
                    ).toSingle<Boolean> {
                        true
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
                else if (modifyStudyWordUseCase.checkCorrect(it,Language.CHINESE)) WordsError.INVALID_CHARS
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
                else if (modifyStudyWordUseCase.checkCorrect(it,Language.PINYIN)) WordsError.INVALID_CHARS
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
                else if (modifyStudyWordUseCase.checkCorrect(it,Language.RUSSIAN)) WordsError.INVALID_CHARS
                else WordsError.NOTHING
            }
    }

}