package com.yingenus.pocketchinese.domain.entitiys.dictionary.search

import java.io.InputStream

interface ResSearch {
    fun searchSentence(sentence : String, vararg resources : InputStream): List<SearchResult>
    //fun setSamples(samples : Array<Samples>)


    open class SearchResult(val idPlus : Int, val wight : Int){

        companion object{
            fun create(id : Int, index : Int, wight : Int): SearchResult{
                val idPlus = id shl 10 + index and 0b1111111111
                return SearchResult(idPlus, wight)
            }


        }

        val id = idPlus shr 10
        val index = idPlus and 0b1111111111

        override fun equals(other: Any?): Boolean {
            if (other is SearchResult){
                if (other === this)
                    return true
                if (other.idPlus == this.idPlus)
                    return true
            }
            return false
        }

        override fun hashCode(): Int {
            return idPlus
        }

        override fun toString(): String {
            return "SearchResult idPlus = $idPlus"
        }
    }

    open class Samples(val value : String){

    }

}