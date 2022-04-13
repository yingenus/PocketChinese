package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.common.Result
import com.yingenus.pocketchinese.domain.dto.SuggestList
import com.yingenus.pocketchinese.domain.dto.SuggestListDetailed
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.domain.dto.SuggestWordGroup
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface SuggestWordsRepository {
    fun getAllSuggestLists(): Single<List<SuggestList>>
    fun getSuggestListByName( name : String) : Maybe<SuggestListDetailed>
    fun getSuggestWords( name : String) : Single<List<SuggestWordGroup>>
}