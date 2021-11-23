package com.yingenus.pocketchinese.domain.repository

import com.yingenus.pocketchinese.domain.dto.Tone

interface ToneRepository {
    fun getAllTone(): List<Tone>
}