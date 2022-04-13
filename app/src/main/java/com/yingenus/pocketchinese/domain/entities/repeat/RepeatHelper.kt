package com.yingenus.pocketchinese.domain.entities.repeat

import com.yingenus.pocketchinese.domain.dto.KnownLevel
import java.util.*

interface RepeatHelper {
    fun nextRepeat(lastRepeat: Date, lvl: KnownLevel): Date
    fun howExpired(lastRepeat: Date, lvl: KnownLevel): Expired
    fun canAccept(lastRepeat: Date, lvl: KnownLevel): Boolean
}