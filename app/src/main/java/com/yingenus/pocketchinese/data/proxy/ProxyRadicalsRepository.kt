package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.domain.dto.ZiChar
import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import com.yingenus.pocketchinese.domain.repository.search.UnitWordRepository
import java.lang.IllegalStateException

class ProxyRadicalsRepository : RadicalsRepository{

    private var _repository : RadicalsRepository? = null
    private val repository : RadicalsRepository
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: RadicalsRepository){
        if (_repository == null) _repository = repository
    }

    override fun getRadicals(): Map<Int,List<String>> = repository.getRadicals()
    override fun getCharacters(radical : String): Map<Int,List<ZiChar>> = repository.getCharacters(radical)
}