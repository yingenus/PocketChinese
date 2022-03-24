package com.yingenus.pocketchinese.domain.entities.dictionarysearch.ngram

 fun split(str : String, splitLength : Int): List<String>{
     if (str.length <= splitLength) return listOf(str)

     val splited = mutableListOf<String>()

     for (i in 0..str.length - splitLength){
         splited.add(str.substring(i,i+splitLength))
     }
     return splited
 }