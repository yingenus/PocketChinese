package com.yingenus.pocketchinese

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        //val
       // assertEquals("com.example.pocketchinese", appContext.packageName)
        //testXMLParser()
    }

    /*
    fun testXMLParser() {
        var parser=Xml.newPullParser()
        parser.setInput(instrumentationContext.assets.open("wordslist.xml"),null)
        var map= mapOf<String,String>("version" to "0.0.2","language" to "chn","translation" to "rus")

        assertEquals("getHeaders test fail",map, XMLSuggestWordsParser(parser).getHeaders())

        var list= mutableListOf<StudyList>()
        list.add(StudyList("EXAMPLE",50,"0.1"))
        list.add(StudyList("HSK 1",150,"0.1"))

        assertEquals("getHeaders test fail",list,XMLSuggestWordsParser(parser).getListsOfWords())

        var list2= mutableListOf<ChinCharacter>()
        list2.add(ChinCharacter("1","11","12",null, listOf(ChinCharacter("11","112","113"))))
        list2.add(ChinCharacter("2","21","22",null))
        list2.add(ChinCharacter("3","31","32",null, listOf(ChinCharacter("33","331","332"),
                ChinCharacter("333","3331",""))))

        parser=Xml.newPullParser()
        parser.setInput(instrumentationContext.assets.open("wordslist.xml"),null)

        assertEquals("getHeaders test fail",list2,XMLSuggestWordsParser(parser).getListOfWordIn("EXAMPLE"))

    }
    */
}