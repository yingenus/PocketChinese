package com.yingenus.pocketchinese.controller.fragment.train

import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.yingenus.pocketchinese.R
import com.yingenus.pocketchinese.view.pintext.PinTextView

interface TrainViewBuilder {
    fun build(inflater: LayoutInflater, parent: ViewGroup):TrainView
}

class TrnTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent){
    class Builder : TrainViewBuilder {
        override fun build(inflater: LayoutInflater, parent: ViewGroup) =
                TrnTrainView(inflater, parent)
    }
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text = chinText?.capitalize()
        super.secondText.text = pinText

        inputLength(trnText!!)
        mAnswer=trnText
        mAnsLength=mAnswer.length
    }

    override fun getHiddenWord(): String {
        return mAnswer
    }

    override fun getEditHint(context: Context?): String {
        return context!!.getString(R.string.my_language)
    }
}

class PinTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent){
    class Builder:TrainViewBuilder{
        override fun build(inflater: LayoutInflater, parent: ViewGroup)=PinTrainView(inflater, parent)
    }
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text=chinText?.capitalize()
        super.secondText.text=trnText
        inputLength(pinText!!)
        mAnswer=pinText
        mAnsLength=mAnswer.length

    }

    override fun getHiddenWord(): String {
        return mAnswer
    }

    override fun getEditHint(context: Context?): String {
        return context!!.getString(R.string.pinyin)
    }
}

class ChnTrainView(inflater: LayoutInflater, parent: ViewGroup):TrainView(inflater,parent){
    class Builder:TrainViewBuilder{
        override fun build(inflater: LayoutInflater, parent: ViewGroup)=ChnTrainView(inflater,parent)
    }
    private var mAnsLength=0
    private var mAnswer=""

    override fun bindItem(chinText: String?, pinText: String?, trnText: String?) {
        super.mainText.text=trnText?.capitalize()
        super.secondText.text=pinText
        super.inputLength(chinText!!)
        mAnswer=chinText
        mAnsLength=mAnswer.length

    }

    override fun getHiddenWord(): String {
        return mAnswer
    }

    override fun getEditHint(context: Context?): String {
        return context!!.getString(R.string.chinese)
    }
}