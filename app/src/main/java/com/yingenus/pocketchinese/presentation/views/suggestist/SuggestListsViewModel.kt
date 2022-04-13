package com.yingenus.pocketchinese.presentation.views.suggestist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.domain.dto.SuggestList
import com.yingenus.pocketchinese.domain.repository.ImageRep
import com.yingenus.pocketchinese.domain.repository.SuggestWordsRepository
import com.yingenus.pocketchinese.presentation.views.toUri
import java.net.URI
import javax.inject.Inject

class SuggestListsViewModel @Inject constructor(
    private val suggestWordsRepository: SuggestWordsRepository,
    private val imageRep: ImageRep,
    private val settings : ISettings
) : ViewModel()
{
    class ShovedSuggestList
        (val name : String,
         val version : String,
         val words : Int,
         val image : Uri?,
         val tags : List<String>){

    }

    private var showedTags = listOf<Pair<Boolean,String>>()
        set(value) {
            _tags.postValue(value)
            field = value
        }

    private var showedNewSuggestLists = listOf<ShovedSuggestList>()
        set(value) {
            _newSuggestLists.postValue(value)
            field = value
        }

    private var showedOtherSuggestLists = listOf<ShovedSuggestList>()
        set(value) {
            _otherSuggestLists.postValue(value)
            field = value
        }

    private val _tags : MutableLiveData<List<Pair<Boolean,String>>> = MutableLiveData()
    val tags : LiveData<List<Pair<Boolean,String>>>
        get() = _tags

    private val _newSuggestLists : MutableLiveData<List<ShovedSuggestList>> = MutableLiveData()
    val newSuggestLists : LiveData<List<ShovedSuggestList>>
        get() = _newSuggestLists

    private val _otherSuggestLists : MutableLiveData<List<ShovedSuggestList>> = MutableLiveData()
    val otherSuggestLists : LiveData<List<ShovedSuggestList>>
        get() = _otherSuggestLists

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error


    fun updateSuggestLists(){
         suggestWordsRepository
             .getAllSuggestLists().subscribe { onSuccess ->
                 val tags = extractTags(onSuccess)
                 addTags(tags)
                 showLists(onSuccess)
             }
    }

    fun onTagClicked( tag : String, isSelected : Boolean){
        var activeTags = showedTags.toMutableList()
        val tagPosition = activeTags.indexOfFirst { it.second == tag }
        if (tagPosition != -1){
            activeTags.removeAt(tagPosition)
            activeTags.add(tagPosition, isSelected to tag)
            val new_selected = activeTags.map { it.first }.filter { it }.size
            if (new_selected == 0){
                showedTags = activeTags.map {  true  to it.second}
            }else{
                showedTags = activeTags
            }
        }
        updateSuggestLists()
    }

    private fun addTags( newTags : List<String>){
        val otherTags = showedTags
        val tmpTags = otherTags.toMutableList()
        val realNew = newTags.filter { tag -> !otherTags.any { it.second == tag } }
        tmpTags.addAll(realNew.map {  true to it})
        _tags.postValue( tmpTags)
    }

    private fun showLists(lists : List<SuggestList> ){
        val showed = filterByTag(lists)
        showedNewSuggestLists = getNewSuggestLists(showed).map { it.toShovedSuggestList() }
        showedOtherSuggestLists = getOtherSuggestLists(showed).map { it.toShovedSuggestList() }
    }

    private fun extractTags( lists : List<SuggestList>): List<String>{
        val tags = mutableSetOf<String>()

        lists.forEach {
            tags.addAll(it.tags)
        }

        return tags.toList()
    }

    private fun filterByTag ( lists: List<SuggestList>): List<SuggestList>{
        val selectedTags = showedTags.filter { it.first }.map { it.second }
        return lists.filter { list -> selectedTags.any { list.tags.contains(it) } }
    }

    private fun getNewSuggestLists(lists: List<SuggestList>): List<SuggestList>{
        return lists.filter { it.name.contains("слова", ignoreCase = true) }
    }

    private fun getOtherSuggestLists(lists: List<SuggestList>): List<SuggestList>{
        return lists.filter { !it.name.contains("слова", ignoreCase = true) }
    }

    private fun SuggestList.toShovedSuggestList() =
        ShovedSuggestList(
            this.name,
            this.version,
            this.words,
            imageRep.getImageURI(this.image)?.toUri(),
            this.tags
        )
}