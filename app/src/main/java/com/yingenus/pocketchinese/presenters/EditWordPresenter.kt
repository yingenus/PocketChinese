package com.yingenus.pocketchinese.presenters

import android.content.Context
import com.yingenus.pocketchinese.controller.fragment.CreateWordInterface
import com.yingenus.pocketchinese.model.database.pocketDB.Connection
import com.yingenus.pocketchinese.model.database.pocketDB.StudyList
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

import java.util.*

class EditWordPresenter(private val studyWordUUID: UUID, private val studyListUUID: UUID, view: CreateWordInterface)
    : CreateEditWordPresenter(view) {

    private var block:Int=0
    private lateinit var editableWord: StudyWord

    override fun createWord(studyWord: StudyWord) {
        editableWord.chinese = studyWord.chinese
        editableWord.pinyin = studyWord.pinyin
        editableWord.translate = studyWord.translate

        wordDAO.update(editableWord)

        if (block != view.getSelectedBlock()){
            val connection = Connection(editableWord.uuid, studyWordUUID, block)
            connectionDAO.update(connection)
        }

        view.wordCreated()
    }

    override fun onCreate(context: Context) {
        super.onCreate(context)
        loadStudyWord()
    }

    override fun initChnInputObserver() {
        //super.initChnInputObserver()
    }

    private fun loadStudyWord(){
        val singleObservable = Single.create<List<StudyList>> { emitter ->
            emitter.onSuccess(listDAO.getAll())
        }.subscribeOn(Schedulers.io())
        singleObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onSuccess ->
                    view.setListOfStudyLists(onSuccess.map { it.name })
                    view.setSelectStudyList(onSuccess.indexOf(onSuccess.find { it.uuid.equals(studyListUUID) }))
                }

        singleObservable
                .map { list ->
                    list.find { it.uuid.equals(studyListUUID) } }
                .map { studyList ->
                    wordDAO.getAllInSorted(studyList!!)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { onSuccess ->
                    view.setBlocks(onSuccess?.keys?.max() ?: 0)
                    for (key in onSuccess?.keys!!){

                        val st=onSuccess[key]?.find { it.uuid.equals(studyWordUUID) }
                        if (st!= null){
                            block=key
                            editableWord=st
                            setTexts()
                            break
                        }
                    }
                    super.initChnInputObserver()
                }

        //val observable = singleObservable.toObservable().publish().autoConnect()
    }

    private fun setTexts(){
        if (::editableWord.isInitialized){
            view.setText(CreateWordInterface.FIELD.CHN,editableWord.chinese)
            view.setText(CreateWordInterface.FIELD.PIN,editableWord.pinyin)
            view.setText(CreateWordInterface.FIELD.TRN,editableWord.translate)

            view.setSelectBlock(block)

        }
    }

}