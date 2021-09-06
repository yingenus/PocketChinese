package com.yingenus.pocketchinese.model.dictionary.search.chn

import com.yingenus.pocketchinese.model.dictionary.search.NGramIndexer
import com.yingenus.pocketchinese.model.dictionary.search.ResSearch
import com.yingenus.pocketchinese.model.dictionary.search.WordsIndexer


class ChnIndexer : NGramIndexer {
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
                    '-', '/', '.', '\'',',' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.REPLACE
                    ';' -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SPLIT
                    else -> flag = if (flag == Step.DELETE) Step.DELETE else Step.SAFE
                }
                when (flag) {
                    Step.DELETE -> continue@loop
                    Step.SPLIT -> outputWords.add("")
                    Step.REPLACE -> outputWords[outputWords.lastIndex] += " "
                    Step.END_DELETE -> continue@loop
                    Step.SAFE -> outputWords[outputWords.lastIndex] += char.toString()
                }
            }

            return outputWords.map { it.replace(" ", "") }.toTypedArray()
        }


    }

    override fun extract(initialSamples: Array<ResSearch.Samples>): Array<NGramIndexer.NGramSentence> {
        val resultSamples = mutableMapOf<String, NGramIndexer.NGramSentence>()

        for (sample in initialSamples){
            for (word in ExtractIterator.extract(prepareSample(sample))){
                if (!resultSamples.containsKey(word)){
                    resultSamples[word] = NGramIndexer.NGramSentence(word)
                }
                resultSamples[word]!!.initialSentences.add(sample)
            }
        }
        return  resultSamples.values.toTypedArray()
    }
    // index for 1N
    override fun index(initialSamples: Array<out WordsIndexer.Sentence>): Array<WordsIndexer.Word> {
        val resultSample = mutableMapOf<String, WordsIndexer.Word>()

        for (sample in initialSamples){
            val word = sample.sentence.replace("""\s+""","").trim()

            for (char in word){

                val chr = char.toString()

                if (!resultSample.containsKey(chr)){
                    resultSample[chr] = WordsIndexer.Word(chr)
                }

                sample.links.add(resultSample[chr]!!)
                resultSample[chr]!!.links.add(sample)

            }

        }
        return resultSample.values.toTypedArray()
    }

    // index for 2N
    override fun index2N(initialSamples: Array<NGramIndexer.NGramSentence>): Array<WordsIndexer.Word> {
        val resultSample = mutableMapOf<String, WordsIndexer.Word>()

        for (sample in initialSamples){
            val word = sample.sentence.replace("""\s+""","").trim()

            if (word.length<2){
                continue
            }

            for (i in 2.rangeTo(word.length)){

                val n2word = word.substring(i-2,i)

                if (!resultSample.containsKey(n2word)){
                    resultSample[n2word] = WordsIndexer.Word(n2word)
                }

                sample.twoNLinks.add(resultSample[n2word]!!)
                resultSample[n2word]!!.links.add(sample)

            }
        }
        return resultSample.values.toTypedArray()
    }

    private fun prepareSample(sample : ResSearch.Samples): String{
        return sample.value.toLowerCase().trim()
    }
}