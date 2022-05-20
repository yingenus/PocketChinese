package com.yingenus.pocketchinese.presentation.views.suggestist

import android.content.Context
import android.content.Intent

const val INNER_INTENT_GIVEN_LIST = "com.pocketchinese.com.character.givenlist"

fun getSuggestActivityIntent(context: Context, suggestListName: String): Intent {
    val intent=  Intent(context, SuggestWordsActivity::class.java)
    intent.putExtra(INNER_INTENT_GIVEN_LIST,suggestListName)
    return intent
}
