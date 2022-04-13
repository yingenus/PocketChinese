package com.yingenus.pocketchinese.domain.entities.traning

import com.yingenus.pocketchinese.common.Language
import com.yingenus.pocketchinese.domain.dto.StudyWord


interface AnswerChecker {
    fun check(item: StudyWord, value:String):Boolean
}

object CheckerFactory{
    fun getAnswerChecker( language: Language) =
        when( language ){
            Language.PINYIN -> PinAnswerChecker()
            Language.CHINESE -> ChnAnswerChecker()
            Language.RUSSIAN -> RusAnswerChecker()
        }
}

class ChnAnswerChecker: AnswerChecker {
    override fun check(item: StudyWord, value: String): Boolean {
        return item.chinese.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}
class PinAnswerChecker: AnswerChecker {
    override fun check(item: StudyWord, value: String): Boolean {
        return item.pinyin.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}
class RusAnswerChecker: AnswerChecker {
    override fun check(item: StudyWord, value: String): Boolean {
        return item.translate.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}
class TrnAnswerChecker: AnswerChecker {
    override fun check(item: StudyWord, value: String): Boolean {
        return item.translate.toLowerCase().filterNot{ it==' '}.contentEquals(value.filterNot{ it==' '}.toLowerCase())
    }
}