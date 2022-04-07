package com.yingenus.pocketchinese.presentation.views.creteeditword

import android.os.Bundle
import android.view.View
import com.yingenus.pocketchinese.PocketApplication
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.fragment.CreateEditWordFragment
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import java.util.*
import javax.inject.Inject

class EditWordFragment (private val studyWordUUID: UUID, private val studyListUUID: UUID) : CreateEditWordFragment() {

    @Inject
    lateinit var editWordPresenterFactory : EditWordPresenter.Factory
    private lateinit var presenter : EditWordPresenter

    private var isUpdateEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PocketApplication.getAppComponent().injectEditWordFragment(this)
        presenter = editWordPresenterFactory.crete(studyWordUUID,studyListUUID, this)
    }

    override fun getViewPresenter(): CreateEditWordPresenter {
        return presenter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.setText(R.string.edit_word)
        createButton.setText(R.string.change)
        blockTabLayout.visibility = View.INVISIBLE
        blockTabLayout.isEnabled = false
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        isUpdateEnable=true
        super.onFocusChange(v, hasFocus)
    }
}