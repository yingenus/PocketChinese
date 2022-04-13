package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.StudyWordStatistic
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface WordInfoUseCase {
    fun getStudyWord( id : Long): Maybe<StudyWord>
    fun getShowedStudyWords( studyListId: Long) : Single<List<ShowedStudyWord>>
    fun getShowedStudyWord(id : Long): Maybe<ShowedStudyWord>
    fun getStudyWordStatistic(id : Long): Maybe<StudyWordStatistic>
}