package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import java.lang.IllegalStateException

class ProxyDictionaryItemRepository() : DictionaryItemRepository {

    private var _repository : DictionaryItemRepository ? = null
    private val repository : DictionaryItemRepository
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: DictionaryItemRepository){
        if (_repository == null) _repository = repository
    }

    override fun getAllDictionaryItems(): List<DictionaryItem> = repository.getAllDictionaryItems()
    override fun findById( id : Int): DictionaryItem? = repository.findById(id)
    override fun findByChinese( chinese : String): List<DictionaryItem> = repository.findByChinese(chinese)
    override fun findByPinyin( pinyin : String): List<DictionaryItem> = repository.findByPinyin(pinyin)
    override fun findByTranslation( translation : String): List<DictionaryItem> = repository.findByTranslation(translation)
}