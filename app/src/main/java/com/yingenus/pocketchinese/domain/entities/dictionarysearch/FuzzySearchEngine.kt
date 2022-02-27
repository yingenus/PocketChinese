package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.BruteUnitWordsRepository
import com.yingenus.pocketchinese.data.local.NgramM3AllAccessRep
import com.yingenus.pocketchinese.data.local.hot.NgramM3HotRepo
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram.NgramM3Search
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.search.NgramM3Repository
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import main.newsearch.NGramSearch.NgramM1Search
import main.newsearch.dto.VariantWord

class FuzzySearchEngine(
        private val dictionaryItemRepository: DictionaryItemRepository,
        rusNgramRepository: NgramM3Repository<Int>,
        pinNgramRepository: NgramM3Repository<Int>,
        chnN1gramRepository: NgramRepository<VariantWord>,
        chnN2gramRepository: NgramRepository<VariantWord>,
        rusUnitWordRepository: UnitWordRepository,
        pinUnitWordRepository: UnitWordRepository
        ) : SearchEngine {

    private val rusSearcher : Searcher
    private val pinSearcher : Searcher
    private val chnSearcher : Searcher

    private companion object{
        val lengthPinThreshold = listOf( 0 to 0 , 3 to 1, 4 to 2, 6 to 3 , 8 to 4 )
        val lengthRusThreshold = listOf( 0 to 0 , 4 to 1, 6 to 2 , 10 to 3 )
    }

    init{

        val rusUnitWordRepository = BruteUnitWordsRepository(rusUnitWordRepository)
        val rusNgramM3Search = NgramM3Search<Int>( rusNgramRepository, 3, object : NgramM3Search.Params{
            override fun getThreshold(query: String): Int {
                var threshold : Int = 0
                for (i in lengthRusThreshold){
                    if ( query.length >= i.first){
                        threshold = i.second
                    }
                }

                return threshold
            }
        })

        rusSearcher = NComSearch(rusUnitWordRepository,rusNgramM3Search, LevenshteinComparator(lengthRusThreshold.toSet()))

        val pinUnitWordRepository = BruteUnitWordsRepository(pinUnitWordRepository)
        val pinNgramM3Search = NgramM3Search<Int>( pinNgramRepository, 3, object : NgramM3Search.Params{
            override fun getThreshold(query: String): Int {
                var threshold : Int = 0
                for (i in lengthPinThreshold){
                    if ( query.length >= i.first){
                        threshold = i.second
                    }
                }

                return threshold
            }
        })

        pinSearcher = NComSearch(pinUnitWordRepository,pinNgramM3Search, LevenshteinComparator(lengthPinThreshold.toSet()))

        chnSearcher = TwoNgramsSearch(NgramM1Search(chnN1gramRepository,1),NgramM1Search(chnN2gramRepository,2))

    }


    override fun find(query: String): Observable<DictionaryItem> {

        val language = witchLanguage(query)
        val preparedQuery = when(language){
            Language.CHINESE -> query.replace(" ","")
            Language.PINYIN -> query.replace(" ","")
            Language.RUSSIAN -> query.trim()
        }

        //.observeOn(Schedulers.io())
        return Observable.defer {
            when(val result = getSearcher(language).find(preparedQuery)){
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