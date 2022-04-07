package com.yingenus.pocketchinese.domain.entities.dictionarysearch

interface Comparator {
    /*
    Определение разници мнжду солвами
     @param {word} - слово которое сранивается
     @param {comparable} - слово с которым сравнивается
     @returns {Int} - расстояние меду словами, чем больше том больше разница
        -1 - слова абсолютно различны
     */
    fun compere(word : String, comparable : String) : Int
}