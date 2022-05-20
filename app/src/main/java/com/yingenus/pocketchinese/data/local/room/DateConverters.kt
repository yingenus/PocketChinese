package com.yingenus.pocketchinese.data.local.room

import androidx.room.TypeConverter
import java.util.*


class DateConverters {

    @TypeConverter
    fun fromTimestamp(time : Long): Date?{
        return if (time == null) null else Date(time)
    }

    @TypeConverter
    fun date2Timestamp(date : Date): Long?{
        return if (date == null) null else date.time
    }
}