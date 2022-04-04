package com.yingenus.pocketchinese.domain.entitiys.dictionary

import com.yingenus.pocketchinese.domain.repository.ChinCharRepository

abstract class DbExtractor(val chinCharRepository: ChinCharRepository):Extractor {
}

class IdDbExtractor(chinCharRepository: ChinCharRepository): DbExtractor(chinCharRepository){
    override fun extract(query: String): List<com.yingenus.pocketchinese.domain.dto.ChinChar> {
        val result = chinCharRepository.findById(query.toInt())
        return if (result == null) emptyList() else listOf(result)
        //listOf(  emptyList<com.yingenus.pocketchinese.domain.dto.ChinChar>() else )
    }
}
/*
class RusDbExtractor(dbDao: ChinCharDaoImpl): DbExtractor(dbDao){
    override fun extract(query: String): List<ChinChar> {
        return dbDao.findChinCharInColumn(query,ChinChar.TRANSLATION_FIELD_NAME)
    }
}
class PinDbExtractor(dbDao: ChinCharDaoImpl): DbExtractor(dbDao){
    override fun extract(query: String): List<ChinChar> {
        return dbDao.findChinCharInColumn(query,ChinChar.PINYIN_FIELD_NAME)
    }
}
class ChnDbExtractor(dbDao: ChinCharDaoImpl): DbExtractor(dbDao){
    override fun extract(query: String): List<ChinChar> {
        return dbDao.findChinCharInColumn(query,ChinChar.WORD_FIELD_NAME)
    }
}

 */