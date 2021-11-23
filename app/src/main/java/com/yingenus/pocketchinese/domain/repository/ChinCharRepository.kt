package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.ChinChar

interface ChinCharRepository {
    fun getAllChinChar(): List<ChinChar>
    fun findById( id : Int): ChinChar?
    fun findByChinese( chinese : String): List<ChinChar>
    fun findByPinyin( pinyin : String): List<ChinChar>
    fun findByTranslation( translation : String): List<ChinChar>
}