package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.ISettings
import com.yingenus.pocketchinese.domain.dto.ShowedStudyWord
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.StudyWordStatistic
import com.yingenus.pocketchinese.domain.entities.studystatictic.WordStatistic
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class WordInfoUseCaseImpl @Inject constructor(
    private val studyRepository: StudyRepository,
    private val wordStatistic: WordStatistic,
    setting : ISettings
) : WordInfoUseCase {

    init {
        wordStatistic.setRepeatType(setting.getRepeatType())
    }

    override fun getStudyWord(id: Long): Maybe<StudyWord> {
        return studyRepository.getStudyWord(id)
    }

    override fun getShowedStudyWords(studyListId: Long): Single<List<ShowedStudyWord>> {
        return studyRepository.getStudyWordsByListId(studyListId)
            .flatMapObservable { Observable.fromIterable(it) }
            .flatMap { word ->
                wordStatistic.getStatistic(word).map { showedStudyWord(word, it) }.toObservable()
            }
            .collect({ mutableListOf<ShowedStudyWord>() }, { list, word ->
                list.add(word)
            }).map { it.toList() }
    }

    override fun getShowedStudyWord(id: Long): Maybe<ShowedStudyWord> {
        return studyRepository.getStudyWord(id).flatMap { word ->
            wordStatistic.getStatistic(word).map { showedStudyWord(word,it) }.toMaybe()
        }
    }

    override fun getStudyWordStatistic(id: Long): Maybe<StudyWordStatistic> {
        return studyRepository.getStudyWord(id).flatMap { word ->
            wordStatistic.getStatistic(word).toMaybe()
        }
    }

    private fun showedStudyWord( studyWord: StudyWord, studyWordStatistic: StudyWordStatistic): ShowedStudyWord{
        return ShowedStudyWord(
            id = studyWord.id,
            chinese = studyWord.chinese,
            pinyin = studyWord.pinyin,
            translate = studyWord.translate,
            wordSuccess = studyWordStatistic.wordSuccess,
            recomend = studyWordStatistic.recomend,
            createDate = studyWord.createDate
        )
    }
}