package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyWord
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.*

interface StudyRepository {
    fun getAllStudyList(): Single<List<StudyList>>
    fun getStudyListById(id: Long): Maybe<StudyList>
    fun getStudyListByName(name: String): Maybe<StudyList>

    fun createStudyList(studyList: StudyList) : Completable
    fun updateStudyList(studyList: StudyList) : Completable
    fun deleteStudyList(studyList: StudyList) : Completable

    fun countWordsForList(id: Long): Single<Int>

    fun getStudyWordsByListId(id: Long): Single<List<StudyWord>>
    fun getStudyWordsByListName(name: String): Single<List<StudyWord>>
    fun getStudyWord(id: Long): Maybe<StudyWord>

    fun addStudyWord(studyList: StudyList ,studyWord: StudyWord) : Completable
    fun addStudyWordWithID(studyList: StudyList ,studyWord: StudyWord) : Single<Long>
    fun updateStudyWord(studyWord: StudyWord) : Completable
    fun deleteStudyWord(studyWord: StudyWord) : Completable

    fun addStudyWords(studyList: StudyList ,studyWords: List<StudyWord>) : Completable
    fun addStudyWordsWithID(studyList: StudyList ,studyWords: List<StudyWord>) : Single<List<Long>>
    fun deleteStudyWords(studyWords: List<StudyWord>) : Completable
    fun deleteStudyWordsByIds(studyWords: List<Long>) : Completable
}