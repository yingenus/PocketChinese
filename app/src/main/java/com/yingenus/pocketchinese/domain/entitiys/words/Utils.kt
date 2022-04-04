package com.yingenus.pocketchinese.domain.entitiys.words

import java.util.*


private const val PINREGEX="[A-Za-zāáǎàīíǐìōóǒòēèěéūùǔúǚ]"
private const val CHNREGEX="[\\p{script=Han}]"
private const val TRNREGEX="[A-Za-zА-Яа-я]"
fun checkTrainStandards(text: String)
        =Regex("""^[${PINREGEX+ CHNREGEX+ TRNREGEX}\s]*?$""").matches(text)

fun toTrainStandards(text: String): String {
    var afterSpace=false;
    return text.trim().toLowerCase(Locale.ROOT).filterNot { c->
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