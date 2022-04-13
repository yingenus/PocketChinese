package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.AddWordsConfig
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import io.reactivex.rxjava3.core.Completable

interface AddSuggestWordsToStudyList {
    fun addSuggestWords( addWordsConfig: AddWordsConfig, words : List<SuggestWord>): Completable
}