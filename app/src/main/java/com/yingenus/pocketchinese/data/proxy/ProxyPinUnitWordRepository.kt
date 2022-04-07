package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.UnitWord
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import java.lang.IllegalStateException


class ProxyPinUnitWordRepository : UnitWordRepository {

    private var _repository : UnitWordRepository? = null
    private val repository :UnitWordRepository
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: UnitWordRepository){
        if (_repository == null) _repository = repository
    }

    override fun getUnitWord( unitWordId : Int): Result<UnitWord> = repository.getUnitWord(unitWordId)
    override fun getUnitWords( unitWordIds : IntArray) : Result<List<UnitWord>> = repository.getUnitWords(unitWordIds)
    override fun getAll(): Result<List<UnitWord>> = repository.getAll()
}