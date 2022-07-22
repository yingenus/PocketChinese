package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.StudyListStatistic
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface StudyListInfoUseCase {
    fun getAllStudyLists(): Single<List<ShowedStudyList>>
    fun getStudyList( name : String): Maybe<ShowedStudyList>
    fun getStudyList( id: Long): Maybe<ShowedStudyList>
    fun getStudyListOfWord( studyWordId : Long): Maybe<ShowedStudyList>
    fun getStudyListStatistic( name: String): Maybe<StudyListStatistic>
    fun getStudyListStatistic( id: Long): Maybe<StudyListStatistic>
}