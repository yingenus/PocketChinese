package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.BruteUnitWordsRepository
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramM3Search
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.search.NgramM3Repository
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import io.reactivex.rxjava3.core.Observable
import main.newsearch.NGramSearch.NgramM1Search
import com.yingenus.pocketchinese.domain.dto.VariantWord
import javax.inject.Inject
import javax.inject.Named

class FuzzySearchEngine @Inject constructor(
    @Named("chinese_searcher") val chnSearcher : Searcher,
    @Named("pinyin_searcher") val pinSearcher : Searcher,
    @Named("russian_searcher") val rusSearcher : Searcher,
    val dictionaryItemRepository: DictionaryItemRepository
        ) : SearchEngine {

    override fun find(query: String): Observable<DictionaryItem> {

        val language = witchLanguage(query)
        val preparedQuery = when(language){
            Language.CHINESE -> query.replace(" ","")
            Language.PINYIN -> query.replace(" ","")
            Language.RUSSIAN -> query.trim()
        }

        //.observeOn(Schedulers.io())
        return Observable.defer {
            val result = getSearcher(language).find(preparedQuery)
            when(result){
                is Result.Failure -> Observable.error<Int>( Throwable(result.msg,null))
                is Result.Success -> Observable.fromIterable(result.value)
                else -> Observable.empty<Int>()
            }
        }

                .take(100)
                .map { id ->
                    dictionaryItemRepository.findById(id)
                }
    }

    private fun getSearcher(language: Language) = when(language){
        Language.CHINESE -> chnSearcher
        Language.PINYIN -> pinSearcher
        Language.RUSSIAN -> rusSearcher
    }
}