package com.yingenus.pocketchinese.model.pinplayer

import com.yingenus.pocketchinese.model.database.dictionaryDB.PinTone

data class Tone(val pinyin: String,val requiredPostfix: String, val sound:  String){

    companion object{
        fun fromPinTone(tone : PinTone): Tone{
            if (tone.pinyin.trim().contains(" ")){
                val split = tone.pinyin.trim().split(" ")
                if (split.size >= 2){
                    return Tone(split[0],split[1],tone.tone)
                }else{
                    return Tone(tone.pinyin,"",tone.tone)
                }
            }else{
                return Tone(tone.pinyin,"",tone.tone)
            }
        }
    }
}