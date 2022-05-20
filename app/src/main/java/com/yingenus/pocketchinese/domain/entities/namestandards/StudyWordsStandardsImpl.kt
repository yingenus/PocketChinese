package com.yingenus.pocketchinese.domain.entities.namestandards

import com.yingenus.pocketchinese.common.Language
import java.util.*
import javax.inject.Inject

class StudyWordsStandardsImpl @Inject constructor(): StudyWordsStandards {
    companion object{
        private const val PINREGEX="[A-Za-zāáǎàīíǐìōóǒòēèěéūùǔúǚ]"
        private const val CHNREGEX="[\\p{script=Han}]"
        private const val TRNREGEX="[A-Za-zА-Яа-я]"
    }

    override fun isCorrectField(content : String, language: Language): Boolean =
        Regex("""^[${PINREGEX+ CHNREGEX+ TRNREGEX}\s]*?$""")
            .matches(content)

    override fun toStandards(content: String, language: Language): String {
        var afterSpace=false;
        return content.trim().toLowerCase(Locale.ROOT).filterNot { c->
            if (c==' '&& afterSpace) true
            else if (c==' '){
                afterSpace=true
                false
            }else{
                afterSpace=false
                false
            }
        }
    }
}