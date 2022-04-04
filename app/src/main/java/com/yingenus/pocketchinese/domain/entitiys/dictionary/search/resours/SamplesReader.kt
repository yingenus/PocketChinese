package com.yingenus.pocketchinese.domain.entitiys.dictionary.search.resours

import java.io.*

open class Sample(val sentence : String, val ids : List<Int>, val wights: List<Int>){
    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other is Sample){
            if (other.sentence == this.sentence)
                return true
        }
        return false
    }

    override fun hashCode(): Int {
        return sentence.hashCode()
    }

    override fun toString(): String {
        return "${sentence} with ${ids.size} ids"
    }
}

open class SamplesReader( reader : Reader) : BufferedReader(reader){

    private companion object{
        const val sentIdsSeparator : String= "\t"
        const val idsSeparator : String = ","
        const val idWightSeparator : String = "_"


        fun isSampleLine(line : String): Boolean = true
            //Regex("""^.+\t[0-9,_]+$""",RegexOption.DOT_MATCHES_ALL).matches(line)


        fun getSentence(line : String): String =
            line.substring(0 until line.indexOf(sentIdsSeparator))


        fun getIds(line: String): List<Pair<Int,Int>>{
            val rafIds = line.substring( (line.indexOf(sentIdsSeparator)+1) until line.length)

            return rafIds.split(idsSeparator).map { idw : CharSequence ->
                val id = idw.substring(0 until idw.indexOf(idWightSeparator))
                val wight = idw.substring( idw.indexOf(idWightSeparator)+1 until idw.length)
                Pair(id.toInt(),wight.toInt())
            }
        }

    }

    fun readSample() : Sample?{
        val line = findSampleLine() ?: return null

        val sentence = getSentence(line)
        val idws = getIds(line)

        return Sample(sentence, idws.map { it.first }, idws.map { it.second })
    }

    private fun findSampleLine(): String?{
        var line : String?
        do {
            line = super.readLine()
        }while ( line != null && !isSampleLine(line))
        return line
    }
}