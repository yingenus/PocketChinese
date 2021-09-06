package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.model.words.suggestwords.JSONObjects

interface SuggestListsInterface {

    enum class SortType{
        AZ,ZA
    }

    class Item(val wordsList: JSONObjects.FileInfo){
        var isNew = true
    }

    fun setItems(items : List<Item>)
    fun updateItems(items: List<Item>)
    fun newFires(itIs : Boolean)
    fun setSortType(type : SortType)
    fun showWordsList(item : JSONObjects.FileInfo)
    fun setTags(tags : List<String>)
}