package com.yingenus.pocketchinese.presentation.views.suggestist

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yingenus.pocketchinese.domain.dto.SuggestWordGroup
import com.yingenus.pocketchinese.domain.repository.ImageRep
import com.yingenus.pocketchinese.domain.repository.SuggestWordsRepository
import com.yingenus.pocketchinese.presentation.views.toUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import java.net.URI
import javax.inject.Inject

class SuggestListViewModel @AssistedInject constructor(
    @Assisted private val suggestListName : String,
    private val imageRep: ImageRep,
    private val suggestWordsRepository: SuggestWordsRepository
) : ViewModel(){

    @AssistedFactory
    interface Factory{
        fun create(suggestListName : String) : SuggestListViewModel
    }

    private val _name : MutableLiveData<String> = MutableLiveData()
    val name : LiveData<String>
        get() = _name

    private val _version : MutableLiveData<String> = MutableLiveData()
    val version : LiveData<String>
        get() = _version

    private val _words : MutableLiveData<Int> = MutableLiveData()
    val words : LiveData<Int>
        get() = _words

    private val _image : MutableLiveData<Uri> = MutableLiveData()
    val image : LiveData<Uri>
        get() = _image

    private val _description : MutableLiveData<String> = MutableLiveData()
    val description : LiveData<String>
        get() = _description

    private val _tags : MutableLiveData<List<String>> = MutableLiveData()
    val tags : LiveData<List<String>>
        get() = _tags

    private val _suggestGroups : MutableLiveData<List<SuggestWordGroup>> = MutableLiveData()
    val suggestGroups : LiveData<List<SuggestWordGroup>>
        get() = _suggestGroups

    private val _error : MutableLiveData<String> = MutableLiveData()
    val error : LiveData<String>
        get() = _error

    fun updateSuggestWords(){
        suggestWordsRepository
            .getSuggestListByName(suggestListName)
            .switchIfEmpty( Single.error(Throwable("cant find such suggestList")))
            .doOnSuccess {
                _name.postValue(it.name)
                _version.postValue(it.version)
                _words.postValue(it.words)
                _image.postValue(imageRep.getImageURI(it.image)?.toUri())
                _description.postValue(it.description)
                _tags.postValue(it.tags)
            }
            .ignoreElement()
            .andThen {
                suggestWordsRepository
                    .getSuggestWords(suggestListName)
                    .doOnSuccess {
                        _suggestGroups.postValue(it)
                    }.ignoreElement()
            }.subscribe({},{ error ->
                _error.postValue(error.message)
            })
    }

}