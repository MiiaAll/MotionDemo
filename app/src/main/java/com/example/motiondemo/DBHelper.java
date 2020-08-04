package com.example.motiondemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    SQLiteDatabase db;
    private HashMap hp;
    private static DBHelper sInstance;
    static Handler messageHandler;

    public static final String DATABASE_NAME = "sensorRecord.db";
    private static final int DATABASE_VERSION = 1;
    public String databasePath = "";

    //Data table structure
    public static final String DATA_TABLE_NAME = "data";
    public static final String DATA_TIME = "time";
    public static final String DATA_ACCX = "accX";
    public static final String DATA_ACCY = "accY";
    public static final String DATA_ACCZ = "accZ";


    private static final String DATA_TABLE_STRUCTURE =
            " (" + DATA_TIME + " INTEGER," +
                    DATA_ACCX + " REAL," +
                    DATA_ACCY + " REAL," +
                    DATA_ACCZ + " REAL" +
                    ")";

    private static final String DATA_TABLE_CREATE =
            "CREATE TABLE " + DATA_TABLE_NAME + DATA_TABLE_STRUCTURE;

    /**
     The static getInstance() method ensures that only one DatabaseHelper will ever exist at any given time.
     If the sInstance object has not been initialized, one will be created.
     If one has already been created then it will simply be returned.
     You should not initialize your helper object using with new DatabaseHelper(context)!
     Instead, always use DatabaseHelper.getInstance(context), as it guarantees that only
     one database helper will exist across the entire application’s lifecycle.
     */
    public static synchronized DBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
            Log.d(TAG, "New DBHelper created");
        }

        return sInstance;
    }


    public static synchronized DBHelper getInstance(Context context, Handler handler) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
            Log.d(TAG, "New DBHelper created");
        }

        messageHandler = handler;
        return sInstance;
    }

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called");
        //Create persistent tables
        db.execSQL(DATA_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade called");

        //Drop persistent tables
        db.execSQL("DROP TABLE IF EXISTS " + DATA_TABLE_NAME);

        //Recreate tables
        onCreate(db);
    }

    public void closeDB(){
        if (db != null){
            Log.d(TAG, "closeDB: closing db");
            db.close();
            db = null;
        }

        if (sInstance != null){
            Log.d(TAG, "closeDB: closing DBHelper instance");
            sInstance.close();
            sInstance = null;
        }
    }

    public void insertData(long time, float acc_x, float acc_y, float acc_z) throws SQLException {

        ContentValues sensorValues = new ContentValues();
        sensorValues.put(DATA_TIME, time);
        sensorValues.put(DATA_ACCX, acc_x);
        sensorValues.put(DATA_ACCY, acc_y);
        sensorValues.put(DATA_ACCZ, acc_z);
        Log.d("db","time: " + String.valueOf(time) + " stored");
        db.insert(DATA_TABLE_NAME,null,sensorValues);

        //db.insertOrThrow(SUBJECTS_TABLE_NAME_TEMP, null, subjectData);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DATA_TABLE_NAME);
        return numRows;
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ DATA_TABLE_NAME);
        db.close();
        //Log.d("tag","can't delete yet!");
    }

}
