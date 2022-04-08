package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.dao.KeyDaoImpl
import com.yingenus.pocketchinese.data.local.sqlite.dao.RadicalsDaoImpl
import com.yingenus.pocketchinese.data.local.sqlite.dao.VariantsDaoImpl
import com.yingenus.pocketchinese.data.local.sqlite.dao.WordDaoImpl
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.domain.dto.Tone
import com.yingenus.pocketchinese.domain.dto.ZiChar
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import javax.inject.Inject

class SqliteWordRepository @Inject constructor(dictionaryHelper: DictionaryDBHelper): DictionaryItemRepository, RadicalsRepository, ToneRepository {

    private val wordDaoImpl : WordDaoImpl
    private val variantsDao : VariantsDaoImpl
    private val radicalsDao : RadicalsDaoImpl
    private val keyDao : KeyDaoImpl

        init {
            wordDaoImpl = WordDaoImpl(dictionaryHelper.connectionSource)
            variantsDao = VariantsDaoImpl(dictionaryHelper.connectionSource)
            radicalsDao = RadicalsDaoImpl(dictionaryHelper.connectionSource)
            keyDao = KeyDaoImpl(dictionaryHelper.connectionSource)
        }



    override fun getAllDictionaryItems(): List<DictionaryItem> {
        return  wordDaoImpl.getAll().map { it.toChinChar() }
    }

    override fun getAllTone(): List<Tone> {
        return variantsDao.getAll().map { it.toTone() }
    }

    override fun findById(id: Int): DictionaryItem? {
        return wordDaoImpl.loadById(id)?.toChinChar()
    }

    override fun findByChinese(chinese: String): List<DictionaryItem> {
        return wordDaoImpl.loadByEntryChinese(chinese).map { it.toChinChar() }
    }

    override fun findByPinyin(pinyin: String): List<DictionaryItem> {
        return wordDaoImpl.loadByEntryPinyin(pinyin).map { it.toChinChar() }
    }

    override fun findByTranslation(translation: String): List<DictionaryItem> {
        return wordDaoImpl.loadByEntryTranslation(translation).map { it.toChinChar() }
    }

    override fun getRadicals(): Map<Int, List<String>> {
        return radicalsDao.getAll().groupBy { it.stoke!! }.mapValues { it.value.map { it.radical!! } }.mapKeys { it.key.toIntOrNull()?:1 }
    }

    override fun getCharacters(radical: String): Map<Int, List<ZiChar>> {
        return keyDao.loadByRadical(radical).groupBy { it.stroke!! }.mapValues { it.value.map { it.toZiChar() } }
    }
}