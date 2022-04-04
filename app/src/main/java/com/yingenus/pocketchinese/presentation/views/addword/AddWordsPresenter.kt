package com.yingenus.pocketchinese.presentation.views.addword

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.controller.logErrorMes
import com.yingenus.pocketchinese.domain.entitiys.database.PocketDBOpenManger
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.Connection
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.ConnectionDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyListDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWordDAO
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import com.yingenus.pocketchinese.domain.entitiys.words.suggestwords.JSONObjects
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.RuntimeException
import kotlin.random.Random

class AddWordsPresenter(val view : AddWordInterface) {

    private lateinit var wordsDao : StudyWordDAO
    private lateinit var connectDao : ConnectionDAO
    private lateinit var listsDAO: StudyListDAO

    private val listsName : List<String> by lazy { listsDAO.getAll()?.map { it.name }?: listOf() }

    private lateinit var insertedWords : List<JSONObjects.Word>

    private lateinit var textPublisher: PublishSubject<String>
    private lateinit var listSelectedPublisher : PublishSubject<String>

    fun onCreate( context : Context, words : List<JSONObjects.Word>){
        insertedWords = words

        val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase

        wordsDao = StudyWordDAO(sqlDb)
        connectDao = ConnectionDAO(sqlDb)
        listsDAO = StudyListDAO(sqlDb)


        textPublisher = PublishSubject.create()
        listSelectedPublisher = PublishSubject.create()

        pasteLists()
        initTextObserver()
        initListSelectedObserver()
    }

    fun onDestroy(){
        PocketDBOpenManger.releaseHelper()
    }

    fun onEditText( text : String){
        textPublisher.onNext(text)
    }

    fun onListSelected(){
        listSelectedPublisher.onNext(view.getSelectedList())
    }

    fun insertWords(){
        InsertHelper().insertWords()
    }

    private fun pasteLists(){
        view.setUserLists(listsName)
    }


    private fun initTextObserver(){
        textPublisher.subscribe { checkStandard() }
    }

    private fun initListSelectedObserver(){
        listSelectedPublisher
                .filter { view.getSelectedParams().existBlock }
                .observeOn(Schedulers.io())
                .map {
                    listsDAO.get(it)
                }
                .map {
                    connectDao.getAll(it!!.uuid) ?: emptyList()
                }
                .observeOn(Schedulers.computation())
                .map { if (it.isEmpty()) 0 else it.groupBy { it.block }.keys.sorted().last()  }
                .doOnError { Log.e("selection obs", it.message+"\n"+it.stackTrace.joinToString(separator = "\n")) }
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onNext -> view.setBlocksCount( onNext) }
    }

    private fun checkStandard():Boolean{
        val text = view.getNewListName()

        val isEmp = text.isBlank() ||text.isEmpty()
        view.showNameError(AddWordInterface.CreateError.EMPTY_NAME, isEmp)

        val isExist = listsName.contains(text)
        view.showNameError(AddWordInterface.CreateError.NAME_BUSY, isExist)

        return ( !isEmp )&&( !isExist )
    }


    private inner class InsertHelper{
        fun insertWords(){
            val params =view.getSelectedParams()

            if ((!params.existBlock && !checkStandard()) ||(params.existBlock && !checkList())){
                return
            }
            view.startInsert()

            val list =
                    if (params.existBlock)
                        findList(view.getSelectedList())
                    else
                        createList(view.getNewListName())

            val startBlock = if(params.startedBlock == 0) 1 else params.startedBlock

            var observable =
                    if (params.mixWords)
                        Observable.defer { Observable.fromIterable( getRandomize(insertedWords)) }
                    else
                        Observable.fromIterable(insertedWords)

            observable = observable.subscribeOn(Schedulers.computation())

            if (params.pinType == AddWordInterface.PinType.SIMPLIFIED)
                observable = observable.map { it.simplify2PIN() }

            var resultObservable = observable
                    .scan(InsertPosition(0,0,null), ({ last: InsertPosition?, item: JSONObjects.Word ->
                            if (last?.item == null)
                                InsertPosition(startBlock, 1, item)
                            else if (last.count == params.wordsInBlock)
                                InsertPosition(last.block + 1, 0, item)
                            else
                                InsertPosition(last.block, last.count + 1, item) }))
                    .filter { it.item != null }.observeOn(Schedulers.io())

            if (params.splitToBlocks){
                 resultObservable = resultObservable.doOnNext {

                     val word = it!!.item!!.toStudyWord()
                     val connection = Connection(list!!.uuid, word.uuid, it.block)

                     wordsDao.create(word)
                     connectDao.create(connection)
                     Log.d("add to db", word.chinese)
                    }
            }else{
                 resultObservable = resultObservable.doOnNext {
                     val word = it!!.item!!.toStudyWord()
                     val connection = Connection(list!!.uuid, word.uuid, startBlock)

                     wordsDao.create(word)
                     connectDao.create(connection)
                     Log.d("add to db", word.chinese)
                 }
            }

            resultObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { view.finishInsert(true) }
                    .doOnError { view.finishInsert(false) }
                    .subscribe({onNext -> },{onError -> Log.e("insert words", onError.logErrorMes())})


        }

        private fun findList(name : String) =
                listsDAO.get(name)

        private fun createList(name : String): StudyList {
            val studyList = StudyList(name)
            listsDAO.create(studyList)
            return listsDAO.get(name)!!
        }

        private fun getRandomize(list : List<JSONObjects.Word>) : List<JSONObjects.Word>{
            val randomized = Array<JSONObjects.Word?>(list.size) { null}

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

        private fun checkList():Boolean{
            val name = view.getSelectedList()
            if (name.isEmpty() || name.isBlank()){
                view.listNotSelected(true)
                return false
            }else{
                view.listNotSelected(false)
                return true
            }
        }

        private fun JSONObjects.Word.simplify2PIN()=
                JSONObjects.Word( this.word, this.pinyin.replace(Regex("""[āáǎà]"""),"a")
                                .replace(Regex("""[īíǐì]"""),"i")
                                .replace(Regex("""[ōóǒò]"""),"o")
                                .replace(Regex("""[ēèěé]"""),"e")
                                .replace(Regex("""[ūùǔú]"""),"u")
                                .replace(Regex("""[ǚ]"""),"v"),
                            this.translation,
                            this.examples
                        )


        private fun JSONObjects.Word.toStudyWord(): StudyWord {
            val pin = Regex("""[A-Za-zāáǎàīíǐìōóǒòēèěéūùǔúǚ\s]+""").findAll(pinyin).first().value.replace(" ","")
            val trnsl = Regex("""[A-Za-zА-Яа-я\s]+""").findAll(translation).first().value
            return StudyWord(word, pin, trnsl)
        }

    }

    private data class InsertPosition(val block : Int, val count : Int, val item : JSONObjects.Word?)

}