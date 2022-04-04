package com.yingenus.pocketchinese.presentation.views.creteeditword

import android.os.Bundle
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateEditWordFragment
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateEditWordPresenter
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordInterface
import com.yingenus.pocketchinese.presentation.views.creteeditword.CreateWordPresenter
import java.util.*
import javax.inject.Inject

class CreateNewWordFragment: CreateEditWordFragment, CreateWordInterface {

    private val studyListUUID: UUID?
    private val chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar?

    @Inject
    lateinit var createWordPresenterFactory : CreateWordPresenter.Factory
    private lateinit var presenter: CreateWordPresenter

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PocketApplication.getAppComponent().injectCreateNewWordFragment(this)
        if (chinChar != null){
            presenter = createWordPresenterFactory.create(chinChar,this)
        }
        if (studyListUUID != null){
            presenter = createWordPresenterFactory.create(studyListUUID,this)
        }
    }

    constructor(studyListUUID: UUID): super(){
        this.studyListUUID = studyListUUID
        this.chinChar = null
    }

    constructor(chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar):super(){
        this.studyListUUID = null
        this.chinChar = chinChar
    }

}