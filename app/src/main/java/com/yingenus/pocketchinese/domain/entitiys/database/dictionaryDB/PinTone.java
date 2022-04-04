package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable( tableName = "py_variants")
public class PinTone {

    public static final String PIN_FIELD_NAME = "py";
    public static final String TONE_FIELD_NAME = "tone";

    @DatabaseField(canBeNull = false, columnName =PIN_FIELD_NAME)
    String pinyin;
    @DatabaseField(canBeNull = false, columnName =TONE_FIELD_NAME)
    String tone;

    public String getPinyin() {
        return pinyin;
    }

    public String getTone() {
        return tone;
    }
}
