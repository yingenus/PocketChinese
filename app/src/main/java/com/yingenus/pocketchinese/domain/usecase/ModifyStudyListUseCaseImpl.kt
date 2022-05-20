package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ModifyStudyListUseCaseImpl @Inject constructor(
    private val studyRepository: StudyRepository
): ModifyStudyListUseCase {

    override fun containsName(name: String): Single<Boolean> {
        return studyRepository.getStudyListByName(name).map { true }.defaultIfEmpty(false)
    }

    override fun renameStudyList(list: ShowedStudyList, newName: String): Completable {
        return containsName(newName).flatMapMaybe {
            if (it) Maybe.error(Throwable("this name alrady taken"))
            else studyRepository.getStudyListById(list.id)
        }
            .switchIfEmpty(Single.error(Throwable("cant rename because no such item")))
            .flatMapCompletable {
                it.name = newName
                studyRepository.updateStudyList(it)
            }

    }

    override fun notifyUsers(list: ShowedStudyList, notify: Boolean): Completable {
        return studyRepository.getStudyListById(list.id)
                .switchIfEmpty(Single.error(Throwable("cant rename because no such item")))
            .flatMapCompletable {
                it.notifyUser = notify
                studyRepository.updateStudyList(it)
            }
    }

    override fun createStudyList(name: String): Completable {
        return studyRepository.createStudyList(
            StudyList( 0, name,true)
        )
    }

    override fun deleteStudyList(name: String): Completable {
        return studyRepository.getStudyListByName(name)
            .switchIfEmpty(Single.error(Throwable("cant rename because no such item")))
            .flatMapCompletable { list->
                studyRepository.deleteStudyList(list)
            }
    }
}