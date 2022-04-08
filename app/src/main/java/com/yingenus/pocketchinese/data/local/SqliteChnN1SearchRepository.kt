package com.yingenus.pocketchinese.data.local

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.sqlite.DictionaryDBHelper
import com.yingenus.pocketchinese.data.local.sqlite.dao.ChnNgramDao
import com.yingenus.pocketchinese.data.local.sqlite.dao.ChnNgramDaoImpl
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import com.yingenus.pocketchinese.domain.dto.VariantWord
import java.sql.SQLException
import javax.inject.Inject

class SqliteChnN1SearchRepository @Inject constructor(dictionaryHelper: DictionaryDBHelper): NgramRepository<VariantWord>{

    private val chinNgramDao : ChnNgramDao = ChnNgramDaoImpl(dictionaryHelper.connectionSource)

    override fun getNgrams(ngram: String): Result<List<VariantWord>> {
        try {
            val result = chinNgramDao.get1gram(ngram)

            if (result != null)
                return Result.Success(result.wordVariants)
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }
}