package com.yingenus.pocketchinese.functions.search

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.dto.VariantWord
import kotlin.math.max

class MockUnitWordRepository() : UnitWordRepository {

    var isFail = false
    var isContainsAll = true

    companion object{
        const val size = 1000
    }

    override fun getUnitWord(unitWordId: Int): Result<UnitWord> {
        if (isFail) return Result.Failure("smh goes wrong")

        if (unitWordId in 1 until size+1){
            if (isContainsAll || unitWordId % 3 != 0) return Result.Success(generateUnitWord(unitWordId))
        }
        if (unitWordId <= 0 ) return Result.Failure("id is null or negative")
        return Result.Empty()
    }

    private fun generateUnitWord( id : Int) : UnitWord{
        val str = id.toString()
        val varWords = (1 .. max(1 , id /2)).map { VariantWord ( id+ it, it,it*2) }

        return UnitWord(id,str,varWords)
    }

    override fun getSize(): Result<Int> {
        if (isFail) return Result.Failure("smh goes wrong")

        return Result.Success(size)
    }
}

