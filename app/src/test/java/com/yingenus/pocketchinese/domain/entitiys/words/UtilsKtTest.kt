package com.yingenus.pocketchinese.domain.entitiys.words

import junit.framework.TestCase

class UtilsKtTest : TestCase() {

    fun testCheckTrainStandards() {
        val textRG="тестовое слово без плохих символов"
        val textRB="тетсовоес; слово"
        val textRB2="тетсовоес() слово"
        assertEquals(true, checkTrainStandards(textRG))
        assertEquals(false, checkTrainStandards(textRB))
        assertEquals(false, checkTrainStandards(textRB2))
        val pinG="tǎe àf īt íd sǐh tìu hō kói"
        val pinB="tǎe àf(īt íd sǐh tìu) hō"
        assertEquals(true,checkTrainStandards(pinG))
        assertEquals(false,checkTrainStandards(pinB))
        val chnG="地方撒 二哥地方"
        val chnB="的各位的；辅导班"
        assertEquals(true,checkTrainStandards(chnG))
        assertEquals(false,checkTrainStandards(chnB))
    }

    fun testToTrainStandards() {
        val text="   тестовое  слово    без      плохих  символов    "
        val exp="тестовое слово без плохих символов"
        assertEquals(exp, toTrainStandards(text))
    }
}