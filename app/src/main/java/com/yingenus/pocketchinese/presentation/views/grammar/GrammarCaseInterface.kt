package com.yingenus.pocketchinese.presentation.views.grammar

import java.net.URI

interface GrammarCaseInterface {
    fun setTitle(title : String)
    fun setLiked(isLiked : Boolean)
    fun setTitleIconURI( iconLink : URI)
    fun setHtmlURI(htmlLink : URI)
    fun declareError(msg : String)
}