package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.domain.dto.ShowedStudyList
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyListStatistic
import com.yingenus.pocketchinese.domain.dto.StudyListStatisticShort
import com.yingenus.pocketchinese.domain.entities.studystatictic.ListStatistic
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import javax.inject.Inject

class StudyListInfoUseCaseImpl @Inject constructor(
    private val studyRepository: StudyRepository,
    private val listStatistic: ListStatistic,
    settings : ISettings
) : StudyListInfoUseCase {

    init {
        listStatistic.setRepeatType(settings.getRepeatType())
    }

    override fun getAllStudyLists(): Single<List<ShowedStudyList>> {
        return studyRepository.getAllStudyList()
            .flatMapObservable { Observable.fromIterable(it) }
            .map { list ->
                listStatistic.getShortStatistic(list)
                    .map {
                        showedStudyList(list, it)
                        }.toObservable()
            }.flatMap { it }.collect({ mutableListOf<ShowedStudyList>()},{ list:MutableList<ShowedStudyList>, statistic ->
                list.add(statistic)
            })
            .map { it.toList() }
    }

    override fun getStudyList(name: String): Maybe<ShowedStudyList> {
        return studyRepository.getStudyListByName(name)
            .flatMap { list ->
                listStatistic.getShortStatistic(list).map { showedStudyList(list, it) }.toMaybe()
            }
    }

    override fun getStudyList(id: Long): Maybe<ShowedStudyList> {
        return studyRepository.getStudyListById(id)
            .flatMap { list ->
                listStatistic.getShortStatistic(list).map { showedStudyList(list, it) }.toMaybe()
            }
    }

    override fun getStudyListStatistic(name: String): Maybe<StudyListStatistic> {
        return studyRepository.getStudyListByName(name)
            .flatMap { list ->
                listStatistic.getStatistic(list).toMaybe()
            }
    }

    override fun getStudyListStatistic(id: Long): Maybe<StudyListStatistic> {
        return studyRepository.getStudyListById(id)
            .flatMap { list ->
                listStatistic.getStatistic(list).toMaybe()
            }
    }

    private fun showedStudyList( studyList: StudyList, shortStatistic: StudyListStatisticShort): ShowedStudyList{
        return ShowedStudyList(
            id = studyList.id,
            name = studyList.name,
            notifyUser = studyList.notifyUser,
            repeat = shortStatistic.repeat,
            percentComplete = shortStatistic.percentComplete,
            words = shortStatistic.words,
            repeatDate = shortStatistic.lastRepeat,
            createDate = studyList.createDate
        )
    }
}