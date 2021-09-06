package com.yingenus.pocketchinese.model

import java.lang.IllegalArgumentException

data class RepeatType private constructor(val ignoreCHN :Boolean,
                      val ignorePIN :Boolean,
                      val ignoreTRN :Boolean){

    companion object{
        val default : RepeatType = RepeatType(false,false,false)
        operator fun invoke( ignoreCHN :Boolean, ignorePIN :Boolean,
                     ignoreTRN :Boolean): RepeatType{
            if ( ignoreCHN&&ignorePIN&&ignoreTRN )
                throw IllegalArgumentException("at list one argument should be false ")
            return RepeatType(ignoreCHN,ignorePIN,ignoreTRN)
        }
    }
}