package com.yingenus.pocketchinese.controller.dialog

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.model.database.dictionaryDB.Example

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