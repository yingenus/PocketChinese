package com.yingenus.pocketchinese.model.dictionary.search.pin

import com.yingenus.pocketchinese.model.dictionary.search.ResSearch
import com.yingenus.pocketchinese.model.dictionary.search.WordsIndexer

class PinIndexer : WordsIndexer {

    val allowTone: Boolean


    private enum class Step{
        DELETE, SPLIT, SAFE, END_DELETE, REPLACE
    }

    private object ExtractIterator {

        fun extract(sample: String): Array<String> {

            val outputWords: MutableList<String> = mutableListOf()


            var flag = Step.SAFE
            outputWords.add("")
            loop@ for (char in sample) {

                when (char) {
                    '(', '[' -> flag = Step.DELETE
                    ')', ']' -> flag = Step.END_DELETE
                    '-', '/', '.', '\'' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.REPLACE
                    ',', ';' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SPLIT
                    else -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SAFE
                }
                when (flag) {
                    Step.DELETE -> continue@loop
                    Step.SPLIT -> outputWords.add("")
                    Step.REPLACE -> outputWords[outputWords.lastIndex] += " "
                    Step.END_DELETE -> continue@loop
                    Step.SAFE -> outputWords[outputWords.lastIndex] += replaceSpecialChars(char).toString()
                }
            }

            return outputWords.map { it.replace(" ", "") }.toTypedArray()
        }

        private fun replaceSpecialChars(it: Char) = when (it) {
            'ā', 'á', 'ǎ', 'à' -> 'a'
            'ī', 'í', 'ǐ', 'ì' -> 'i'
            'ō', 'ó', 'ǒ', 'ò' -> 'o'
            'ē', 'è', 'ě', 'é' -> 'e'
            'ū', 'ù', 'ǔ', 'ú' -> 'u'
            'ǚ' -> 'v'
            else -> it
        }
    }

    constructor():this(false)

    constructor(allowTone : Boolean){
        this.allowTone = allowTone
    }

    override fun extract(initialSamples: Array<ResSearch.Samples>): Array<WordsIndexer.Sentence> {
        val resultSamples = mutableMapOf<String, WordsIndexer.Sentence>()

        for (sample in initialSamples){
            for (word in ExtractIterator.extract(prepareSample(sample))){
                if (!resultSamples.containsKey(word)){
                    resultSamples[word] = WordsIndexer.Sentence(word)
                }
                resultSamples[word]!!.initialSentences.add(sample)
            }
        }
        return  resultSamples.values.toTypedArray()
    }

    override fun index(initialSamples: Array<out WordsIndexer.Sentence>): Array<WordsIndexer.Word> {
        val resultSample = mutableMapOf<String, WordsIndexer.Word>()

        for (sample in initialSamples){
            val words = sample.sentence.replace("""\s+"""," ").trim().split(" ")

            for(word in words){
                if (!resultSample.containsKey(word)){
                    resultSample[word] = WordsIndexer.Word(word)
                }

                sample.links.add(resultSample[word]!!)
                resultSample[word]!!.links.add(sample)
            }

        }
        return resultSample.values.toTypedArray()
    }

    private fun prepareSample(sample : ResSearch.Samples): String{
        return sample.value.toLowerCase().trim()
    }
}