package com.yingenus.pocketchinese.model.dictionary

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinCharDaoImpl

abstract class DbExtractor(val dbDao: ChinCharDaoImpl):Extractor {
}

class IdDbExtractor(dbDao: ChinCharDaoImpl): DbExtractor(dbDao){
    override fun extract(query: String): List<ChinChar> {
        return listOf(dbDao.queryForId(query))
    }
}

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