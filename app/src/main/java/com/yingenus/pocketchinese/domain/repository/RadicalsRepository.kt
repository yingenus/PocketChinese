package com.yingenus.pocketchinese.domain.repository

interface RadicalsRepository {
    fun getRadicals(): List<Pair<Int,List<String>>>
    fun getCharacters(radical : String): List<Pair<Int,List<String>>>
}