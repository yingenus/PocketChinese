package com.yingenus.pocketchinese.controller.fragment

import android.os.Bundle
import android.view.View
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.presenters.CreateEditWordPresenter
import com.yingenus.pocketchinese.presenters.EditWordPresenter
import java.util.*

class EditWordFragment(studyWordUUID: UUID, studyListUUID: UUID, chinCharRepository: ChinCharRepository) : CreateEditWordFragment() {

    private val presenter : EditWordPresenter = EditWordPresenter(studyWordUUID,studyListUUID,this,chinCharRepository)

    private var isUpdateEnable = false


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