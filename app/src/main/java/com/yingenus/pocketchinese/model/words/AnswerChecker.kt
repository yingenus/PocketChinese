package com.yingenus.pocketchinese.model.words

import com.yingenus.pocketchinese.model.database.pocketDB.StudyWord

interface AnswerChecker {
    fun check(item: StudyWord, value:String):Boolean
}

class ChnAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.chinese.toLowerCase().filterNot{ it==' '}.contentEquals(value.toLowerCase())
    }
}
class PinAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.pinyin.toLowerCase().filterNot{ it==' '}.contentEquals(value.toLowerCase())
    }
}
class TrnAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.translate.toLowerCase().filterNot{ it==' '}.contentEquals(value.toLowerCase())
    }
}