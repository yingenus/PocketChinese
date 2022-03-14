package com.yingenus.pocketchinese.domain.repository.search

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.UnitWord

interface UnitWordRepository {
    fun getUnitWord( unitWordId : Int): Result<UnitWord>
    fun getUnitWords( unitWordIds : IntArray) : Result<List<UnitWord>>
    fun getAll(): Result<List<UnitWord>>
}