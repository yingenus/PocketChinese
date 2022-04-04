package com.yingenus.pocketchinese.domain.entitiys.words

import com.yingenus.pocketchinese.domain.entitiys.database.pocketDB.StudyWord

interface AnswerChecker {
    fun check(item: StudyWord, value:String):Boolean
}

class ChnAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.chinese.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}
class PinAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.pinyin.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}
class TrnAnswerChecker:AnswerChecker{
    override fun check(item: StudyWord, value: String): Boolean {
        return item.translate.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}