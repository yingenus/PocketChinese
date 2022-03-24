package com.yingenus.pocketchinese.data.local.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public class ExamplesDBHelper extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME = "examplesDB.db";


    public ExamplesDBHelper(Context context,String databaseName, int userVersion){
        super(context,databaseName, null, userVersion);
    }

    public ExamplesDBHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
