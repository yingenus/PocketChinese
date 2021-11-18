package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

interface RadicalSearchInterface {
    fun setRadicals(radicals : Map<Int,List<String>>)
    fun setCharacters(characters : Map<Int,List<String>>)
    fun publishCharacter(character : String)
}