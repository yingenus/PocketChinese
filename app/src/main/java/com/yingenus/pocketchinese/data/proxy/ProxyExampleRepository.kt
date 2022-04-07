package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.domain.dto.Example
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import java.lang.IllegalStateException

class ProxyExampleRepository : ExampleRepository {

    private var _repository : ExampleRepository? = null
    private val repository : ExampleRepository
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: ExampleRepository){
        if (_repository == null) _repository = repository
    }

    override fun findById( id : Int): Example? = repository.findById(id)
    override fun fundByChinCharId( id : Int): List<Example> = repository.fundByChinCharId(id)
    override fun fundByChinCharId( id : Int, maxSize : Int): List<Example> = repository.fundByChinCharId(id,maxSize)
}