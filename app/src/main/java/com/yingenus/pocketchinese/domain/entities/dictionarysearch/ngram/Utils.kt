package com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram

 fun split(str : String, splitLength : Int): List<String>{
     if (str.length <= splitLength) return listOf(str)

     val splited = mutableListOf<String>()

     for (i in 0..str.length - 3){
         splited.add(str.substring(i,i+3))
     }
     return splited
 }