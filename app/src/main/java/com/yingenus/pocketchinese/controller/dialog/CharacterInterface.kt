package com.yingenus.pocketchinese.controller.dialog

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.model.database.dictionaryDB.Example

interface CharacterInterface {


    fun startAddNewStudy(word : ChinChar)

    fun setChin(text : String)
    fun setLinked( links : List<String>)
    fun setPinyin(text : String)

    fun setTags(tags : List<String>)
    fun setTranslations(trns : List<String>)
    fun setExamples(exampls : List<Example>)
    fun setCharacters(entrysChars : List<ChinChar>)

}