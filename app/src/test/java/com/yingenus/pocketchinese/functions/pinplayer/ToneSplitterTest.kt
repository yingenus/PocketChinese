package com.yingenus.pocketchinese.functions.pinplayer


import org.junit.Test

import org.junit.Assert.*


class ToneSplitterTest {

    @Test
    fun split() {


        val line = "yòng (shí) &liàng kă  ^ chē , yī chē zuò wŭ shí 'rén "
        //val resultExpected = "yòngshíliàngkăchēyīchēzuòwŭshírén"
        val resultExpected = "yòngshíliàngkǎchēyīchēzuòwǔshírén"
        val result = ToneSplitter.prepare(line)
        assertArrayEquals(resultExpected.toCharArray(),result.toCharArray())
    }

    @Test
    fun prepare() {
        val line = "quán chū yú yī zhě quáng ，quán chū yú èr zhě quán ruò"
        val tones = listOf(
                Tone("quán","","quan6"),
                Tone("quán","chū","quan2"),
                Tone("chū","","chu2"),
                Tone("yú","","yi6"),
                Tone("yú","yī","yi2"),
                Tone("yī","","yi1"),
                Tone("zhě","","zh3"),
                Tone("quáng","","qiang2"),
                Tone("èr","","er4"),
                Tone("ruò","","ruo4")
                )

        val splitter = ToneSplitter(tones)

        val result = splitter.split(line)

        val expectedResult = listOf(
                Tone("quán","chū","quan2"),
                Tone("chū","","chu2"),
                Tone("yú","yī","yi2"),
                Tone("yī","","yi1"),
                Tone("zhě","","zh3"),
                Tone("quáng","","qiang2"),
                Tone("quán","chū","quan2"),
                Tone("chū","","chu2"),
                Tone("yú","","yi6"),
                Tone("èr","","er4"),
                Tone("zhě","","zh3"),
                Tone("quán","","quan6"),
                Tone("ruò","","ruo4")
        )

        assertEquals("size same", expectedResult.size,result.size)

        expectedResult.forEachIndexed { index, tone ->
            assertEquals(tone, result[index])
        }

    }
}