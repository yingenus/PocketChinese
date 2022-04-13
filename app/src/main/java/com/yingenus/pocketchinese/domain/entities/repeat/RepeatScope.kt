package com.yingenus.pocketchinese.domain.entities.repeat

import com.yingenus.pocketchinese.domain.dto.KnownLevel

interface RepeatScope {
    fun repeatWindow(lvl: KnownLevel):Float
    fun nextDay(lvl: KnownLevel):Float
}