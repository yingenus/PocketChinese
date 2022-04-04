package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class LinksDaoImpl extends BaseDaoImpl<Links,String> {
    public LinksDaoImpl(ConnectionSource connection) throws SQLException {
        super(connection,Links.class);
    }



    public List<Links> findChinCharInColumn(String query, String columnName) throws SQLException {
        if (!containsColumn(columnName)){
            throw new IllegalArgumentException(" ChinChar not contain "+columnName+" column");
        }

        PreparedQuery<Links> preparedQuery = queryBuilder().where().eq(columnName, query).prepare();

        List<Links> results= query(preparedQuery);

        return results;
    }

    private boolean containsColumn(String column){
        return column.equals(Links.WORD_ID_FIELD_NAME) || column.equals(Links.EXAMPLES_IDS_FIELD_NAME);
    }
}
