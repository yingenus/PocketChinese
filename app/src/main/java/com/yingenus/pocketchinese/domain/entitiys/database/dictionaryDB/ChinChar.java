package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable( tableName = "words")
public class ChinChar implements Serializable{
    public static final String ID_FIELD_NAME = "ID";
    public static final String WORD_FIELD_NAME="chinese_word";
    public static final String PINYIN_FIELD_NAME="pinyin";
    public static final String TRANSLATION_FIELD_NAME="translation";
    public static final String TAGS_FIELD_NAME="tags";
    public static final String TRANSLATION_EXAMPLES_FIELD_NAME="trn_examples";

    @DatabaseField(id = true, canBeNull = false, columnName = ID_FIELD_NAME)
    int id;

    @DatabaseField(canBeNull = false, columnName = WORD_FIELD_NAME)
    String chinese;
    @DatabaseField(canBeNull = false, columnName = PINYIN_FIELD_NAME)
    String pinyin;
    @DatabaseField(canBeNull = false, columnName = TRANSLATION_FIELD_NAME)
    String translation;
    @DatabaseField(canBeNull = true, columnName = TRANSLATION_EXAMPLES_FIELD_NAME)
    String translationExms;

    @DatabaseField(canBeNull = true, columnName = TAGS_FIELD_NAME)
    String tags;

    public ChinChar(){

    }

    public ChinChar(String chinese, String pinyin, String translation, String tags, String translationExms){
        this.chinese = chinese;
        this.pinyin = pinyin;
        this.translation = translation;
        this.tags = tags;
        this.translationExms = translationExms;
    }

    public void setTranslationExms(String[] translationExms){
        this.translationExms = UtilsKt.array2String(translationExms);
    }

    public void setTranslation(String[] translation) {
        this.translation = UtilsKt.array2String(translation);
    }

    public int getId(){
        return id;
    }

    public String[] getTranslations() {
        if (translation != null) return UtilsKt.string2Array(translation);
        else return new String[0];
    }

    public String[] getTranslationExms() {
        if (translationExms != null) return UtilsKt.string2Array(translationExms);
        else return new String[0];
    }

    public String getGeneralTag(){
        if (tags != null){
            String[] tagsArr = UtilsKt.string2Array(tags);
            return tagsArr[0];
        }
        else
            return "";
    }

    public String getChinese(){
        return chinese;
    }

    public String getPinyin(){
        return pinyin;
    }

    public void setGeneralTag(String tag){
        String[] tagsArr = new String[getTranslations().length+1];
        if (tags != null){
            tagsArr = UtilsKt.string2Array(tags);
        }
        tagsArr[0] = tag;
        tags = UtilsKt.array2String(tagsArr);
    }

    public String[] getSpecialTags(){

        String[] ans = new String[getTranslations().length];

        if (tags != null){
            String[] tagsArr = UtilsKt.string2Array(tags);
            if (tagsArr.length > 1){
                for(int i = 1; i< tagsArr.length ; i++){
                    ans[i-1] = tagsArr[i];
                }
            }
        }
        return ans;
    }

    public void setSpecialTag(String tag, int position){
        int transLength = getTranslations().length;

        if(position > transLength-1){
            throw new IndexOutOfBoundsException(
                    " translation array length is "+transLength+" but position : "+ position);
        }

        String[] tagsArr = new String[transLength+1];

        if (tags != null){
            String[] crossArr = UtilsKt.string2Array(tags);
            for (int i = 0; i < crossArr.length; i++ ){
                tagsArr[i] = crossArr[i];
            }
        }

        tagsArr[position+1] = tag;

        tags = UtilsKt.array2String(tagsArr);

    }

}
