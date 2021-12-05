package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

import com.yingenus.pocketchinese.domain.repository.RadicalsRepository

class RadicalSearchPresenter(private var view: RadicalSearchInterface?, private val radicalRepository: RadicalsRepository) {

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