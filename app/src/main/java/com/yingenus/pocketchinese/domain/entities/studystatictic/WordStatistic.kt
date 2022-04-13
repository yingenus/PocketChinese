package com.yingenus.pocketchinese.domain.entities.studystatictic

import com.yingenus.pocketchinese.domain.dto.RepeatType
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.StudyWordStatistic
import com.yingenus.pocketchinese.domain.dto.StudyWordStatisticByLanguage
import io.reactivex.rxjava3.core.Single

interface WordStatistic {
    fun setRepeatType(repeatType: RepeatType)
    fun getStatistic( word : StudyWord): Single<StudyWordStatistic>
    fun getStatisticByLanguage( word : StudyWord): Single<StudyWordStatisticByLanguage>
}