package com.yingenus.pocketchinese.model.dictionary

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar

interface Extractor {
    fun extract(query: String): List<com.yingenus.pocketchinese.domain.dto.ChinChar>
}