package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import com.yingenus.pocketchinese.domain.dto.UnitWord
import java.sql.SQLException
import javax.inject.Inject

class RoomRusSearchRepository @Inject constructor(val wordsDb: WordsDb) :
    UnitWordRepository,
    NgramM3AllAccessRep<Int>,
    com.yingenus.pocketchinese.functions.search.UnitWordRepository{

    override fun getUnitWord(unitWordId: Int): Result<UnitWord> {
        try {
            var result = wordsDb.rusWordDao().getWord(unitWordId)

            if (result != null)
                return Result.Success(result.toUnitWord())
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getSize(): Result<Int> {
        try {
            val result = wordsDb.rusWordDao().getMaxId()

            return Result.Success(result)
        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getUnitWords(unitWordIds: IntArray): Result<List<UnitWord>> {
        try {
            var result = wordsDb.rusWordDao().getWords(unitWordIds)

            if (result.isNotEmpty())
                return Result.Success(result.map { it.toUnitWord() })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getAll(): Result<List<UnitWord>> {
        try {
            var result = wordsDb.rusWordDao().getAll()

            if (result.isNotEmpty())
                return Result.Success(result.map { it.toUnitWord() })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getNgrams(ngram: String, positions: IntArray): Result<List<Pair<Int,List<Int>>>> {
        try {
            var result = wordsDb.rus3gramDao().getNgrams(ngram,positions)

            if (result.isNotEmpty())
                return Result.Success(result.map { it.position to it.wordsIds  })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getNgram(ngram: String, position: Int): Result<List<Int>> {
        try {
            var result = wordsDb.rus3gramDao().getNgram(ngram,position)

            if (result != null)
                return Result.Success(result.wordsIds)
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getAllNgrams(): Result<List<Triple<String, Int, List<Int>>>> {
        try {
            var result = wordsDb.rus3gramDao().getAll()

            if (result.isNotEmpty())
                return Result.Success(result.map { Triple(it.ngram, it.position, it.wordsIds) })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }
}