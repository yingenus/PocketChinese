package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.rus

import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.ResSearch
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.WordsIndexer
import com.yingenus.pocketchinese.domain.entitiys.dictionary.search.WordsIndexer.*


class RusIndexer : WordsIndexer {

    private enum class Step{
        DELETE, SPLIT, SAFE, END_DELETE, REPLACE
    }

    private object ExtractIterator{

        fun extract(sample: String): Array<String>{

            val outputWords : MutableList<String> = mutableListOf()


            var flag = Step.SAFE
            outputWords.add("")
            loop@ for (char in sample){

                when (char){
                    '(', '[','_' -> flag = Step.DELETE
                    ')', ']' -> flag = Step.END_DELETE
                    '-','/','.' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.REPLACE
                    ',',';' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SPLIT
                    else -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SAFE
                }
                when (flag){
                    Step.DELETE -> continue@loop
                    Step.SPLIT -> outputWords.add("")
                    Step.REPLACE -> outputWords[outputWords.lastIndex]+=" "
                    Step.END_DELETE -> continue@loop
                    Step.SAFE -> outputWords[outputWords.lastIndex]+= char.toString()
                }
            }

            return outputWords.map { it.replace("""\s+"""," ").trim() }.toTypedArray()
        }
    }

    override fun extract(initialSamples: Array<ResSearch.Samples>) : Array<Sentence> {
        val resultSamples = mutableMapOf<String, Sentence>()

        for (sample in initialSamples){
            for (word in ExtractIterator.extract(prepareSample(sample))){
                if (!resultSamples.containsKey(word)){
                    resultSamples[word] = Sentence(word)
                }
                resultSamples[word]!!.initialSentences.add(sample)
            }
        }
        return  resultSamples.values.toTypedArray()
    }

    override fun index(initialSamples: Array<out Sentence>): Array<Word> {
        val resultSample = mutableMapOf<String, Word>()

        for (sample in initialSamples){
            val words = sample.sentence.replace("""\s+"""," ").trim().split(" ")

            for(word in words){
                if (!resultSample.containsKey(word)){
                    resultSample[word] = Word(word)
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