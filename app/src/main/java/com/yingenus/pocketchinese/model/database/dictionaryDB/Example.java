package com.yingenus.pocketchinese.model.database.dictionaryDB;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "examples")
public class Example {
    private static final String SEPARATOR = "__,__";

    public static final String ID_FIELD_NAME = "id";

    public static final String WORD_FIELD_NAME="chinese_word";
    public static final String PINYIN_FIELD_NAME="pinyin";
    public static final String TRANSLATION_FIELD_NAME="translation";
    public static final String ENTRY_WORDS_FIELD_NAME="entry_words";

    @DatabaseField(generatedId = true, canBeNull = false, columnName = ID_FIELD_NAME)
    int id;
    @DatabaseField(canBeNull = false, columnName = WORD_FIELD_NAME)
    String chinese;
    @DatabaseField(canBeNull = false, columnName = PINYIN_FIELD_NAME)
    String pinyin;
    @DatabaseField(canBeNull = false, columnName = TRANSLATION_FIELD_NAME)
    String translation;
    @DatabaseField(columnName = ENTRY_WORDS_FIELD_NAME)
    String entryWords;

    public Example(){

    }

    public Example(String chinese, String pinyin, String translation, String entryWords){
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.translation = translation;
        this.entryWords = entryWords;
    }

    public Example(String chinese, String pinyin, String translation, String[] entryWords){
        this(chinese,pinyin,translation,UtilsKt.array2String(entryWords));
    }

    public int getId() {
        return id;
    }

    public String getChinese() {
        return chinese;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getTranslation() {
        return translation;
    }

    public void setEntryWords(String[] entryWords) {
        this.entryWords = UtilsKt.array2String(entryWords);
    }

    public String[] getEntryWords(){
        if (entryWords!=null){
            return UtilsKt.string2Array(entryWords);
        }else {
            return new String[0];
        }
    }



}
