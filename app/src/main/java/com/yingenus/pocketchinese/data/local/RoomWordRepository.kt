package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.domain.dto.ChinChar
import com.yingenus.pocketchinese.domain.dto.Tone
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository

class RoomWordRepository(val wordsDb: WordsDb): ChinCharRepository, RadicalsRepository, ToneRepository {

    override fun getAllChinChar(): List<ChinChar> {
        return wordsDb.wordDao().getAll().map { it.toChinChar() }
    }

    override fun getAllTone(): List<Tone> {
        return wordsDb.variantsDao().getAll().map { it.toTone() }
    }

    override fun findById(id: Int): ChinChar? {
        return wordsDb.wordDao().loadById(id)?.toChinChar()
    }

    override fun findByChinese(chinese: String): List<ChinChar> {
        return wordsDb.wordDao().loadByEntryChinese(chinese).map { it.toChinChar() }
    }

    override fun findByPinyin(pinyin: String): List<ChinChar> {
        return wordsDb.wordDao().loadByEntryPinyin(pinyin).map { it.toChinChar() }
    }

    override fun findByTranslation(translation: String): List<ChinChar> {
        return wordsDb.wordDao().loadByEntryTranslation(translation).map { it.toChinChar() }
    }

    override fun getRadicals(): Map<Int, List<String>> {
        return wordsDb.radicalsDao().getAll().groupBy { it.stoke }.mapValues { it.value.map { it.radical } }.mapKeys { it.key.toIntOrNull()?:1 }
    }

    override fun getCharacters(radical: String): Map<Int, List<String>> {
        return wordsDb.keyDao().loadByRadical(radical).groupBy { it.stroke }.mapValues { it.value.map { it.character } }
    }
}