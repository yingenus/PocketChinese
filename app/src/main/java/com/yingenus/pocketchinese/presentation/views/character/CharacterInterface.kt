package com.yingenus.pocketchinese.presentation.views.character

interface CharacterInterface {


    fun startAddNewStudy(word : com.yingenus.pocketchinese.domain.dto.ChinChar)

    fun setChin(text : String)
    fun setLinked( links : List<String>)
    fun setPinyin(text : String)

    fun setTags(tags : List<String>)
    fun setTranslations(trns : List<String>)
    fun setExamples(exampls : List<com.yingenus.pocketchinese.domain.dto.Example>)
    fun setCharacters(entrysChars : List<com.yingenus.pocketchinese.domain.dto.ChinChar>)

}