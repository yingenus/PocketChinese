package com.yingenus.pocketchinese.presentation.views.suggestist

import com.yingenus.pocketchinese.data.json.suggest.JSONObjects

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