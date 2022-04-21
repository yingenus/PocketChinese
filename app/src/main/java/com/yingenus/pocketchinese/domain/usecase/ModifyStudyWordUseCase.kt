package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.StudyWord
import io.reactivex.rxjava3.core.Completable

interface ModifyStudyWordUseCase {
    fun checkCorrect(text : String, language: Language) : Boolean
    fun createStudyWord( studyListId : Long, chinese : String, pinyin : String, translation : String): Completable
    fun changeChinese( studyWord: Long, chinese: String): Completable
    fun changePinyin( studyWord: Long, pinyin: String): Completable
    fun changeTranslation( studyWord: Long, translation: String): Completable
    fun changeAll(studyWord: Long, chinese: String, pinyin: String, translation: String): Completable
    fun deleteStudyWord( studyWord: StudyWord): Completable
    fun deleteStudyWords( studyWord: List<Long>): Completable
    fun deleteStudyWord( id: Long): Completable
}