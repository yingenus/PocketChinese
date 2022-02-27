package com.yingenus.pocketchinese.controller.fragment

import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import com.yingenus.pocketchinese.presenters.CreateWordPresenter
import java.util.*

class CreateNewWordFragment: CreateEditWordFragment,CreateWordInterface{

    private val presenter: CreateWordPresenter

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    constructor(studyListUUID: UUID, dictionaryItemRepository: DictionaryItemRepository): super(){
        presenter = CreateWordPresenter.Builder.getPresenter(studyListUUID,this,dictionaryItemRepository)
    }

    constructor(dictionaryItem: com.yingenus.pocketchinese.domain.dto.DictionaryItem, dictionaryItemRepository: DictionaryItemRepository):super(){
        presenter = CreateWordPresenter.Builder.getPresenter(dictionaryItem,this,dictionaryItemRepository)
    }

}