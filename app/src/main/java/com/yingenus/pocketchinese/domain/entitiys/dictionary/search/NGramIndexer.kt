package com.yingenus.pocketchinese.domain.entitiys.dictionary.search

interface NGramIndexer : WordsIndexer {

    override fun extract(initialSamples: Array<ResSearch.Samples>): Array<NGramSentence>

    fun index2N(initialSamples: Array<NGramSentence>): Array<WordsIndexer.Word>

    open class NGramSentence(sentence : String) : WordsIndexer.Sentence(sentence){
        val twoNLinks = mutableListOf<WordsIndexer.Word>()
    }

}