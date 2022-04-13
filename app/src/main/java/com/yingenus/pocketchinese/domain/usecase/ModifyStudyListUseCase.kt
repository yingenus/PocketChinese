package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ModifyStudyListUseCase {

    fun containsName(name: String): Single<Boolean>

    fun renameStudyList( list: ShowedStudyList, newName: String) : Completable

    fun notifyUsers( list: ShowedStudyList, notify : Boolean) : Completable

    fun createStudyList( name : String): Completable

    fun deleteStudyList( name : String): Completable

}