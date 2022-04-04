package com.yingenus.pocketchinese.domain.entitiys.database.dictionaryDB;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

public class ToneDaoImpl extends BaseDaoImpl<PinTone,String> {
    public ToneDaoImpl(ConnectionSource connection) throws SQLException {
        super(connection,PinTone.class);
    }

    public List<PinTone> getAll() throws SQLException{
        return queryForAll();
    }

}
