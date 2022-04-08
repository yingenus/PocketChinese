package com.yingenus.pocketchinese.functions.pinplayer

import com.yingenus.pocketchinese.domain.repository.ToneRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.ref.SoftReference

class ToneSplitter constructor(tones : List<Tone>) {

    private val tonesList: List<Tone> = tones.sortedByDescending { if (it.requiredPostfix != "") 1 else 0}

    companion object{
        @Volatile private var splitterRef : SoftReference<ToneSplitter?> = SoftReference(null)

        fun splitter(sent : String, toneRepository: ToneRepository): List<Tone>{
            var splitter = splitterRef.get()
            if (splitter == null){
                splitter = buildSplitter(toneRepository)
                splitterRef = SoftReference(splitter)
            }
            return splitter.split(sent)
        }

        fun rxSplitter(sent : String, toneRepository: ToneRepository): Observable<Tone>{
            return Observable.defer { Observable.fromIterable(splitter(sent,toneRepository)) }.subscribeOn(Schedulers.io())
        }

        private fun buildSplitter(toneRepository: ToneRepository): ToneSplitter{
            //val helper = DictionaryDBOpenManger.getHelper(context,DictionaryDBHelper::class.java)
           // val toneDb = ToneDaoImpl(helper.connectionSource)
            return ToneSplitter(toneRepository.getAllTone().map { Tone.fromTone(it) })
        }


        private fun toStandard(sent : String) = sent
                .replace('ă','ǎ')
                .replace('ŭ','ǔ')
                .replace('ŏ','ǒ')
                .replace('ĭ','ǐ')
                .replace('ĕ','ě')


        fun prepare(sent  : String) = toStandard(sent).toLowerCase().replace(Regex("""[^a-zāáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ]+"""),"")

    }

    fun split(sent : String): List<Tone>{
        return splitRecurs(prepare(sent)).result
    }



    private fun splitRecurs(sent : String): SplitResult{
        val candidates : MutableList<Tone> = mutableListOf()

        var entrees : List<Tone> = tonesList
        var line = ""

        for ( i in 0 until sent.length){
            line += sent[i]

            val newEntry: MutableList<Tone> = mutableListOf()

            entrees.forEach {entry ->
                if (entry.pinyin.contains(line)){
                    newEntry.add(entry)
                    if (entry.pinyin == line){
                        if (entry.requiredPostfix == ""){
                            candidates.add(entry)
                        }else if (sent.length >= entry.pinyin.length+entry.requiredPostfix.length){
                            val recPost = sent.substring(entry.pinyin.length, entry.pinyin.length+entry.requiredPostfix.length)
                            if (recPost == entry.requiredPostfix ) candidates.add(entry)
                        }
                    }
                }
            }
            entrees = newEntry

            if (entrees.isEmpty()) break

        }

        val result = mutableListOf( SplitResult(sent.length, emptyList()))

        result.addAll(candidates.map {
                    val result = splitRecurs(sent.substring(it.pinyin.length,sent.length))
                    SplitResult(result.missed, mutableListOf(it)
                            .apply { addAll(result.result) })
                })

        return result.reduce { acc, item ->
            if (item.missed < acc.missed)
                item
            else
                acc
        }
    }

    class SplitResult(val missed : Int, val result : List<Tone>)

}