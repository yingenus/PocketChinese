package com.yingenus.pocketchinese.domain.entities.suggestwords

import com.yingenus.pocketchinese.domain.dto.SuggestWord

object SuggestWordsUtils {
    fun suggestWordTranslationToRepeatStandards(word : SuggestWord): String{
        return if(word.translation.contains(Regex("""[,;]"""))) {
            val separator = word.translation.indexOfFirst { it == ',' || it == ';' }
             word.translation.substring(0, separator)
        }else word.translation
    }
}