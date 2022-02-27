package com.yingenus.pocketchinese.domain.entities.dictionarysearch

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MatchSearchEngine(
        private val dictionaryRepository: DictionaryItemRepository
) : SearchEngine{
    override fun find(query: String): Observable<DictionaryItem> {
        return Observable.defer {
            val result = when(witchLanguage(query)){
                Language.RUSSIAN -> dictionaryRepository.findByTranslation(query)
                Language.PINYIN -> dictionaryRepository.findByPinyin(query)
                Language.CHINESE -> dictionaryRepository.findByChinese(query)
            }
            Observable.fromIterable(result)

        }
                .observeOn(Schedulers.io())
                .take(100)

    }


}