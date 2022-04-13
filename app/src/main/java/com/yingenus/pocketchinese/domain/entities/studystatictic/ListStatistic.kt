package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.domain.dto.RepeatType
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyListStatistic
import com.yingenus.pocketchinese.domain.dto.StudyListStatisticShort
import io.reactivex.rxjava3.core.Single

interface ListStatistic {
    fun setRepeatType( repeatType: RepeatType)
    fun getStatistic(studyList: StudyList): Single<StudyListStatistic>
    fun getShortStatistic(studyList: StudyList): Single<StudyListStatisticShort>
}