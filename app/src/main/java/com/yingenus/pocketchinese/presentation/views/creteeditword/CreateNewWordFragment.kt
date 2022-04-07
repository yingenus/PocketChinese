package com.yingenus.pocketchinese.presentation.views.creteeditword

import android.os.Bundle
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.controller.fragment.CreateEditWordFragment
import com.yingenus.pocketchinese.domain.dto.DictionaryItem
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import java.util.*
import javax.inject.Inject


class CreateNewWordFragment: CreateEditWordFragment,CreateWordInterface{

    private lateinit var presenter: CreateWordPresenter

    private val studyListUUID: UUID?
    private val dictionaryItem: DictionaryItem?

    @Inject
    lateinit var createWordPresenterFactory : CreateWordPresenter.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PocketApplication.getAppComponent().injectCreateNewWordFragment(this)
        if (dictionaryItem != null){
            presenter = createWordPresenterFactory.create(dictionaryItem,this)
        }
        if (studyListUUID != null){
            presenter = createWordPresenterFactory.create(studyListUUID,this)
        }
    }

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    constructor(studyListUUID: UUID): super(){
        this.studyListUUID = studyListUUID
        this.dictionaryItem = null
    }

    constructor(dictionaryItem: com.yingenus.pocketchinese.domain.dto.DictionaryItem):super(){
        this.studyListUUID = null
        this.dictionaryItem = dictionaryItem
    }


}
