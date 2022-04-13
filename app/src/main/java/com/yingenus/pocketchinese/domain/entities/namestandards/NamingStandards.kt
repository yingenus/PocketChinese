package com.yingenus.pocketchinese.domain.entities.namestandards

interface NamingStandards {
    fun isCorrectName(name : String): Boolean
}