package com.yingenus.pocketchinese.presenters

import android.content.Context
import com.yingenus.pocketchinese.controller.fragment.CreateWordInterface
import com.yingenus.pocketchinese.domain.repository.ChinCharRepository
import com.yingenus.pocketchinese.model.database.DictionaryDBOpenManger
import com.yingenus.pocketchinese.model.database.PocketDBOpenManger
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinChar
import com.yingenus.pocketchinese.model.database.dictionaryDB.ChinCharDaoImpl
import com.yingenus.pocketchinese.model.database.dictionaryDB.DictionaryDBHelper
import com.yingenus.pocketchinese.model.words.checkTrainStandards
import com.yingenus.pocketchinese.model.database.pocketDB.ConnectionDAO
import com.yingenus.pocketchinese.model.database.pocketDB.StudyListDAO
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWordDAO
import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord
import com.yingenus.pocketchinese.model.words.toTrainStandards
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

open class CreateEditWordPresenter(val view: CreateWordInterface, val chinCharRepository: ChinCharRepository) {

    //private lateinit var chinDao: ChinCharDaoImpl

    protected lateinit var wordDAO: StudyWordDAO
    protected lateinit var listDAO: StudyListDAO
    protected lateinit var connectionDAO: ConnectionDAO

    open fun onCreateWordClicked(){
        if (checkStandards()){
            val chn = view.getText(CreateWordInterface.FIELD.CHN)
            val pin = view.getText(CreateWordInterface.FIELD.PIN)
            val trn = view.getText(CreateWordInterface.FIELD.TRN)

            val studyWord= StudyWord(toTrainStandards(chn), toTrainStandards(pin), toTrainStandards(trn))

            createWord(studyWord)
        }
    }

    open fun onCreate(context: Context){
        val sqlDb = PocketDBOpenManger.getHelper(context).writableDatabase
        //val connection =
        //        DictionaryDBOpenManger.getHelper(context,DictionaryDBHelper::class.java).connectionSource

        //chinDao = ChinCharDaoImpl(connection)

        wordDAO = StudyWordDAO(sqlDb)
        listDAO = StudyListDAO(sqlDb)
        connectionDAO = ConnectionDAO(sqlDb)

        initChnInputObserver()
    }

    open fun afterTextChanged(field : CreateWordInterface.FIELD){
        checkStandard(field)
    }

    open fun chnLostFocus(){

    }

    open fun onDestroy(){

        wordDAO.finish()
        listDAO.finish()
        connectionDAO.finish()

        PocketDBOpenManger.releaseHelper()
        //DictionaryDBOpenManger.releaseHelper()
    }

    open fun initChnInputObserver(){
        val observable = view.getChnInputObserver()

        observable
                .observeOn(Schedulers.io())
                .map { text ->
                    val result= chinCharRepository.findByChinese(text)
                    result.first{ it.chinese == text }
                    //chinDao.findChinCharInColumn(text, ChinChar.WORD_FIELD_NAME).first { it.chinese.equals(text) }
                    }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { onError -> toDefaultHint() }
                .retry()
                .subscribe { onNext ->  chinCharFounded( onNext )}

    }

    open fun createWord(studyWord: StudyWord){

    }

    protected fun toDefaultHint(){
        view.setText(CreateWordInterface.FIELD.PIN,"")
        checkStandards()
    }

    protected fun chinCharFounded(chinChar: com.yingenus.pocketchinese.domain.dto.ChinChar){
        view.setText(CreateWordInterface.FIELD.PIN, chinChar.pinyin)
        view.setText(CreateWordInterface.FIELD.TRN,chinChar.translation.first())
        checkStandards()
    }

    protected  fun findSimilar( text : String){
        val observable= Single.create<StudyWord> {  wordDAO.get(text)}
        observable.observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ onNext-> view.analogFounded(onNext) },{onError-> })
    }

    open fun checkStandards()=
        checkStandard(CreateWordInterface.FIELD.CHN)&&
        checkStandard(CreateWordInterface.FIELD.PIN)&&
        checkStandard(CreateWordInterface.FIELD.TRN)


    open fun checkStandard(field : CreateWordInterface.FIELD):Boolean{
        val length = view.getMaxCharsLength(field)
        val text = view.getText(field)

        val isEmp = text.isBlank() ||text.isEmpty()
        view.showEmtFieldMes(field,isEmp)

        val isInv = !checkTrainStandards(text.toString())
        view.showInvalCharsMes(field,isInv)

        val isMax = text.length>length
        view.showMaxCharsMes(field,isMax)

        return ( !isEmp )&&( !isInv )&&( !isMax )
    }



}