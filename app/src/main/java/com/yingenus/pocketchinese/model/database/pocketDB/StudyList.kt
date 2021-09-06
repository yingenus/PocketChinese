package com.yingenus.pocketchinese.model.database.pocketDB

import java.util.*

class StudyList(var name: String, items: Int = 0, val version: String? = null,
                val updateDate: Date = Date(), lastRepeat: Date = Date(),
                success: Int = 0, worst: Int = 0, val uuid: UUID = UUID.randomUUID(), var notifyUser: Boolean = true) {

    var items: Int = items
    var lastRepeat: Date = lastRepeat
    var success: Int = success
    var worst: Int = worst


    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other is StudyList){
            if(other.uuid == this.uuid) return true
        }
        return false
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
