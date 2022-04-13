package com.yingenus.pocketchinese.domain.usecase

import com.yingenus.pocketchinese.domain.dto.AddWordsConfig
import com.yingenus.pocketchinese.domain.dto.StudyList
import com.yingenus.pocketchinese.domain.dto.StudyWord
import com.yingenus.pocketchinese.domain.dto.SuggestWord
import com.yingenus.pocketchinese.domain.repository.StudyRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject
import kotlin.random.Random

class AddSuggestWordsToStudyListImpl @Inject constructor(
    private val studyRepository: StudyRepository
): AddSuggestWordsToStudyList {


    override fun addSuggestWords(
        addWordsConfig: AddWordsConfig,
        words: List<SuggestWord>
    ): Completable {
        return studyRepository.getStudyListById(addWordsConfig.studyListId)
            .switchIfEmpty(Single.error(Throwable("cant complete because no such item")))
            .flatMap { Single.create<Pair<StudyList,List<SuggestWord>>> {
                    if (addWordsConfig.mixWords){
                        it to getRandomize(words)
                    }else{
                        it to words
                    }
                }
            }
            .map {
                if (addWordsConfig.romanization == AddWordsConfig.Romanization.LETTER_PINYIN){
                    it.first to it.second.map { it.simplify2PIN() }
                }else{
                    it.first to it.second
                }
            }
            .map {
                it.first to it.second.map { it.toStudyWord() }
            }
            .flatMapCompletable {
                studyRepository.addStudyWords(it.first,it.second)
            }
    }

    private fun SuggestWord.toStudyWord() : StudyWord =
        StudyWord(
            id = 0,
            chinese = this.word,
            pinyin = this.pinyin,
            translate = this.translation,
            createDate = Date(System.currentTimeMillis())
        )

    private fun getRandomize(list : List<SuggestWord>) : List<SuggestWord>{
        val randomized = Array<SuggestWord?>(list.size) { null}

        val freeIndex = randomized.mapIndexed { index, _ ->  index}.toMutableList()

        for (word in list){
            val insIndex = freeIndex
                .removeAt(Random.nextInt(freeIndex.size))

            if (randomized[insIndex] != null)
                throw RuntimeException("index is busy")

            randomized[insIndex] = word
        }

        return randomized.filterNotNull()
    }

    private fun SuggestWord.simplify2PIN()=
        SuggestWord( this.word, this.pinyin.replace(Regex("""[āáǎà]"""),"a")
            .replace(Regex("""[īíǐì]"""),"i")
            .replace(Regex("""[ōóǒò]"""),"o")
            .replace(Regex("""[ēèěé]"""),"e")
            .replace(Regex("""[ūùǔú]"""),"u")
            .replace(Regex("""[ǚ]"""),"v"),
            this.translation,
            this.examples,
            this.description
        )
}