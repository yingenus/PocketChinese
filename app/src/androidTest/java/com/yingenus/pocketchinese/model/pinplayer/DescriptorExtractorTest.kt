package com.yingenus.pocketchinese.model.pinplayer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
@MediumTest
class DescriptorExtractorTest {

    @Test
    fun getSoundDescriptor() {
        val context: Context = ApplicationProvider.getApplicationContext()

        val tone1 = "ao3"
        val tone2 = "shou3"
        val tone3 = "tong5"

        val toneFile1 = DescriptorExtractor.getSoundDescriptor(tone1,context)


        val toneFile2 = DescriptorExtractor.getSoundDescriptor(tone2,context)

        val toneAsset1 =  context.assets.openFd("pinsound/ao3.mp3")
        val toneAsset2 = context.assets.openFd("pinsound/shou3.mp3")


        assertEquals("toneFile1 start", toneFile1.assetFile.startOffset,toneAsset1.startOffset)
        assertEquals("toneFile1 length", toneFile1.assetFile.length,toneAsset1.length)
        assertEquals("toneFile2 start", toneFile2.assetFile.startOffset,toneAsset2.startOffset)
        assertEquals("toneFile2 length", toneFile2.assetFile.length,toneAsset2.length)

        assertThrows(IllegalArgumentException::class.java,{DescriptorExtractor.getSoundDescriptor(tone3,context)})

    }
}