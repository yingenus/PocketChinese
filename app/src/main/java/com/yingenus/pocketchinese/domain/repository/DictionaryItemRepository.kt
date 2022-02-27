package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.DictionaryItem

interface DictionaryItemRepository {
    fun getAllDictionaryItems(): List<DictionaryItem>
    fun findById( id : Int): DictionaryItem?
    fun findByChinese( chinese : String): List<DictionaryItem>
    fun findByPinyin( pinyin : String): List<DictionaryItem>
    fun findByTranslation( translation : String): List<DictionaryItem>
}