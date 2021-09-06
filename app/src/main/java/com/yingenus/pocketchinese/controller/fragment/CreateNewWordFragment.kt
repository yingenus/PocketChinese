package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import com.yingenus.pocketchinese.presenters.CreateWordPresenter
import java.util.*

class CreateNewWordFragment: CreateEditWordFragment,CreateWordInterface{

    private val presenter: CreateWordPresenter

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    constructor(studyListUUID: UUID): super(){
        presenter = CreateWordPresenter.Builder.getPresenter(studyListUUID,this)
    }

    constructor(chinChar: ChinChar):super(){
        presenter = CreateWordPresenter.Builder.getPresenter(chinChar,this)
    }

}