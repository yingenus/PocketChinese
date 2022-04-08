package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.dao.Pin3gramDao
import com.yingenus.pocketchinese.data.local.sqlite.dao.Pin3gramDaoImpl
import com.yingenus.pocketchinese.data.local.sqlite.dao.PinWordDao
import com.yingenus.pocketchinese.data.local.sqlite.dao.PinWordDaoImpl
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import com.yingenus.pocketchinese.domain.dto.UnitWord
import java.lang.annotation.Inherited
import java.sql.SQLException
import javax.inject.Inject

class SqlitePinSearchRepository @Inject constructor(dictionaryHelper: DictionaryDBHelper) :
    UnitWordRepository,
    NgramM3AllAccessRep<Int>,
    com.yingenus.pocketchinese.functions.search.UnitWordRepository{

    private val pinWordDao : PinWordDao = PinWordDaoImpl(dictionaryHelper.connectionSource)
    private val pin3gramDao : Pin3gramDao = Pin3gramDaoImpl(dictionaryHelper.connectionSource)

    override fun getUnitWord(unitWordId: Int): Result<UnitWord> {
        try {
            var result = pinWordDao.getWord(unitWordId)

            if (result != null)
                return Result.Success(result.toUnitWord())
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getUnitWords(unitWordIds: IntArray): Result<List<UnitWord>> {
        try {
            var result = pinWordDao.getWords(unitWordIds)

            if (result.isNotEmpty())
                return Result.Success(result.map { it.toUnitWord() })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getSize(): Result<Int> {
        try {
            val result = pinWordDao.getMaxId()

            return Result.Success(result)
        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getAll(): Result<List<UnitWord>> {
        try {
            var result = pinWordDao.getAll()

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
            var result = pin3gramDao.getNgrams(ngram,positions)

            if (result.isNotEmpty())
                return Result.Success(result.map { it.position!! to it.wordsIds  })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }

    override fun getNgram(ngram: String, position: Int): Result<List<Int>> {
        try {
            var result = pin3gramDao.getNgram(ngram,position)

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
            var result = pin3gramDao.getAll()

            if (result.isNotEmpty())
                return Result.Success(result.map { Triple(it.ngram!!, it.position!!, it.wordsIds) })
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }
}