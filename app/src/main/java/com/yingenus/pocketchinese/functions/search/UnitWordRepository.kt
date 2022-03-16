package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.UnitWord

interface UnitWordRepository {
    fun getUnitWord( unitWordId : Int): Result<UnitWord>
    fun getSize(): Result<Int>
}