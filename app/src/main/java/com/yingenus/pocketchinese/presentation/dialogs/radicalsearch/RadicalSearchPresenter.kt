package com.yingenus.pocketchinese.presentation.dialogs.radicalsearch

import com.yingenus.pocketchinese.domain.repositorys.CharactersRadicalRepository

class RadicalSearchPresenter(private var view: RadicalSearchInterface?, private val radicalRepository: CharactersRadicalRepository) {

    init {
        view?.setRadicals(radicalRepository.getRadicals())
    }

    var isShowRadical = true


    fun onBackPressed(){
        if (!isShowRadical){
            view?.setRadicals(radicalRepository.getRadicals())
            isShowRadical = true
        }
    }

    fun radicalSelected(radical : String){
        if (isShowRadical){
            isShowRadical = false
            view?.setCharacters(radicalRepository.getCharacters(radical))
        }else{
            view?.publishCharacter(radical)
        }
    }

    fun onDestroy(){
        view = null
    }

}