package com.yingenus.pocketchinese.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.yingenus.pocketchinese.model.database.pocketDB.PocketBaseHelper;

public class PocketDBOpenManger {

    @VisibleForTesting
    private static volatile SQLiteOpenHelper helper;

    @VisibleForTesting
    private static int initCount = 0;

    public static void setHelper(PocketBaseHelper helper){
        PocketDBOpenManger.helper = helper;
    }

    public static SQLiteOpenHelper getHelper(Context context){
        Log.i("pocketDb","request pocketDb");
        if (PocketDBOpenManger.helper == null){
            Log.i("pocketDb","open pocketDb");
            if (context!= null){
                helper = new PocketBaseHelper(context);
            }
            else {
                throw new RuntimeException("Cant create new helper because context is null");
            }
        }

        initCount++;
        return helper;
    }

    public static void releaseHelper(){
        initCount--;
        Log.i("pocketDb","try to close pocketDb");
        if (initCount == 0) {
            if (helper != null) {

                helper.close();
                helper = null;
                Log.i("pocketDb","closed pocketDb");
            }

        }
        if (initCount < 0){
            Log.i("pocketDb","to many release request");
        }
    }




}
