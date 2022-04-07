package com.yingenus.pocketchinese.data.proxy

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.VariantWord
import com.yingenus.pocketchinese.domain.repository.ToneRepository
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import java.lang.IllegalStateException

class ProxyChinN2Repository : NgramRepository<VariantWord> {

    private var _repository : NgramRepository<VariantWord> ? = null
    private val repository : NgramRepository<VariantWord>
        get() = _repository ?: throw IllegalStateException("proxed class is not inited")

    fun setRepository( repository: NgramRepository<VariantWord>){
        if (_repository == null) _repository = repository
    }

    override fun getNgrams( ngram : String) : Result<List<VariantWord>> = repository.getNgrams(ngram)
}