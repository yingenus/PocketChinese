package com.yingenus.pocketchinese.domain.entitiys.dictionary.search

interface WordsIndexer {
    fun extract(initialSamples : Array<ResSearch.Samples>) : Array<out Sentence>
    fun index(initialSamples : Array<out Sentence>) : Array<Word>

    open class Word(val word : String){
        val links = mutableListOf<Sentence>()
    }

    open class Sentence(val sentence : String){
        val initialSentences = mutableListOf<ResSearch.Samples>()
        val links = mutableListOf<Word>()
    }
}