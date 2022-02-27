package com.yingenus.pocketchinese.presenters

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.controller.fragment.CreateWordInterface
import com.yingenus.pocketchinese.domain.repository.DictionaryItemRepository
import com.yingenus.pocketchinese.model.database.pocketDB.Connection
import com.yingenus.pocketchinese.model.database.pocketDB.StudyList
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

open class CreateWordPresenter(view: CreateWordInterface, dictionaryItemRepository: DictionaryItemRepository): CreateEditWordPresenter(view,dictionaryItemRepository) {

    object Builder{

        fun getPresenter(dictionaryItem : com.yingenus.pocketchinese.domain.dto.DictionaryItem, view: CreateWordInterface, dictionaryItemRepository: DictionaryItemRepository): CreateWordPresenter{
            return CreateWordChar(dictionaryItem,view,dictionaryItemRepository)
        }

        fun getPresenter(studyListUUID: UUID, view: CreateWordInterface, dictionaryItemRepository: DictionaryItemRepository): CreateWordPresenter{
            return CreateWordStudyList(studyListUUID,view,dictionaryItemRepository)
        }

    }

    protected lateinit var selectedList: UUID

    override fun createWord(studyWord: StudyWord) {
        if (::selectedList.isInitialized){
            wordDAO.create(studyWord)

            val connection = Connection(selectedList, studyWord.uuid, view.getSelectedBlock() + 1)

            connectionDAO.create(connection)

            view.wordCreated()
        }
    }

    class CreateWordChar(val dictionaryItem: com.yingenus.pocketchinese.domain.dto.DictionaryItem, view: CreateWordInterface, dictionaryItemRepository: DictionaryItemRepository) : CreateWordPresenter(view,dictionaryItemRepository) {

        override fun onCreate(context: Context) {
            super.onCreate(context)

            loadLists()
            initSelectObserver()
            fillFields()
        }

        private fun fillFields(){

            view.setBlocks(0)

            view.setText(CreateWordInterface.FIELD.CHN, dictionaryItem.chinese)
            view.setText(CreateWordInterface.FIELD.PIN,dictionaryItem.pinyin)
            view.setText(CreateWordInterface.FIELD.TRN,dictionaryItem.translation.first())
        }

        private fun loadLists(){
            val singleObservable= Single.create<List<StudyList>>{ emitter ->
                emitter.onSuccess(listDAO.getAll())
            }.subscribeOn(Schedulers.io())
            singleObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{onSuccess->
                        view.setListOfStudyLists( onSuccess.map { it.name })
                    }
        }

        private fun initSelectObserver(){
            val observable= view.getSelectStudyListObservable()
                    .observeOn(Schedulers.io())
                    .map {
                        listDAO.get(it)}
                    .publish()
                    .autoConnect()



            observable
                    .map {
                    wordDAO.getAllIn(it!!)!!.groupBy({it.first},{it.second})?.keys?.max()?:0 }
                    .doOnError { Log.i("Create word error", it.message?:" un known")  }
                    .retry()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { onNext->
                        view.setBlocks(onNext)
                    }

            observable
                    .retry()
                    .subscribe { selectedList = it!!.uuid }
        }

    }

    class CreateWordStudyList(val studyListUUID: UUID, view: CreateWordInterface, dictionaryItemRepository : DictionaryItemRepository): CreateWordPresenter(view,dictionaryItemRepository){
        override fun onCreate(context: Context) {
            super.onCreate(context)

            selectedList = studyListUUID

            load()
        }

        private fun load(){
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
                    .map { list -> list.find { it.uuid.equals(studyListUUID) } }
                    .map { studyList ->
                        wordDAO.getAllIn(studyList!!)?.groupBy({it.first},{it.second})?.keys?.max()
                                ?: 0
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { onSuccess ->
                        view.setBlocks(onSuccess)
                    }
        }
    }

}