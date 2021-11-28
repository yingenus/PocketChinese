package com.yingenus.pocketchinese.domain.repository

interface RadicalsRepository {
    fun getRadicals(): Map<Int,List<String>>
    fun getCharacters(radical : String): Map<Int,List<String>>
}