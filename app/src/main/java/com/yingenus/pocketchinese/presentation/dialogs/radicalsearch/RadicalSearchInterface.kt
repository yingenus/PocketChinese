package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

interface RadicalSearchInterface {

    class Character(val zi : String, val isEnabled : Boolean)

    fun setRadicals(radicals : Map<Int,List<Character>>)
    fun setCharacters(characters : Map<Int,List<Character>>)
    fun publishCharacter(character : Character)
}