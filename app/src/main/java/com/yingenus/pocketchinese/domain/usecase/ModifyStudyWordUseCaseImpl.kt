package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.entities.namestandards.StudyWordsStandards
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class ModifyStudyWordUseCaseImpl @Inject constructor(
    private val studyRepository: StudyRepository,
    private val studyWordsStandards: StudyWordsStandards
): ModifyStudyWordUseCase{
    override fun checkCorrect(text: String, language: Language): Boolean {
        return studyWordsStandards.isCorrectField(text,language)
    }

    override fun createStudyWord(
        studyListId: Long,
        chinese: String,
        pinyin: String,
        translation: String
    ): Completable {
        return studyRepository.getStudyListById(studyListId)
            .switchIfEmpty( Single.error(Throwable("no such study list")))
            .flatMapCompletable {
                if ( checkCorrect(chinese,Language.CHINESE)
                    && checkCorrect(pinyin, Language.PINYIN)
                    && checkCorrect(translation,Language.RUSSIAN)){
                    studyRepository.addStudyWord(it,
                        StudyWord(0, chinese,pinyin,translation, Date(System.currentTimeMillis()))
                    )
                }else{
                    Completable.error(Throwable("chinese or pinyin or translation is out from standards"))
                }
            }
    }

    override fun changeChinese(studyWord: Long, chinese: String): Completable {
        return studyRepository.getStudyWord(studyWord)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable {
                if ( checkCorrect(chinese,Language.CHINESE)){
                    it.chinese = chinese
                    studyRepository.updateStudyWord(it)
                }else{
                    Completable.error(Throwable("chineseis out from standards"))
                }
            }
    }

    override fun changePinyin(studyWord : Long, pinyin: String): Completable {
        return studyRepository.getStudyWord(studyWord)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable {
                if ( checkCorrect(pinyin,Language.PINYIN)){
                    it.pinyin = pinyin
                    studyRepository.updateStudyWord(it)
                }else{
                    Completable.error(Throwable("pinyin is out from standards"))
                }
            }
    }

    override fun changeTranslation(studyWord: Long, translation: String): Completable {
        return studyRepository.getStudyWord(studyWord)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable {
                if ( checkCorrect(translation,Language.RUSSIAN)){
                    it.translate = translation
                    studyRepository.updateStudyWord(it)
                }else{
                    Completable.error(Throwable("translation is out from standards"))
                }
            }
    }

    override fun changeAll(
        studyWord: Long,
        chinese: String,
        pinyin: String,
        translation: String
    ): Completable {
        return studyRepository.getStudyWord(studyWord)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable {
                if ( checkCorrect(translation,Language.RUSSIAN) && checkCorrect(pinyin,Language.PINYIN) && checkCorrect(chinese,Language.CHINESE)){
                    it.chinese = chinese
                    it.pinyin = pinyin
                    it.translate = translation
                    studyRepository.updateStudyWord(it)
                }else{
                    Completable.error(Throwable("translation is out from standards"))
                }
            }
    }

    override fun deleteStudyWords(ids: List<Long>): Completable {
        return studyRepository.deleteStudyWordsByIds(ids)
    }


    override fun deleteStudyWord(studyWord: StudyWord): Completable {
        return studyRepository.getStudyWord(studyWord.id)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable { studyRepository.deleteStudyWord(it) }
    }

    override fun deleteStudyWord(id: Long): Completable {
        return studyRepository.getStudyWord(id)
            .switchIfEmpty( Single.error(Throwable("no such study word")))
            .flatMapCompletable { studyRepository.deleteStudyWord(it) }
    }
}