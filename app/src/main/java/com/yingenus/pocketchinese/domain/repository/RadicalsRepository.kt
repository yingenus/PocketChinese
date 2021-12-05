package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.ZiChar

interface RadicalsRepository {
    fun getRadicals(): Map<Int,List<String>>
    fun getCharacters(radical : String): Map<Int,List<ZiChar>>
}