package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable( tableName = "links")
public class Links {
    public static final String WORD_ID_FIELD_NAME = "word_id";
    public static final String EXAMPLES_IDS_FIELD_NAME="exmpl_ids";

    @DatabaseField(canBeNull = false, columnName = WORD_ID_FIELD_NAME)
    String wordId;
    @DatabaseField(canBeNull = false, columnName = EXAMPLES_IDS_FIELD_NAME)
    String ids;

    public Links(){

    }

    public Links(int wordId, String examplIds){
        this.wordId = Integer.toString(wordId);
        this.ids = examplIds;
    }

    public Links(int wordId, int[] ids){
        this(wordId, UtilsKt.array2String(toStringArray(ids)));
    }

    public int getWordId() {
        return Integer.parseInt(wordId);
    }

    public int[] getExamplIds(){
        return toIntArray(UtilsKt.string2Array(ids));
    }

    private static String[] toStringArray(int[] args){
        String[] out = new String[args.length];

        for (int i = 0; i<args.length;i++){
            out[i] = Integer.toString(args[i]);
        }

        return out;
    }

    private static int[] toIntArray(String[] args){
        int[] out = new int[args.length];

        for (int i = 0; i<args.length;i++){
            out[i] = Integer.parseInt(args[i]);
        }

        return out;
    }

}
