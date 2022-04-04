package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;


import java.sql.SQLException;
import java.util.List;

import kotlin.Pair;

public class ChinCharDaoImpl extends BaseDaoImpl<ChinChar,String> {
    public ChinCharDaoImpl(ConnectionSource connection) throws SQLException {
        super(connection,ChinChar.class);
    }

    public String[] getAllTranslations() throws SQLException {
        return getResultForSingleColumn(ChinChar.TRANSLATION_FIELD_NAME);
    }
    public String[] getAllPinyin()throws SQLException{
        return getResultForSingleColumn(ChinChar.PINYIN_FIELD_NAME);
    }
    public String[] getAllChin()throws SQLException{
        return getResultForSingleColumn(ChinChar.WORD_FIELD_NAME);
    }

    public Pair<Integer, String>[] getAllTranslationsWithID() throws SQLException {
        return getResultForSingleColumnWithId(ChinChar.TRANSLATION_FIELD_NAME);
    }
    public Pair<Integer, String>[] getAllPinyinWithID()throws SQLException{
        return getResultForSingleColumnWithId(ChinChar.PINYIN_FIELD_NAME);
    }
    public Pair<Integer, String>[] getAllChinWithID()throws SQLException{
        return getResultForSingleColumnWithId(ChinChar.WORD_FIELD_NAME);
    }

    public List<ChinChar> getAll() throws SQLException{
        return queryForAll();
    }

    public List<ChinChar> findChinCharInColumn(String query, String columnName) throws SQLException {
        if (!containsColumn(columnName)){
            throw new IllegalArgumentException(" ChinChar not contain "+columnName+" column");
        }

        PreparedQuery<ChinChar> preparedQuery = queryBuilder().where().eq(columnName, query).prepare();

        List<ChinChar> results= query(preparedQuery);

        return results;
    }

    private String[] getResultForSingleColumn(String columnName)throws SQLException{
        List<String[]> resultsList = queryBuilder().selectColumns(columnName)
                .queryRaw().getResults();

        String[] results = new String[0];

        if (resultsList.size()!=0){
            results = new String[resultsList.size()];
            for(int i = 0; i<resultsList.size();i++){
                results[i] = resultsList.get(i)[0];
            }
        }
        return results;
    }

    private Pair<Integer,String>[] getResultForSingleColumnWithId(String columnName) throws SQLException {
        List<String[]> resultsList = queryBuilder().selectColumns(columnName, ChinChar.ID_FIELD_NAME)
                .queryRaw().getResults();

        Pair<Integer,String>[] results = new Pair[0];

        if (resultsList.size()!=0){
            results = new Pair[resultsList.size()];
            for(int i = 0; i<resultsList.size();i++){
                results[i] = new Pair<Integer,String>(Integer.parseInt(resultsList.get(i)[1]),resultsList.get(i)[0]);
            }
        }
        return results;
    }

    private boolean containsColumn(String column){
        return column.equals(ChinChar.ID_FIELD_NAME) || column.equals(ChinChar.PINYIN_FIELD_NAME) ||
                column.equals(ChinChar.TAGS_FIELD_NAME) || column.equals(ChinChar.TRANSLATION_EXAMPLES_FIELD_NAME) ||
                column.equals(ChinChar.TRANSLATION_FIELD_NAME) || column.equals(ChinChar.WORD_FIELD_NAME);
    }

}
