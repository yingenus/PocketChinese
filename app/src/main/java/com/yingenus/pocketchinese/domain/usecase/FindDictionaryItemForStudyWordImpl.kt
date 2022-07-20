package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.SearchEngine
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Named

class FindDictionaryItemForStudyWordImpl @Inject constructor(
    @Named("match_search") private val searchEngine : SearchEngine
): FindDictionaryItemForStudyWord {

    override fun findDictionaryItem(word: String, language: Language): Single<DictionaryItem> {
        return searchEngine.find(word.lowercase()).filter { isSatisfact(it,word,language) }.firstOrError()
    }

    override fun canInsert( word: String, dictionaryItem: DictionaryItem, language: Language) =
        isSatisfact(dictionaryItem,word,language)

    private fun isSatisfact(dictionaryItem: DictionaryItem, word: String, language: Language): Boolean{
        return when (language){
            Language.PINYIN -> dictionaryItem.pinyin.isEqual(word)
            Language.CHINESE -> dictionaryItem.chinese.isEqual(word)
            Language.RUSSIAN -> dictionaryItem.translation.first().isEqual(word)
        }
    }

    private fun String.isEqual( word: String) =
        this.lowercase().replace(" ", "") == word.lowercase().replace(" ", "")

}