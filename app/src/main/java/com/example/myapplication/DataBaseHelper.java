package com.example.myapplication;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    public DataBaseHelper(Context context, String dbPath) {
        super(context, dbPath, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("CREATING TABLE :", "SUCCESS");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        Log.d("UPGRADING TABLE :", "SUCCESS");

        onCreate(database);
    }
}

