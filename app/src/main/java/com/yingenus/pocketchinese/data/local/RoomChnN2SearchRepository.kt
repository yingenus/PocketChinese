package com.yingenus.pocketchinese.data.local

import androidx.work.workDataOf
import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.data.local.room.VariantsDao
import com.yingenus.pocketchinese.data.local.room.WordsDb
import com.yingenus.pocketchinese.domain.repository.search.NgramRepository
import main.newsearch.dto.VariantWord
import java.sql.SQLException

class RoomChnN2SearchRepository(val wordsDb: WordsDb): NgramRepository<VariantWord>{
    override fun getNgrams(ngram: String): Result<List<VariantWord>> {
        try {
            val result = wordsDb.chnNgramDao().get2gram(ngram)

            if (result != null)
                return Result.Success(result.wordVariants)
            else
                return Result.Empty()

        }catch (e : SQLException){
            return Result.Failure(e.toString())
        }
    }
}