package com.yingenus.pocketchinese.presentation.views.grammar

import com.yingenus.pocketchinese.domain.dto.GrammarCase
import com.yingenus.pocketchinese.domain.repository.GrammarRep
import com.yingenus.pocketchinese.domain.repository.ImageRep
import java.net.URI

class GrammarPresenter(
        var view: GrammarCaseInterface?,
        grammarCaseName: String,
        val grammarRep: GrammarRep,
        val imageRep: ImageRep
        ) {

    private val grammarCase : GrammarCase? = grammarRep.getCase(grammarCaseName)

    private var isLiked = false

    init {

        view?.setTitle(grammarCase?.title?:"")

        if (grammarCase != null){
            val imageUri = imageRep.getImageUri(grammarCase.image)
            if(imageUri != null) view?.setTitleIconURI(imageUri)

            val uri = URI.create("https://appassets.androidplatform.net/"+grammarCase.link)

            view?.setHtmlURI(uri)
        }
    }

    fun likePressed(){
        isLiked = !isLiked
        view?.setLiked(isLiked)
    }

    fun onDestroy(){
        view = null
    }
}