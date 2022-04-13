package com.yingenus.pocketchinese.domain.dto

class AddWordsConfig(
    val studyListId : Long,
    val romanization: Romanization,
    val mixWords : Boolean
) {
    enum class Romanization(){
        TONE_PINYIN, LETTER_PINYIN
    }
}