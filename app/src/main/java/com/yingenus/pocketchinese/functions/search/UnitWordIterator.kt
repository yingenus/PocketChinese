package com.yingenus.pocketchinese.functions.search

import android.os.Build
import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.UnitWord
import java.util.function.Consumer

class UnitWordIterator(private val repository: UnitWordRepository) : Iterator<UnitWord> {

    private var position = 0
    private val size : Int

    init {
        val sizeResult = repository.getSize()
        size = if ( sizeResult is Result.Success){
            sizeResult.value
        }else if (sizeResult is Result.Failure){
            throw Exception(sizeResult.msg)
        }
        else{
            0
        }
    }

    override fun forEachRemaining(action: Consumer<in UnitWord>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            while (hasNext()){
                action.accept(next())
            }
        }
    }

    override fun hasNext(): Boolean {
        return size > 0 && position < size
    }

    override fun next(): UnitWord {
        return getNext()
    }

    private fun getNext() : UnitWord {

        require( position < size)

        var wordResult : Result<UnitWord>? = null

        do {

            wordResult = repository.getUnitWord(++position)

            if (wordResult is Result.Failure) throw Exception(wordResult.msg)

        }while ( position < size && wordResult !is Result.Success )

        if (wordResult is Result.Success){
            return wordResult.value
        }else{
            throw Exception("cant get wore unit words from repository")
        }
    }


}