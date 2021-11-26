package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import com.yingenus.pocketchinese.presenters.CreateWordPresenter
import java.util.*

class CreateNewWordFragment: CreateEditWordFragment,CreateWordInterface{

    private val presenter: CreateWordPresenter

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    constructor(studyListUUID: UUID, chinCharRepository: ChinCharRepository): super(){
        presenter = CreateWordPresenter.Builder.getPresenter(studyListUUID,this,chinCharRepository)
    }

    constructor(chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar,chinCharRepository: ChinCharRepository):super(){
        presenter = CreateWordPresenter.Builder.getPresenter(chinChar,this,chinCharRepository)
    }

}