package com.yingenus.pocketchinese.presentation.views.creteeditword

import android.content.Context
import android.util.Log
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.Connection
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyList
import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

open class CreateWordPresenter(
    view: CreateWordInterface,
    chinCharRepository: ChinCharRepository): CreateEditWordPresenter(view,chinCharRepository) {

    class Factory @Inject constructor(private val chinCharRepository: ChinCharRepository){
        fun create(chinChar : com.yingenus.pocketchinese.domain.dto.ChinChar, view: CreateWordInterface): CreateWordPresenter{
            return Builder.getPresenter(chinChar,view, chinCharRepository)
        }
        fun create(studyListUUID: UUID, view: CreateWordInterface): CreateWordPresenter{
            return Builder.getPresenter(studyListUUID,view, chinCharRepository)
        }
    }

    object Builder{

        fun getPresenter(chinChar : com.yingenus.pocketchinese.domain.dto.ChinChar, view: CreateWordInterface, chinCharRepository: ChinCharRepository): CreateWordPresenter {
            return CreateWordChar(chinChar,view,chinCharRepository)
        }

        fun getPresenter(studyListUUID: UUID, view: CreateWordInterface, chinCharRepository: ChinCharRepository): CreateWordPresenter {
            return CreateWordStudyList(studyListUUID,view,chinCharRepository)
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

    class CreateWordChar (
        val chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar,
        view: CreateWordInterface,
        chinCharRepository: ChinCharRepository) : CreateWordPresenter(view,chinCharRepository) {

        override fun onCreate(context: Context) {
            super.onCreate(context)

            loadLists()
            initSelectObserver()
            fillFields()
        }

        private fun fillFields(){

            view.setBlocks(0)

            view.setText(CreateWordInterface.FIELD.CHN, chinChar.chinese)
            view.setText(CreateWordInterface.FIELD.PIN,chinChar.pinyin)
            view.setText(CreateWordInterface.FIELD.TRN,chinChar.translation.first())
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

    class CreateWordStudyList(
        val studyListUUID: UUID,
        view: CreateWordInterface,
        chinCharRepository : ChinCharRepository): CreateWordPresenter(view,chinCharRepository){

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