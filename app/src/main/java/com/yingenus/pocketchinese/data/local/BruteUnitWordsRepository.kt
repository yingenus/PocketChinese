package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import com.yingenus.pocketchinese.domain.dto.UnitWord

class BruteUnitWordsRepository( private val unitWordRepository: UnitWordRepository) : UnitWordRepository {
    override fun getUnitWord(unitWordId: Int): Result<UnitWord> {
        val result = unitWordRepository.getAll()
        return if (result is Result.Success){

            val candidate = result.value.find { it.unitWordId == unitWordId }
            if (candidate != null) Result.Success(candidate)
            else Result.Empty()
        }
        else if (result is Result.Failure){
            Result.Failure(result.msg)
        }else{
            Result.Empty()
        }
    }

    override fun getUnitWords(unitWordIds: IntArray): Result<List<UnitWord>> {
        val result = unitWordRepository.getAll()
        return if (result is Result.Success){

            val idsSet = unitWordIds.toHashSet()

            val candidats = mutableListOf<UnitWord>()
            result.value.forEach {
                if ( idsSet.contains(it.unitWordId)){
                    candidats.add(it)
                }

            }
            if (candidats.isNotEmpty()) Result.Success(candidats.toList())
            else Result.Empty()
        }
        else if (result is Result.Failure){
            Result.Failure(result.msg)
        }else{
            Result.Empty()
        }
    }

    override fun getAll(): Result<List<UnitWord>> {
        return unitWordRepository.getAll()
    }
}