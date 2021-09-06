package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.controller.fragment.AddWordFragment
import com.yingenus.pocketchinese.model.words.suggestwords.JSONObjects
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddWordsActivity : ScrollingContainerActivity(), AddWordFragment.AddWordsCallbacks{
    object Builder{
        fun getIntent(context : Context, words : List<JSONObjects.Word>): Intent{
            AddWordFragment.InsertingList.word = words
            return Intent(context,AddWordsActivity::class.java)
        }
    }
    override fun createFragment() = AddWordFragment()


    override fun onError() {
        MaterialAlertDialogBuilder(applicationContext)
                .setTitle(getString(R.string.error_insert_words))
                .setPositiveButton(android.R.string.cancel) { _, _-> finish()}
                .show()
    }

    override fun onClose() {
        finish()
    }

    override fun onAdded() {
        MaterialAlertDialogBuilder(applicationContext)
                .setTitle(getString(R.string.success_insert_words))
                .setPositiveButton(android.R.string.cancel) { _, _-> finish()}
                .show()
    }
}