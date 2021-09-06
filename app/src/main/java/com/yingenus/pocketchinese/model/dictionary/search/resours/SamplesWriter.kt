package com.yingenus.pocketchinese.model.dictionary.search.resours

import java.io.BufferedWriter
import java.io.Writer

open class SamplesWriter(writer : Writer) : BufferedWriter(writer){


    private companion object{
        const val sentIdsSeparator : String= "\t"
        const val idsSeparator : String = ","
        const val idWightSeparator : String = "_"


        fun sample2Line(sample: Sample): String =
                sample.sentence+ sentIdsSeparator +
                        sample.ids.mapIndexed { index: Int, id: Int -> getIdW(id, sample.wights[index]) }
                                .joinToString(separator = idsSeparator)

        fun getIdW(id : Int, weight : Int) = id.toString() + idWightSeparator + weight.toString()
    }


    fun writeSample( sample : Sample){
        super.write(sample2Line(sample))
        super.newLine()
    }

}