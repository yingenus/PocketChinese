package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

import com.yingenus.pocketchinese.domain.repository.RadicalsRepository
import dagger.assisted.AssistedInject
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory

class RadicalSearchPresenter @AssistedInject constructor(@Assisted private var view: RadicalSearchInterface?, private val radicalRepository: RadicalsRepository) {

    @AssistedFactory
    interface Factory{
        fun create(view: RadicalSearchInterface) : RadicalSearchPresenter
    }

    init {
        view?.setRadicals(radicalRepository.getRadicals().mapValues { it.value.map { RadicalSearchInterface.Character(it,true) } })
    }

    var isShowRadical = true


    fun onBackPressed(){
        if (!isShowRadical){
            view?.setRadicals(radicalRepository.getRadicals().mapValues { it.value.map { RadicalSearchInterface.Character(it,true) } })
            isShowRadical = true
        }
    }

    fun radicalSelected(radical : RadicalSearchInterface.Character){
        if (isShowRadical){
            isShowRadical = false
            view?.setCharacters(radicalRepository.getCharacters(radical.zi).mapValues { it.value.map { RadicalSearchInterface.Character(it.character,it.isInDb) } })
        }else{
            view?.publishCharacter(radical)
        }
    }

    fun onDestroy(){
        view = null
    }

}