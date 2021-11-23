package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.Example

interface ExampleRepository {
    fun findById( id : Int): Example?
    fun fundByChinCharId( id : Int): List<Example>
}