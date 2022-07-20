package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import io.reactivex.rxjava3.core.Single

interface FindDictionaryItemForStudyWord {
    fun findDictionaryItem( word : String, language : Language): Single<DictionaryItem>
    fun canInsert(word: String, dictionaryItem: DictionaryItem, language: Language) : Boolean
}