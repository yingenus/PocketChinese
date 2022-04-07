package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.domain.dto.Tone
import com.yingenus.pocketchinese.domain.repository.ExampleRepository
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import java.lang.IllegalStateException

class ProxyToneRepository : ToneRepository {

    private var _repository : ToneRepository? = null
    private val repository : ToneRepository
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: ToneRepository){
        if (_repository == null) _repository = repository
    }

    override fun getAllTone(): List<Tone> = repository.getAllTone()
}