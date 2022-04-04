package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class ExampleDaoImpl extends BaseDaoImpl<Example, String> {
    public ExampleDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Example.class);
    }




}
