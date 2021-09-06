package com.yingenus.pocketchinese.controller.fragment.train

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.view.pintext.PinTextView

interface TrainViewBuilder {
    fun build(inflater: LayoutInflater, parent: ViewGroup):TrainView
}

class TrnTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent), TextWatcher {
    class Builder : TrainViewBuilder {
        override fun build(inflater: LayoutInflater, parent: ViewGroup) =
                TrnTrainView(inflater, parent)
    }
    val MAX_COLUMS = 7
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text = chinText?.capitalize()
        super.secondText.text = pinText

        calcColumns(trnText!!,MAX_COLUMS)
        mAnswer=trnText.filterNot { it==' ' }
        mAnsLength=mAnswer.length
    }

    override fun getHiddenWord(): String {
        return mAnswer
    }

    override fun loadTrain(inflater: LayoutInflater?): PinTextView {
        val view = inflater!!.inflate(R.layout.trn_pin_text_view, null) as PinTextView
        return view
    }

}

class PinTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent),TextWatcher {
    class Builder:TrainViewBuilder{
        override fun build(inflater: LayoutInflater, parent: ViewGroup)=PinTrainView(inflater, parent)
    }
    val MAX_COLUMS=7
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text=chinText?.capitalize()
        super.secondText.text=trnText
        calcColumns(pinText!!,MAX_COLUMS)
        mAnswer=pinText.filterNot { it==' ' }
        mAnsLength=mAnswer.length

    }

    override fun getHiddenWord(): String {
        return mAnswer
    }

    override fun loadTrain(inflater: LayoutInflater?): PinTextView {
        val view=inflater!!.inflate(R.layout.trn_pin_text_view,null) as PinTextView
        return view
    }

}

class ChnTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent){
    class Builder:TrainViewBuilder{
        override fun build(inflater: LayoutInflater, parent: ViewGroup)=ChnTrainView(inflater,parent)
    }
    private val MAX_COLUMNS=5
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text=trnText?.capitalize()
        super.secondText.text=pinText
        super.calcColumns(chinText,MAX_COLUMNS)
        mAnswer=chinText!!.filterNot { it==' ' }
        mAnsLength=mAnswer.length

    }

    override fun getHiddenWord(): String {
        return mAnswer
    }


    override fun loadTrain(inflater: LayoutInflater?): PinTextView {
        val view=inflater!!.inflate(R.layout.chn_pin_text_view,null,false)
        return view as PinTextView
    }
}