package com.yingenus.pocketchinese.domain.entitiys.dictionary

interface Extractor {
    fun extract(query: String): List<com.yingenus.pocketchinese.domain.dto.ChinChar>
}