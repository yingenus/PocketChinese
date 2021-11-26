package com.yingenus.pocketchinese.domain.repositorys

interface CharactersRadicalRepository {
    fun getRadicals(): Map<Int,List<String>>
    fun getCharacters(radical : String): Map<Int,List<String>>
}