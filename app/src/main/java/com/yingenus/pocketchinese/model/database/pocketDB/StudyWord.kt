package com.yingenus.pocketchinese.model.database.pocketDB

import java.util.*
import kotlin.math.max
import kotlin.math.min

class StudyWord(var chinese: String,              var pinyin: String,               var translate: String,
                var chnRepeat: Int = 0,           var pinRepeat: Int = 0,           var trnRepeat: Int = 0,
                    chnLevel: Int = 0,                pinLevel: Int = 0,                trnLevel: Int = 0,
                var chnRepState: Int = 0,         var pinRepState: Int = 0,         var trnRepState: Int = 0,
                var chnLastRepeat: Date = Date(), var pinLastRepeat: Date = Date(), var trnLastRepeat: Date = Date(),
                val createDate: Date = Date(), val uuid: UUID = UUID.randomUUID()) {

    var chnLevel: Int = chnLevel
        set(i){
            field= max(0, min(i,10))
        }
    var pinLevel: Int = pinLevel
        set(i){
            field= max(0, min(i,10))
        }
    var trnLevel: Int = trnLevel
        set(i){
            field= max(0, min(i,10))
        }


    fun getLevel():Int{
        return min(pinLevel, min(chnLevel,trnLevel))
    }


    override fun equals(other: Any?): Boolean {

        if (this === other) return true
        if (other is StudyWord){
            if (other.uuid == this.uuid) return true
        }
        return false
    }

    override fun hashCode(): Int {
        return this.uuid.hashCode()
    }
}