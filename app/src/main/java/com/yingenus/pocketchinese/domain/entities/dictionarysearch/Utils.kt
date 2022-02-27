package com.yingenus.pocketchinese.domain.entities.dictionarysearch

fun witchLanguage(value: String)= when {
    value.contains(Regex("""[\p{script=Han}]""")) -> Language.CHINESE
    value.contains(Regex("""[А-яа-я]""")) -> Language.RUSSIAN
    else -> Language.PINYIN
}