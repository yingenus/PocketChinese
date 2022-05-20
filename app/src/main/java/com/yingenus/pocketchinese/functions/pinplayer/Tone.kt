package com.yingenus.pocketchinese.functions.pinplayer



data class Tone(val pinyin: String,val requiredPostfix: String, val sound:  String){

    companion object{
       // fun fromPinTone(tone : PinTone): Tone{
        //    if (tone.pinyin.trim().contains(" ")){
        //        val split = tone.pinyin.trim().split(" ")
        //        if (split.size >= 2){
         //           return Tone(split[0],split[1],tone.tone)
         //       }else{
        //            return Tone(tone.pinyin,"",tone.tone)
        //        }
        //    }else{
        //        return Tone(tone.pinyin,"",tone.tone)
        //    }
        //}

        fun fromTone(tone : com.yingenus.pocketchinese.domain.dto.Tone): Tone{
            if (tone.pinyin.trim().contains(" ")){
                val split = tone.pinyin.trim().split(" ")
                if (split.size >= 2){
                    return Tone(split[0],split[1],tone.numericalTone)
                }else{
                    return Tone(tone.pinyin,"",tone.numericalTone)
                }
            }else{
                return Tone(tone.pinyin,"",tone.numericalTone)
            }
        }
    }
}