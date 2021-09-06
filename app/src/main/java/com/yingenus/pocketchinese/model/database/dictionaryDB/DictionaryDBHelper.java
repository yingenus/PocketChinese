package com.yingenus.pocketchinese.model.database.dictionaryDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public class DictionaryDBHelper extends OrmLiteSqliteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "dictionaryDB.db";


    public DictionaryDBHelper(Context context){
        super(context,DB_NAME, null, VERSION);
    }

    public DictionaryDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, DB_NAME, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
