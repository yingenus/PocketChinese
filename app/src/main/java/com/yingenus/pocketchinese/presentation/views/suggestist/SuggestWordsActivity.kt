package com.yingenus.pocketchinese.controller.activity

import android.content.Context
import android.content.Intent
import com.yingenus.pocketchinese.domain.entitiys.words.suggestwords.JSONObjects
import com.yingenus.pocketchinese.presentation.views.suggestist.SuggestWordsActivity

const val INNER_INTENT_GIVEN_LIST = "com.pocketchinese.com.character.givenlist"

fun getSuggestActivityIntent(context: Context, file: JSONObjects.FileInfo): Intent {
    val intent=  Intent(context, SuggestWordsActivity::class.java)
    intent.putExtra(INNER_INTENT_GIVEN_LIST,file)
    return intent
}
